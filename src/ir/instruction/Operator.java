package ir.instruction;

public enum Operator {
    Add, Sub, Mul, Div, Mod, Shl, Shr, And, Or,
    Lt, Le, Ge, Gt, Eq, Ne,
    Zext, Bitcast, Trunc,
    Alloca, Load, Store, GEP,
    Phi, MenPhi, LoadDep,
    Br, Call, Ret,
    Not
}
