package token;

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
    CHAR("char", Type.K),
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
    PLUS("+", Type.S),
    SUB("-", Type.S),
    STAR("*", Type.S),
    SLASH("/", Type.S),
    AND("&", Type.S),
    OR("|", Type.S),
    LT("<", Type.S),
    GT(">", Type.S),
    EQ("=", Type.S),
    NOT("~", Type.S),

    INTCONSTANT("整型常量",Type.IC),
    STRINGCONSTANT("字符串常量",Type.SC),
    IDENTIFIER("非关键字标识符" ,Type.I),

    EOF("文件结束"),
    ;

    public final String name;
    public final Type type;

    public static final Map<String, Token> SYMBOL_MAP = new HashMap<>();
    public static final Map<String, Token> KEYWORD_MAP = new HashMap<>();

    static {
        for(Token token : Token.values()){
            if(token.type == Type.S) SYMBOL_MAP.put(token.name, token);
            else if(token.type == Type.K) KEYWORD_MAP.put(token.name, token);
        }
    }

    Token(){
        this(null, null);
    }

    Token(String name){
        this(name, null);
    }

    Token(String name, Type type){
        this.name = name;
        this.type = type;
    }

    public enum Type {
        K("keyword"),S("symbol"),I("identifier"),SC("stringConstant"),IC("integerConstant");

        public String name;

        Type(String name){
            this.name = name;
        }
    }

}
