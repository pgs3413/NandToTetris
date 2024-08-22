package tree;

public enum SubroutineType {

    FUNCTION("function"),
    METHOD("method");

    public String name;

    SubroutineType(String name){
        this.name = name;
    }

}
