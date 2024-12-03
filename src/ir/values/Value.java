package ir.values;

import ir.Module;
import ir.types.IrType;
import ir.SlotTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Value {
    private final Module module = Module.getInstance();
    private String name;
    private IrType type;
    private List<Use> useList = new ArrayList<>();
    public static int REGNUM = 0;

    public void addUse(User user, int num) {
        useList.add(new Use(user, this, num));
    }

    public Value(String name, IrType type) {
        this.type = type;
        this.name = name;
    }

    public Module getModule() {
        return module;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IrType getType() {
        return type;
    }
    

    public List<Use> getUseList() {
        return useList;
    }

    public void setUseList(List<Use> useList) {
        this.useList = useList;
    }

    public void setType(IrType type) {
        this.type = type;
    }

    public void addUse(Use use) {
        this.useList.add(use);
    }

    public void removeUseByUser(User user) {
        this.useList.removeIf(use -> use.getUser() == user);
    }

    public void remove(Use use) {
        this.useList.remove(use);
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value value)) return false;
        return Objects.equals(type, value.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    public void replaceUse(Value value) {
        List<Use> tmp = new ArrayList<>(useList);
        for (Use use : tmp) {

        }
    }
}
