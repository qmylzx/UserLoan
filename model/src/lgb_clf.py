import warnings
import pandas as pd
import numpy as np
import lightgbm as lgb
from src.utils import get_mode
from src.utils import get_cv
from src.utils import get_ptp
from src.utils import get_feature_importance
from src.utils import ks
from sklearn.model_selection import StratifiedKFold
from sklearn.preprocessing import LabelEncoder

warnings.filterwarnings('ignore')


def load_data():
    print(30 * '-' + "load data ing" + 30 * '-')
    # User Basic Information Table
    user_info_cloumns = ['user_id', 'sex', 'occupation', 'education', 'marriage', 'household_type']
    user_info = pd.read_table("../data/train/user_info_train.txt", names=user_info_cloumns, sep=',')
    user_info_test = pd.read_table("../data/test/user_info_test.txt", names=user_info_cloumns, sep=',')

    # Bank Flow Record
    bank_detail = pd.read_table("../data/train/bank_detail_train.txt", sep=',')
    bank_detail.rename(columns={'uid': 'user_id', 'timespan': 'time', 'type': 'deal_type',
                                'amount': 'deal_amount', 'markup': 'wage_type'}, inplace=True)
    bank_detail_columns = ['user_id', 'time', 'deal_type', 'deal_amount', 'wage_type']
    bank_detail_test = pd.read_table("../data/test/bank_detail_test.txt", names=bank_detail_columns, sep=',')

    # User Browse Record Table
    browse_history_columns = ['user_id', 'time', 'browse_behavior', 'browse_subbehavior']
    browse_history = pd.read_table("../data/train/browse_history_train.txt", names=browse_history_columns, sep=',')
    browse_history_test = pd.read_table("../data/test/browse_history_test.txt", names=browse_history_columns, sep=',')

    # Credit Card Bill Record Form
    bill_detail_columns = ['user_id', 'time', 'bank_id', 'previous_bills', 'previous_repayment', 'creditCardQuota',
                           'current_balance', 'current_MinRepay', 'consume_times', 'current_bills', 'adjust_money',
                           'revolving_interest', 'available_money', 'cash_Advance_Limit', 'repayment_status']
    bill_detail = pd.read_table("../data/train/bill_detail_train.txt", names=bill_detail_columns, sep=',')
    bill_detail_test = pd.read_table("../data/test/bill_detail_test.txt", names=bill_detail_columns, sep=',')

    # Lending schedule
    loan_time_columns = ['user_id', 'time']
    loan_time = pd.read_table("../data/train/loan_time_train.txt", names=loan_time_columns, sep=',')
    loan_time_test = pd.read_table("../data/test/loan_time_test.txt", names=loan_time_columns, sep=',')

    # User Overdue Record Table
    overdue_columns = ['user_id', 'isOverdue']
    overdue = pd.read_table("../data/train/overdue_train.txt", names=overdue_columns, sep=',')
    overdue_pred = pd.read_table("../data/test/usersID_test.txt", names=['user_id'], sep=',')

    print(30 * '-' + "load data successfully" + 30 * '-')
    return user_info, user_info_test, bank_detail, bank_detail_test, browse_history, browse_history_test, \
           bill_detail, bill_detail_test, loan_time, loan_time_test, overdue, overdue_pred


def process_time(tabel_list):
    '''
    :param tabel_list: dataFrames list to be process
    :return: no return, processing raw data through reference mechanism
    '''
    for value in tabel_list:
        value['time'] = value['time'] // 86400


