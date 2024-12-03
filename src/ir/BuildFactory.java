package ir;

import ir.instruction.Binary;
import ir.instruction.Convert;
import ir.instruction.Operator;
import ir.instruction.Terminator.Br;
import ir.instruction.Terminator.Call;
import ir.instruction.Terminator.Ret;
import ir.instruction.mem.Alloca;
import ir.instruction.mem.GEP;
import ir.instruction.mem.Load;
import ir.instruction.mem.Store;
import ir.types.ArrayType;
import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.IrType;
import ir.values.*;

import java.util.List;

public class BuildFactory {
    private static final BuildFactory instance = new BuildFactory();

    public static BuildFactory getInstance() {
        return instance;
    }

    public BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public void checkBlockEnd(BasicBlock basicBlock) {
        IrType retType = ((FunctionType) basicBlock.getNode().getParent().getValue().getType()).getReturnType();
        if (!basicBlock.getInstructions().isEmpty()) {
            Value lastInst = basicBlock.getInstructions().getTail().getValue();
            if (lastInst instanceof Ret || lastInst instanceof Br) {
                return;
            }
        }
        if (retType instanceof IntegerType) {
            buildRet(basicBlock, ConstInt.ZERO);
        } else {
            buildRet(basicBlock);
        }
    }

    public Function buildFunction(String name, IrType returnType, List<IrType> parameterTypes) {
        return new Function(name, getFunctionType(returnType, parameterTypes), false);
    }

    public Function buildLibrary(String name, IrType returnType, List<IrType> parameterTypes) {
        return new Function(name, getFunctionType(returnType, parameterTypes), true);
    }

    public FunctionType getFunctionType(IrType returnType, List<IrType> parameterTypes) {
        return new FunctionType(returnType, parameterTypes);
    }

    public List<Value> getArguments(Function function) {
        return function.getArguments();
    }

    public Value buildZext(Value value, BasicBlock basicBlock, IrType targetType) {
        if (value instanceof ConstInt) {
            return new ConstInt(((ConstInt) value).getValue());
        }
        Convert tmp = new Convert(basicBlock, Operator.Zext, value, targetType);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Value buildTrunc(Value value, BasicBlock basicBlock, IrType targetType) {
        if (value instanceof ConstInt) {
            return new ConstChar(((ConstInt) value).getValue());
        }
        Convert tmp = new Convert(basicBlock, Operator.Trunc, value, targetType);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Convert buildBitcast(Value value, BasicBlock basicBlock, IrType targetType) {
        Convert tmp = new Convert(basicBlock, Operator.Bitcast, value, targetType);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Binary buildConvToI1(Value val, BasicBlock basicBlock) {
        Binary tmp = new Binary(basicBlock, Operator.Ne, val, getConstInt(0));
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public GlobalVar buildGlobalVar(String name, IrType type, boolean isConst, Value value) {
        return new GlobalVar(name, type, isConst, value);
    }

    public Alloca buildVar(BasicBlock basicBlock, Value value, boolean isConst, IrType allocaType) {
        Alloca tmp = new Alloca(basicBlock, isConst, allocaType);
        tmp.addInstToBlock(basicBlock);
        if (value != null) {
            buildStore(basicBlock, tmp, value);
        }
        return tmp;
    }

    public ConstInt getConstInt(int value) {
        return new ConstInt(value);
    }

    public ConstChar getConstChar(int value) {
        return new ConstChar(value);
    }

    public Load buildLoad(BasicBlock basicBlock, Value pointer) {
        Load tmp = new Load(basicBlock, pointer);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Store buildStore(BasicBlock basicBlock, Value ptr, Value value) {
        Store tmp = new Store(basicBlock, ptr, value);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Ret buildRet(BasicBlock basicBlock) {
        Ret tmp = new Ret(basicBlock);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Ret buildRet(BasicBlock basicBlock, Value ret) {
        Ret tmp = new Ret(basicBlock, ret);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Binary buildBinary(BasicBlock basicBlock, Operator op, Value lhs, Value rhs) {
        Binary tmp = new Binary(basicBlock, op, lhs, rhs);
        if (op == Operator.And || op == Operator.Or) {
            tmp = buildBinary(basicBlock, Operator.Ne, tmp, ConstInt.ZERO);
        }
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Binary buildNot(BasicBlock basicBlock, Value value) {
        return buildBinary(basicBlock, Operator.Eq, value, ConstInt.ZERO);
    }

    public Alloca buildArray(BasicBlock basicBlock, boolean isConst, IrType arrayType) {
        Alloca tmp = new Alloca(basicBlock, isConst, arrayType);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public GlobalVar buildGlobalArray(String name, IrType type, boolean isConst) {
        Value tmp = new ConstArray(type, ((ArrayType) type).getElementType(), ((ArrayType) type).getLength());
        return new GlobalVar(name, type, isConst, tmp);
    }

    public ArrayType getArrayType(IrType eleType, int length) {
        return new ArrayType(eleType, length);
    }

    public void buildInitArray(Value array, int index, Value value) {
        ((ConstArray)((GlobalVar) array).getValue()).storeValue(index, value);
    }

    public GEP buildGEP(BasicBlock basicBlock, Value pointer, List<Value> indices) {
        GEP tmp = new GEP(basicBlock, pointer, indices);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public List<Value> getFunctionArguments(Function function) {
        return function.getArguments();
    }

    public GEP buildGEP(BasicBlock basicBlock, Value pointer, int offset) {
        GEP tmp = new GEP(basicBlock, pointer, offset);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Call buildCall(BasicBlock basicBlock, Function function, List<Value> args) {
        Call tmp = new Call(basicBlock, function, args);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public Value getConstString(String str) {
        return new ConstString(str);
    }

    public Br buildBr(BasicBlock curBlock, BasicBlock outBlock) {
        Br tmp = new Br(curBlock, outBlock);
        tmp.addInstToBlock(curBlock);
        return tmp;
    }

    public Br buildBr(BasicBlock basicBlock, Value cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        Br tmp = new Br(basicBlock, cond, trueBlock, falseBlock);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }
}
