package error;


import utils.IOUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorHandler {
    // 单例设计模式
    private static final ErrorHandler instance = new ErrorHandler();

    // errors 列表
    private List<Error> errors = new ArrayList<>();

    private static boolean haveError = false;

    public static ErrorHandler getInstance() {
        return instance;
    }


    public void addError(Error error) {
        haveError = true;
        instance.errors.add(error);
    }
    public boolean isHaveError() {
        return haveError;
    }

    public void printErrors(String path) {
        StringBuilder sb = new StringBuilder();
        Collections.sort(errors);
        for (Error error : errors) {
            sb.append(error.toString()).append("\n");
        }
        IOUtils.writeFile(path, sb.toString());
    }
}
