package sym;

import tree.TypeKind;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: type 类型检查 类型转换
 */
public class Type {

    public static Type intType = new Type(TypeKind.INT);
    public static Type boolType = new Type(TypeKind.BOOL);
    public static Type voidType = new Type(TypeKind.VOID);

    public TypeKind typeKind;

    public Type(TypeKind typeKind){
        this.typeKind = typeKind;
    }


    public static class ClassType extends Type {

        public Symbol.ClassSymbol symbol;
        public ArrayType arrayType;

        public ClassType(Symbol.ClassSymbol symbol){
            super(TypeKind.CLASS);
            this.symbol = symbol;
            arrayType = new ArrayType(this);
        }

    }

    public static class ArrayType extends Type {


        public static ArrayType intArrayType = new ArrayType(intType);
        public static ArrayType boolArrayType = new ArrayType(boolType);

        public Type itemType;

        public ArrayType(Type itemType){
            super(TypeKind.Array);
            this.itemType = itemType;
        }

    }


}

