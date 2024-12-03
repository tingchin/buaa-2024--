package ir.values;

public class Use {
    private User user;
    private Value value;
    private int pos;

    public Use(User user, Value value, int num) {
        this.user = user;
        this.value = value;
        this.pos = num;
    }

    public User getUser() {
        return user;
    }

    public Value getValue() {
        return value;
    }

    public int getNum() {
        return pos;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setValue(Value value) {
        this.value = value;
    }

}
