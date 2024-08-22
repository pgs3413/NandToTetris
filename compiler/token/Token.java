package token;

import tree.Op;

import java.util.HashMap;
import java.util.Map;

public enum Token {

    //keyword
    CLASS("class", Type.K),
    CONSTRUCTOR("constructor", Type.K),
    FUNCTION("function", Type.K),
    METHOD("method", Type.K),
    FIELD("field", Type.K),
    STATIC("static",Type.K),
    VAR("var", Type.K),
    INT("int", Type.K),
    BOOLEAN("boolean", Type.K),
    VOID("void", Type.K),
    TRUE("true", Type.K),
    FALSE("false", Type.K),
    NULL("null", Type.K),
    THIS("this", Type.K),
    LET("let", Type.K),
    DO("do", Type.K),
    IF("if", Type.K),
    ELSE("else", Type.K),
    WHILE("while", Type.K),
    RETURN("return", Type.K),
    NEW("new", Type.K),

    //symbol
    LBRACE("{", Type.S),
    RBRACE("}", Type.S),
    LPAREN("(", Type.S),
    RPAREN(")", Type.S),
    LBRACKET("[", Type.S),
    RBRACKET("]", Type.S),
    DOT(".", Type.S),
    COMMA(",", Type.S),
    SEMI(";", Type.S),

    PLUS("+", Type.S, Op.PLUS),
    SUB("-", Type.S, Op.SUB),
    STAR("*", Type.S, Op.MUL),
    SLASH("/", Type.S, Op.DIV),
    AMP("&", Type.S, Op.AND),
    BAR("|", Type.S, Op.OR),
    TILDE("~", Type.S),

    LT("<", Type.S, Op.LT),
    GT(">", Type.S, Op.GT),
    LTEQ("<=", Type.S, Op.LTEQ),
    GTEQ(">=", Type.S, Op.GTEQ),
    EQEQ("==", Type.S, Op.EQ),
    BANGEQ("!=", Type.S, Op.NOTEQ),

    AMPAMP("&&", Type.S, Op.BOOLAND),
    BARBAR("||", Type.S, Op.BOOLOR),
    BANG("!", Type.S),

    EQ("=", Type.S),

    INTCONSTANT("整型常量"),
    STRINGCONSTANT("字符串常量"),
    IDENTIFIER("非关键字标识符"),

    EOF("文件结束"),
    ;

    public final String name;
    public final Type type;
    public final Op op;

    public static final Map<String, Token> SYMBOL_MAP = new HashMap<>();
    public static final Map<String, Token> KEYWORD_MAP = new HashMap<>();

    static {
        for(Token token : Token.values()){
            if(token.type == Type.S) SYMBOL_MAP.put(token.name, token);
            else if(token.type == Type.K) KEYWORD_MAP.put(token.name, token);
        }
    }

    Token(String name){
        this(name, null, null);
    }

    Token(String name, Type type){
        this(name, type, null);
    }

    Token(String name, Type type, Op op){
        this.name = name;
        this.type = type;
        this.op = op;
    }

    public enum Type {
        K("keyword"),S("symbol");

        public String name;

        Type(String name){
            this.name = name;
        }
    }

}
