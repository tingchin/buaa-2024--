package ir.values;

import ir.*;
import ir.instruction.*;
import ir.instruction.Terminator.Br;
import ir.instruction.Terminator.Call;
import ir.instruction.Terminator.Ret;
import ir.instruction.mem.Store;
import ir.types.FunctionType;
import ir.types.IrType;
import ir.types.LabelType;
import ir.types.VoidType;
import utils.MyList;
import utils.MyNode;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    private MyList<Instruction, BasicBlock> instructions;
    private MyNode<BasicBlock, Function> node;
    private List<BasicBlock> prev;
    private List<BasicBlock> next;

    public BasicBlock(Function function) {
        super(String.valueOf(REGNUM++), new LabelType());
        this.instructions = new MyList<>(this);
        this.node = new MyNode<>(this);
        this.prev = new ArrayList<>();
        this.next = new ArrayList<>();
        this.node.insertAtEnd(function.getList());
    }

    public MyList<Instruction, BasicBlock> getInstructions() {
        return instructions;
    }

    public void setInstructions(MyList<Instruction, BasicBlock> instructions) {
        this.instructions = instructions;
    }

    public MyNode<BasicBlock, Function> getNode() {
        return node;
    }

    public void setNode(MyNode<BasicBlock, Function> node) {
        this.node = node;
    }

    public List<BasicBlock> getPrev() {
        return prev;
    }

    public void setPrev(List<BasicBlock> prev) {
        this.prev = prev;
    }

    public List<BasicBlock> getNext() {
        return next;
    }

    public void setNext(List<BasicBlock> next) {
        this.next = next;
    }

    public void addPrev(BasicBlock bb) {
        this.prev.add(bb);
    }

    public void addNext(BasicBlock bb) {
        this.next.add(bb);
    }

    public Function getParent() {
        return this.node.getParent().getValue();
    }

    public void refreshReg() {
        for (MyNode<Instruction, BasicBlock> instruction : this.instructions) {
            Instruction inst = instruction.getValue();
            if (!(inst instanceof Store || inst instanceof Br || inst instanceof Ret ||
                    (inst instanceof Call && ((FunctionType) inst.getOperands().get(0).getType()).getReturnType() instanceof VoidType))) {
                inst.setName("%" + REGNUM++);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (MyNode<Instruction, BasicBlock> instruction : this.instructions) {
            s.append("\t").append(instruction.getValue().toString()).append("\n");
        }
        return s.toString();
    }
}
