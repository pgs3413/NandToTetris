package tree;

public enum Op {

    PLUS("+"),SUB("-"),STAR("*"),SLASH("/"),AND("&"),OR("|"),NOT("~"),LT("<"),GT(">"),EQ("=");

    public String name;

    Op(String name){
        this.name = name;
    }
}
