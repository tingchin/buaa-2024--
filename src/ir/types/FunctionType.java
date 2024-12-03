package ir.types;

import java.util.ArrayList;
import java.util.List;

public class FunctionType implements IrType {
    private List<IrType> parameters;
    private IrType returnType;

    public IrType getReturnType() {
        return returnType;
    }

    public List<IrType> getParameters() {
        return parameters;
    }

    public FunctionType() {
        parameters = new ArrayList<>();
    }

    public FunctionType(IrType returnType) {
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        arrayTypeNoLength();

    }

    public FunctionType(IrType returnType, List<IrType> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
        arrayTypeNoLength();
    }

    public void arrayTypeNoLength() {
        List<Integer> target = new ArrayList<>();
        for (IrType parameter : parameters) {
            if (parameter instanceof ArrayType) {
                if (((ArrayType) parameter).getLength() == -1) {
                    target.add(parameters.indexOf(parameter));
                }
            }

        }

        for (Integer i : target) {
            parameters.set(i, new PointerType(((ArrayType) parameters.get(i)).getElementType()));
        }
    }
}
