package tree;

import token.Token;

import java.util.HashMap;
import java.util.Map;

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
    LTEQ("<=", "gt"),
    GTEQ(">=", "lt"),
    // x op x -> bool (x : int boolean sameclass array)
    NOTEQ("!=", "eq"),
    EQ("==", "eq"),

    // bool op bool -> bool
    BOOLAND("&&", "and"),
    BOOLOR("||", "or"),


    NULL(""),

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

    public static Map<Op, Integer> opPriority = new HashMap<>();

    static {
        opPriority.put(Op.BOOLOR, 1);
        opPriority.put(Op.BOOLAND, 2);
        opPriority.put(Op.OR, 3);
        opPriority.put(Op.AND, 4);
        opPriority.put(Op.EQ, 5);
        opPriority.put(Op.NOTEQ, 5);
        opPriority.put(Op.GT, 6);
        opPriority.put(Op.GTEQ, 6);
        opPriority.put(Op.LT, 6);
        opPriority.put(Op.LTEQ, 6);
        opPriority.put(Op.PLUS, 7);
        opPriority.put(Op.SUB, 7);
        opPriority.put(Op.MUL, 8);
        opPriority.put(Op.DIV, 8);
    }
}
