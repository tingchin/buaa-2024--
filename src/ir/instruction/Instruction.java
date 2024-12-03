package ir.instruction;
import ir.instruction.Terminator.Br;
import ir.instruction.Terminator.Ret;
import ir.types.IrType;
import ir.values.BasicBlock;
import ir.values.User;
import utils.MyNode;


public abstract class Instruction extends User {
    private Operator op;
    private MyNode<Instruction, BasicBlock> node;
    private int handler;
    private static int HANDLER = 0;

    public Instruction(IrType type, Operator op, BasicBlock basicBlock) {
        super("", type);
        this.op = op;
        this.node = new MyNode<>(this);
        this.handler = HANDLER++;
        this.getModule().addInstruction(handler, this);
    }

    public Operator getOperator() {
        return op;
    }

    public MyNode<Instruction, BasicBlock> getNode() {
        return node;
    }

    public int getHandler() {
        return handler;
    }

    public static int getHANDLER() {
        return HANDLER;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public void setNode(MyNode<Instruction, BasicBlock> node) {
        this.node = node;
    }


    public static void setHANDLER(int HANDLER) {
        Instruction.HANDLER = HANDLER;
    }

    public void addInstToBlock(BasicBlock basicBlock) {
        if (basicBlock.getInstructions().getTail() == null ||
                (!(basicBlock.getInstructions().getTail().getValue() instanceof Br) &&
                        !(basicBlock.getInstructions().getTail().getValue() instanceof Ret))) {
            this.getNode().insertAtEnd(basicBlock.getInstructions());
        } else {
            this.removeUseFromOperands();
        }
    }

    public void addInstToBlockBegin(BasicBlock block) {
        this.getNode().insertAtBegin(block.getInstructions());

    }


}
