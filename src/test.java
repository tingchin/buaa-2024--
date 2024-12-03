import ir.BuildFactory;

/**
 * @author
 * @Description:
 * @date 2024/11/25 18:38
 */
public class Test {
    public static void main(String[] args) {
        int value = BuildFactory.getInstance().getConstInt(22).getValue();
    }
}
