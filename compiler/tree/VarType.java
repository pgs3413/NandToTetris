package tree;

public enum VarType {

    STATIC("static", "static"),
    FIELD("field", "this"),
    VAR("var", "local"),
    PARAM("param", "argument");

    public String name;
    public String segment;

    VarType(String name, String segment){
        this.name = name;
        this.segment = segment;
    }
}
