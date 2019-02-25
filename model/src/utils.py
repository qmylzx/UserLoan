import time
import numpy as np
import pandas as pd
import lightgbm as lgb
from scipy import stats
from functools import wraps
from sklearn import metrics
from sklearn.feature_selection import SelectKBest, f_classif


def get_data(tabel_names):
    """
    :function periodic time extraction
    :param tabel_names：Reference sets for multiple dataFrame objects
    :return：no return
    """
    for value in tabel_names:
        value['month'] = value['time'].apply(lambda x: int(time.strftime("%m", time.localtime(x))))
        value['day'] = value['time'].apply(lambda x: int(time.strftime("%d", time.localtime(x))))
        value.drop('time', axis=1, inplace=True)


def timer(func_name):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            t0 = time.time()
            retval = func(*args, **kwargs)
            usage_time = time.time() - t0
            print(f'{func_name} usage time: {usage_time}')
            return retval

        return wrapper

    return decorator


def get_feature_importance(X_train, y_train, feat_labels):
    '''
    function: use LightGBM model to evaluate the importance of all features
    :param X_train: an array of features for training
    :param y_train: sample label array for training
    :param feat_labels: attribute names
    :return: feature list which is more important
    '''
    lgb_clf = lgb.LGBMClassifier(boosting_type='gbdt', num_leaves=40, max_depth=-1, learning_rate=0.03,
                                 n_estimators=500,
                                 max_bin=425, objective='binary', is_unbalance=True, min_child_weight=5,
                                 min_child_samples=10,
                                 subsample=0.8, subsample_freq=1, colsample_bytree=1, reg_alpha=3, reg_lambda=5,
                                 seed=1996,
                                 n_jobs=-1, silent=True)
    lgb_clf.fit(X_train, y_train)
    lgb_importance = pd.DataFrame({'features': feat_labels, 'importance': lgb_clf.feature_importances_}).sort_values(
        'importance', ascending=False)
    select_features = lgb_importance[lgb_importance['importance'] > 10]['features'].values
    print(30 * '-' + "get importance successfully" + 30 * '-')
    return select_features


def variance_test(X_train, y_train, feat_labels):
    '''
    :param X_train: an array of features for training
    :param y_train: sample label array for training
    :param feat_labels: attribute names
    :return: dataFrame of variance test result
    '''
    selector = SelectKBest(f_classif, k=10)
    selector.fit(X_train, y_train)
    var_df = pd.DataFrame({'features': feat_labels, 'f_classif': selector.scores_}) \
        .sort_values('f_classif', ascending=False)
    return var_df


def get_mode(arr):
    '''
    function: get a mode of the numpy array
    :param arr: numpy array
    :return: the mode of numpy array
    '''
    return stats.mode(arr)[0][0]


def get_cv(arr):
    '''
    :param arr: numpy array
    :return: the coefficient of variation of arr
    '''
    return arr.mean() / arr.std()


def ks(y_true, y_predict_proba):
    '''
    :param y_true: samples' label
    :param y_predict_proba: prediction probability of samples' classification
    :return: KS statistics
    '''
    # false_postive, true_postive, threshold, label --> postive
    fpr, tpr, thres = metrics.roc_curve(y_true, y_predict_proba, pos_label=1)
    return 'ks', abs(fpr - tpr).max(), False


def get_ptp(arr):
    '''
    :param arr: numpy array
    :return: range
    '''
    return arr.max() - arr.min()
