package base.exception;

/*
    全局异常处理
*/
public class MyException extends Exception {
    private static final long serialVersionUID = 1L;
    // 异常信息
    public String message;

    public MyException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
