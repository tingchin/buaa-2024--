package ir.values;

import ir.types.IrType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class User extends Value {
    protected List<Value> operands;


    public User(String name, IrType type) {
        super(name, type);

        this.operands = new ArrayList<>();
    }

    public List<Value> getOperands() {
        return operands;
    }

    public Value getOperand(int index) {
        return operands.get(index);
    }

    public void setOperands(List<Value> operands) {
        this.operands = operands;
    }


    public void setOperands(int pos, Value operand) {
        if (pos >= operands.size()) {
            return;
        }
        this.operands.set(pos, operand);
        if (operand != null) {
            operand.addUse(new Use(this, operand, pos));
        }
    }

    public void addOperand(Value operand) {
        operands.add(operand);
        if (operand != null) {
            operand.addUse(new Use(this, operand, operands.size() - 1));
        }
    }

    public void removeUseFromOperands() {
        if (operands == null) {
            return;
        }
        for (Value operand : operands) {
            if (operand != null) {
                operand.removeUseByUser(this);
            }
        }
    }

    public void removeNumber(HashSet<Integer> idx) {
        removeUseFromOperands();
        List<Value> tmp = new ArrayList<>(operands);
        operands.clear();
        for (int i = 0; i < tmp.size(); ++i) {
            tmp.get(i).addUse(new Use(this, tmp.get(i), operands.size()));
            this.operands.add(tmp.get(i));
        }
    }
    public void replaceOperand(int num, Value newOperand) {
        Value operand = operands.get(num);
        this.setOperands(num, newOperand);
        if (operand != null &&!this.operands.contains(operand)) {
            operand.removeUseByUser(this);
        }
    }

    public void replaceOperand(Value old, Value newOperand) {
        old.removeUseByUser(this);
        for (int i = 0; i < operands.size(); i++) {
            if (operands.get(i).equals(old)) {
                operands.set(i, newOperand);
            }
        }
    }
}
