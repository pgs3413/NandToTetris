package tree;

public enum TypeKind {

    INT("int"),
    BOOL("boolean"),
    VOID("void"),
    CLASS,
    Array;

    public String name;
    TypeKind(){
        name = null;
    }
    TypeKind(String name){
        this.name = name;
    }
}
