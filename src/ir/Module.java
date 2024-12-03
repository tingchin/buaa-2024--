package ir;

import ir.instruction.Instruction;
import ir.types.IrType;
import ir.values.BasicBlock;
import ir.values.Function;
import ir.values.GlobalVar;
import ir.values.Value;
import utils.MyList;
import utils.MyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Module {
    private static final Module module = new Module();
    private List<GlobalVar> globalVars = new ArrayList<>();
    private MyList<Function, Module> functions;
    private HashMap<Integer, Instruction> instructions;

    private Module() {
        this.globalVars = new ArrayList<>();
        this.functions = new MyList<>(this);
        this.instructions = new HashMap<>();
    }

    public static Module getInstance() {
        return module;
    }

    public void addInstruction(int handle, Instruction instruction) {
        this.instructions.put(handle, instruction);
    }

    public MyList<Function, Module> getFunctions() {
        return functions;
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }


    public void refreshRegNum() {
        for (MyNode<Function, Module> function : functions) {
            Value.REGNUM = 0;
            function.getValue().refreshArgReg();
            if (!function.getValue().isLibraryFunction()) {
                for (MyNode<BasicBlock, Function> basicBlock : function.getValue().getList()) {
                    if (basicBlock.getValue().getInstructions().isEmpty()) {
                        BuildFactory.getInstance().checkBlockEnd(basicBlock.getValue());
                    }
                    basicBlock.getValue().setName(String.valueOf(Value.REGNUM++));
                    basicBlock.getValue().refreshReg();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVar globalVar : globalVars) {
            sb.append(globalVar.toString()).append("\n");
        }
        if (!globalVars.isEmpty()) {
            sb.append("\n");
        }
        refreshRegNum();
        for (MyNode<Function, Module> function : functions) {
            if (function.getValue().isLibraryFunction()) {
                sb.append("declare ").append(function.getValue().toString()).append("\n\n");
            } else {
                sb.append("define dso_local ").append(function.getValue().toString()).append("{\n");
                for (MyNode<BasicBlock, Function> basicBlock : function.getValue().getList()) {
                    if (basicBlock != function.getValue().getList().getHead()) {
                        sb.append("\n");
                    }
                    sb.append(";<label>:").append(basicBlock.getValue().getName()).append(":\n").append(basicBlock.getValue().toString());
                }
                sb.append("}\n\n");
            }
        }
        return sb.toString();
    }
}
