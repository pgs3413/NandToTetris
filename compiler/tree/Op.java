package tree;

public enum Op {

    // unary

    NEG("-", "neg"), // int
    NOT("~", "not"), // int
    BOOLNOT("!", "not"), // bool

    //binary

    // int op int -> int
    PLUS("+", "add"),
    SUB("-", "sub"),
    MUL("*", "Math.multiply"),
    DIV("/", "Math.divide"),
    AND("&", "and"),
    OR("|", "or"),

    // int op int -> bool
    LT("<", "lt"),
    GT(">", "gt"),
    LTEQ("<="),
    GTEQ(">="),
    // x op x -> bool (x : int boolean sameclass array)
    NOTEQ("!="),
    EQ("==", "eq"),

    // bool op bool -> bool
    BOOLAND("&&", "and"),
    BOOLOR("||", "or"),

    ;

    public String name;
    public String cmd;

    Op(String name){
        this.name = name;
    }

    Op(String name, String cmd){
        this.name = name;
        this.cmd = cmd;
    }
}
