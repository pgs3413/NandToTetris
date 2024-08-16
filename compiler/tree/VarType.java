package tree;

public enum VarType {

    STATIC("static"),FIELD("field"),VAR("var"),PARAM("param");

    public String name;

    VarType(String name){
        this.name = name;
    }
}
