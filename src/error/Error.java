package error;


public class Error implements Comparable<Error>{
    @Override
    public int compareTo(Error o) {
        return this.line - o.line;
    }

    private ErrorType errorType;
    private int line;

    public Error() {
    }

    public Error(ErrorType errorType, int line) {
        this.errorType = errorType;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return (line + " " + errorType);
    }

}
