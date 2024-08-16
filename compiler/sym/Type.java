package sym;

import tree.TypeKind;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: type 类型检查 类型转换
 */
public class Type {

    public static Type intType = new Type(TypeKind.INT);
    public static Type charType = new Type(TypeKind.CHAR);
    public static Type boolType = new Type(TypeKind.BOOL);
    public static Type voidType = new Type(TypeKind.VOID);

    public Symbol sym;
    public TypeKind typeKind;

    public Type(TypeKind typeKind){
        this.typeKind = typeKind;
    }

    public Type(TypeKind typeKind, Symbol symbol){
        this.typeKind = typeKind;
        this.sym = symbol;
    }
}

