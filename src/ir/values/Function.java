package ir.values;

import ir.Module;
import ir.types.FunctionType;
import ir.types.IrType;
import utils.MyList;
import utils.MyNode;

import java.util.ArrayList;
import java.util.List;

public class Function extends Value {
    private MyList<BasicBlock, Function> list;
    private  MyNode<Function, Module> node;
    private List<Argument> arguments;
    private List<Function> prev;
    private List<Function> next;
    private boolean isLibraryFunction;

    public Function(String name, IrType type, boolean isLibraryFunction) {
        super(name, type);
        REGNUM = 0;
        this.list = new MyList<>(this);
        this.node = new MyNode<>(this);
        this.arguments = new ArrayList<>();
        this.prev = new ArrayList<>();
        this.next = new ArrayList<>();
        this.isLibraryFunction = isLibraryFunction;
        for (IrType parameter : ((FunctionType) type).getParameters()) {
            arguments.add(new Argument(parameter, ((FunctionType) type).getParameters().indexOf(parameter), isLibraryFunction));
        }

        this.node.insertAtEnd(Module.getInstance().getFunctions());

    }


    public MyList<BasicBlock, Function> getList() {
        return list;
    }

    public MyNode<Function, Module> getNode() {
        return node;
    }

    public List<Value> getArguments() {
        return new ArrayList<>(arguments);
    }

    public List<Function> getPrev() {
        return prev;
    }

    public List<Function> getNext() {
        return next;
    }

    public void addPrev(Function function) {
        prev.add(function);
    }

    public void addNext(Function function) {
        next.add(function);
    }

    public boolean isLibraryFunction() {
        return isLibraryFunction;
    }

    public void refreshArgReg() {
        for (Argument argument : arguments) {
            argument.setName("%" + REGNUM++);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(((FunctionType) this.getType()).getReturnType()).append(" @").append(this.getName()).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            str.append(arguments.get(i).getType());
            if (i != arguments.size() - 1) {
                str.append(", ");
            }
        }
        str.append(")");
        return str.toString();
    }

    public static class Argument extends Value {
        private int index;

        public Argument(String name, IrType type, int index) {
            super(name, type);
            this.index = index;
        }

        public Argument(IrType type, int index, boolean isLibraryFunction) {
            super(isLibraryFunction ? "" : "%" + REGNUM++, type);
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return this.getType().toString() + " " + this.getName();
        }

    }
}
