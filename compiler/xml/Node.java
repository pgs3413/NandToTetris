package xml;

import java.util.List;

public abstract class Node {

    protected String name;

    public Node(String name){
        this.name = name;
    }

    public abstract String toString(int spaceCnt);

}
