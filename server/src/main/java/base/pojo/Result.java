package base.pojo;
import java.util.HashMap;
import java.util.Map;
/*
{   "code": "000000",
    "message":"   ",
    "data": { "code": "000000",    "message":"   ",    "data":data }
}
 */
public class Result {
    private String code; //状态码 成功000000 失败999999
    private String message; //错误信息
    //返回数据类型，链式结构
    private Map<String,Object> data = new HashMap<String, Object>();

    public static Result success(){
        Result result = new Result();
        result.setCode("000000");
        result.setMessage("操作成功");
        return result;
    }
    public static Result repeat(){
        Result result = new Result();
        result.setCode("111111");
        result.setMessage("存在重复！");
        return result;
    }

    public Result add(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
