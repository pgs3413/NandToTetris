package tree;

public enum SubroutineType {

    CONSTRUCTOR("constructor"),FUNCTION("function"),METHOD("method");

    public String name;

    SubroutineType(String name){
        this.name = name;
    }

}
