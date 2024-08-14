package tree;

import xml.Node;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: type decl
 */
public class TypeDecl implements Tree{

    public static TypeDecl intType = new TypeDecl(TypeKind.INT);
    public static TypeDecl charType = new TypeDecl(TypeKind.CHAR);
    public static TypeDecl boolType = new TypeDecl(TypeKind.BOOL);
    public static TypeDecl voidType = new TypeDecl(TypeKind.VOID);

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
        if(typeKind != TypeKind.CLASS){
            return Node.ValueNode.of("keyword", name);
        }
        return Node.ValueNode.of("identifier", name);
    }

}
