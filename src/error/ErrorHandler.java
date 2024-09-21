package error;


import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    // 单例设计模式
    private static final ErrorHandler instance = new ErrorHandler();

    // errors 列表
    private List<Error> errors = new ArrayList<>();

    public static ErrorHandler getInstance() {
        return instance;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
