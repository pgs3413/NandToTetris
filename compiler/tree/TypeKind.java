package tree;

public enum TypeKind {

    INT("int"),CHAR("char"),BOOL("boolean"),VOID("void"),CLASS;

    public String name;
    TypeKind(){
        name = null;
    }
    TypeKind(String name){
        this.name = name;
    }
}