# 时间已知 / 放款前 用户浏览行为
def get_browse_feats(user_info, browse_history, overdue, loan_time):
    train_data = pd.merge(user_info, overdue, how='inner', on='user_id')
    browseAndLoan = pd.merge(browse_history, loan_time, how='left', on='user_id')

    # 浏览记录时间 <= 放款时间，时间非0（0代表未知）
    temp = browseAndLoan[(browseAndLoan['time_x'] <= browseAndLoan['time_y']) & (browseAndLoan['time_x'] > 0)]

    # 行为是连续数值表示的类别特征
    # 子行为应该是用户行为更细粒度的划分，所以将两者转化为字符串拼接，作为新特征
    temp['cross_behavior'] = temp['browse_behavior'].astype(str).values + '_' + temp['browse_subbehavior'].astype(
        str).values

    # 对cross_behavior进行自然数编码
    encoder = LabelEncoder()
    encoder.fit(temp['cross_behavior'])
    temp['cross_behavior'] = encoder.transform(temp['cross_behavior'])

    # 每个用户进行了多少次行为统计
    to_merge = temp.groupby('user_id', as_index=False)['browse_behavior'].agg \
        ({'behavior_count': 'count'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    # 每个用户的不同行为数和频率最高的行为
    to_merge = temp.groupby('user_id', as_index=False)['browse_behavior'].agg \
        ({'unique_behavior': 'nunique', 'browse_behavior_mode': get_mode})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    # 每个用户的不同子行为数和频率最高的子行为
    to_merge = temp.groupby('user_id', as_index=False)['browse_subbehavior'].agg \
        ({'unique_subbehavior': 'nunique', 'browse_subbehavior_mode': get_mode})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    # 每个用户的不同交叉行为数和频率最高的交叉行为
    to_merge = temp.groupby('user_id', as_index=False)['cross_behavior'].agg \
        ({'cross_behavior': 'nunique', 'cross_behavior_mode': get_mode})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    return train_data


# 时间已知 / 放款前 银行流水记录
def get_bank_feats(train_data, bank_detail, loan_time):
    bankAndLoan = pd.merge(bank_detail, loan_time, how='left', on='user_id')
    temp = bankAndLoan.loc[(bankAndLoan['time_x'] <= bankAndLoan['time_y']) & (bankAndLoan['time_x'] > 0)]

    # 每个用户的收入情况
    deal_type_0 = temp[temp['deal_type'] == 0].groupby(['user_id'], as_index=False)
    income_analysis = deal_type_0['deal_amount'].agg({'income_count': 'count', 'income_sum': 'sum',
                                                      'income_median': 'median', 'income_std': 'std',
                                                      'income_cv': get_cv})
    train_data = train_data.merge(income_analysis, how='left', on='user_id')

    # 每个用户的支出情况
    deal_type_1 = temp[temp['deal_type'] == 1].groupby(['user_id'], as_index=False)
    expen_analysis = deal_type_1['deal_amount'].agg({'expen_count': 'count', 'expen_sum': 'sum',
                                                     'expen_median': 'median', 'expen_std': 'std',
                                                     'expen_cv': get_cv})
    train_data = train_data.merge(expen_analysis, how='left', on='user_id')

    # 工资性收入
    wage_type_1 = temp[temp['wage_type'] == 1].groupby(['user_id'], as_index=False)
    wage_income = wage_type_1['deal_amount'].agg({'wages_count': 'count', 'wages_sum': 'sum'})
    train_data = train_data.merge(wage_income, how='left', on='user_id')

    # 非工资性收入
    wage_type_0 = temp[(temp['wage_type'] == 0) & (temp['deal_type'] == 0)].groupby(['user_id'], as_index=False)
    non_wage_income = wage_type_0['deal_amount'].agg({'non_wages_sum': 'sum', 'non_wages_median': 'median',
                                                      'non_wages_std': 'std', 'non_wages_cv': get_cv})
    train_data = train_data.merge(non_wage_income, how='left', on='user_id')

    # 平均每笔收入
    train_data['per_income'] = train_data['income_sum'] / train_data['income_count']

    # 平均每笔支出
    train_data['per_expen'] = train_data['expen_sum'] / train_data['expen_count']

    return train_data


# 时间已知 / 放款前 信用卡账单
def get_bill_feats(train_data, bill_detail, loan_time):
    billAndLoan = bill_detail.merge(loan_time, how='left', on='user_id')
    temp = billAndLoan.loc[(billAndLoan['time_x'] <= billAndLoan['time_y']) & (billAndLoan['time_x'] > 0)]

    ccards_num = temp.groupby(['user_id'], as_index=False)['bank_id'].agg({'bank_num': 'nunique'})
    train_data = train_data.merge(ccards_num, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['previous_bills'].agg({'previous_bills_count': 'count',
                                                                                'previous_bills_sum': 'sum',
                                                                                'previous_bills_median': 'median',
                                                                                'previous_bills_std': 'std',
                                                                                'previous_bills_max': 'max',
                                                                                'previous_bills_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['previous_repayment'].agg({'previous_repayment_sum': 'sum',
                                                                                    'previous_repayment_median': 'median',
                                                                                    'previous_repayment_std': 'std',
                                                                                    'previous_repayment_max': 'max',
                                                                                    'previous_repayment_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['creditCardQuota'].agg({'creditCardQuota_mean': 'mean',
                                                                                 'creditCardQuota_max': 'max',
                                                                                 'creditCardQuota_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['current_balance'].agg({'current_balance_mean': 'mean',
                                                                                 'current_balance_sum': 'sum'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['current_MinRepay'].agg({'current_MinRepay_mean': 'mean',
                                                                                  'current_MinRepay_sum': 'sum'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['consume_times'].agg({'consume_times_median': 'median',
                                                                               'consume_times_sum': 'sum',
                                                                               'consume_times_ptp': get_ptp})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['current_bills'].agg({'current_bills_sum': 'sum',
                                                                               'current_bills_median': 'median',
                                                                               'current_bills_std': 'std',
                                                                               'current_bills_max': 'max',
                                                                               'current_bills_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['adjust_money'].agg({'adjust_money_mean': 'mean',
                                                                              'adjust_money_sum': 'sum',
                                                                              'adjust_money_max': 'max',
                                                                              'adjust_money_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['revolving_interest'].agg({'revolving_interest_mean': 'mean',
                                                                                    'revolving_interest_sum': 'sum',
                                                                                    'revolving_interest_max': 'max',
                                                                                    'revolving_interest_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['available_money'].agg({'available_money_sum': 'sum',
                                                                                 'available_money_median': 'median',
                                                                                 'available_money_std': 'std',
                                                                                 'available_money_max': 'max',
                                                                                 'available_money_min': 'min'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    to_merge = temp.groupby(['user_id'], as_index=False)['cash_Advance_Limit'].agg({'cash_Advance_Limit_mean': 'mean',
                                                                                    'cash_Advance_Limit_max': 'max'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')

    # 还款次数 / 统计次数
    repay_proba = temp.groupby(['user_id'], as_index=False)['repayment_status'].agg({'isRepay': 'mean'})
    # 是否有过还款状态为 1 （已还款；可能存在分期还款）
    repay_proba['isRepay'] = repay_proba['isRepay'].apply(lambda x: 1 if x > 0 else 0)
    train_data = train_data.merge(repay_proba, how='left', on='user_id')

    # 上期还款差额 = 上期账单金额 - 上期还款金额
    temp['pre_payBalance'] = temp['previous_bills'] - temp['previous_repayment']
    to_merge = temp.groupby(['user_id'], as_index=False)['pre_payBalance'].agg({'pre_payBalance_meam': 'mean',
                                                                                'pre_payBalance_max': 'max'})
    train_data = train_data.merge(to_merge, how='left', on='user_id')
    return train_data


def nan_process(train_data):
    drop_features = ['wages_count', 'wages_sum', 'non_wages_cv', 'non_wages_std', 'expen_cv', 'income_cv']
    train_data.drop(drop_features, axis=1, inplace=True)

    fill_median = ['expen_count', 'income_count', 'previous_bills_count']

    # 标志缺失
    for feat in fill_median:
        train_data[feat + '_nan'] = train_data[feat].isnull().apply(lambda x: 1 if x == True else 0)

    # 连续整型用中位数填充
    for feat in fill_median:
        train_data[feat] = train_data[feat].fillna(train_data[feat].median())

    fill_mode = ['bank_num', 'cross_behavior', 'unique_subbehavior', 'unique_behavior', 'behavior_count',
                 'browse_behavior_mode', 'browse_subbehavior_mode', 'cross_behavior_mode']

    # 标志缺失
    for feat in fill_mode:
        train_data[feat + '_nan'] = train_data[feat].isnull().apply(lambda x: 1 if x == True else 0)

    # 离散整型用众数填充
    for feat in fill_mode:
        train_data[feat] = train_data[feat].fillna(train_data[feat].mode()[0])

    # 还款状态，缺失值用-1填充，作为一个新类别
    train_data['isRepay'] = train_data['isRepay'].fillna(-1)

    miss = train_data.isnull().sum()
    numeric_feats = miss[miss > 0].keys().values

    # 标志缺失
    for feat in numeric_feats:
        train_data[feat + '_nan'] = train_data[feat].isnull().apply(lambda x: 1 if x == True else 0)

    # 连续数值型使用均值填充
    for feat in numeric_feats:
        train_data[feat] = train_data[feat].fillna(train_data[feat].mean())

    return train_data


def truncate_process(train_data):
    # 部分特征转化为整型，降低存储位数
    to_int_features = ['behavior_count', 'unique_behavior', 'browse_behavior_mode', 'unique_subbehavior',
                       'browse_subbehavior_mode', 'cross_behavior', 'cross_behavior_mode', 'income_count',
                       'expen_count', 'previous_bills_count', 'isRepay', 'bank_num']

    for feat in to_int_features:
        train_data[feat] = train_data[feat].astype(int)

    # 保留小数点后两位
    float_cols = [col for col in train_data.columns if train_data[col].dtypes == 'float64']

    for feat in float_cols:
        train_data[feat] = train_data[feat].round(2)

    return train_data


def get_train_data(user_info, browse_history, bank_detail, loan_time, bill_detail, overdue):
    print(30 * '-' + "get train data ing" + 30 * '-')
    train_data = get_browse_feats(user_info, browse_history, overdue, loan_time)
    train_data = get_bank_feats(train_data, bank_detail, loan_time)
    train_data = get_bill_feats(train_data, bill_detail, loan_time)
    train_data = nan_process(train_data)
    train_data = truncate_process(train_data)
    print(30 * '-' + "process train data successfully" + 30 * '-')
    return train_data


def get_test_data(user_info_test, browse_history_test, bank_detail_test, loan_time_test, bill_detail_test,
                  overdue_pred):
    print(30 * '-' + "get test data ing" + 30 * '-')
    test_data = get_browse_feats(user_info_test, browse_history_test, overdue_pred, loan_time_test)
    test_data = get_bank_feats(test_data, bank_detail_test, loan_time_test)
    test_data = get_bill_feats(test_data, bill_detail_test, loan_time_test)
    test_data = nan_process(test_data)
    test_data = truncate_process(test_data)
    print(30 * '-' + "process test data successfully" + 30 * '-')
    return test_data


def get_result(X_train, y_train, X_test):
    lgb_clf = lgb.LGBMClassifier(boosting_type='gbdt', num_leaves=30, max_depth=-1, learning_rate=0.01,
                                 n_estimators=10000, max_bin=425, objective='binary', is_unbalance=True,
                                 min_data_in_leaf=30, subsample=0.8, subsample_freq=1, colsample_bytree=0.9,
                                 reg_alpha=0.1, reg_lambda=5, seed=1996, n_jobs=-1, silent=True)

    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=1996)
    off_line_pred = np.zeros(X_train.shape[0])
    on_line_pred = np.zeros(X_test.shape[0])

    for i, (train_index, valid_index) in enumerate(skf.split(X_train, y_train)):
        print(i + 1, "Fold")
        lgb_model = lgb_clf.fit(X_train[train_index], y_train[train_index],
                                eval_names=['train', 'valid'],
                                eval_metric=ks,
                                eval_set=[(X_train[train_index], y_train[train_index]),
                                          (X_train[valid_index], y_train[valid_index])],
                                early_stopping_rounds=100)
        off_line_pred = lgb_model.predict_proba(X_train, num_iteration=lgb_clf.best_iteration_)[:, 1]
        on_line_pred += lgb_model.predict_proba(X_test, num_iteration=lgb_clf.best_iteration_)[:, 1]
        print('offLine_KS:', ks(y_train, off_line_pred)[1])
    on_line_pred = on_line_pred / 5
    return off_line_pred, on_line_pred


if __name__ == '__main__':
    # load data
    user_info, user_info_test, bank_detail, bank_detail_test, browse_history, browse_history_test, bill_detail, \
    bill_detail_test, loan_time, loan_time_test, overdue, overdue_pred = load_data()

    # features processing and get train data
    train_data = get_train_data(user_info, browse_history, bank_detail, loan_time, bill_detail, overdue)

    # features processing and get test data
    test_data = get_test_data(user_info_test, browse_history_test, bank_detail_test, loan_time_test,
                              bill_detail_test, overdue_pred)

    # feature importance evaluation
    features = train_data.drop(['user_id', 'isOverdue'], axis=1).columns.values
    temp_train = train_data.drop(['user_id', 'isOverdue'], axis=1).values
    temp_label = train_data['isOverdue'].values
    select_features = get_feature_importance(temp_train, temp_label, features)

    # to numpy array
    X_train = train_data[select_features].values
    y_train = train_data['isOverdue'].values
    X_test = test_data[select_features].values

    # use tree model predict and get result(probability)
    off_line_pred, on_line_pred = get_result(X_train, y_train, X_test)

    # Obtain the possibility of user default in the test set
    overdue_pred['probability'] = on_line_pred
    overdue_pred.set_index('user_id').to_csv("../data/result/overdue_prob.csv")
