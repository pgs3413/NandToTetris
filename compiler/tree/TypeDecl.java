package tree;

import xml.Node;
import xml.ValueNode;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: type decl
 */
public class TypeDecl implements Tree{

    public static TypeDecl intType = new TypeDecl(TypeKind.INT);
    public static TypeDecl boolType = new TypeDecl(TypeKind.BOOL);
    public static TypeDecl voidType = new TypeDecl(TypeKind.VOID);
    public static TypeDecl intArrayType = new TypeDecl("int", TypeKind.Array);
    public static TypeDecl boolArrayType = new TypeDecl("boolean", TypeKind.Array);


    public String name;
    public TypeKind typeKind;

    public TypeDecl(String name, TypeKind typeKind){
        this.name = name;
        this.typeKind = typeKind;
    }

    public TypeDecl(TypeKind typeKind){
        this.typeKind = typeKind;
        this.name = typeKind.name;
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypeDecl(this);
    }
    @Override
    public Node toXml() {
        String s;
        if(typeKind == TypeKind.Array){
            s = "Array[" + name + "]";
        }else {
            s = name;
        }
        return ValueNode.of("type", s);
    }

}
