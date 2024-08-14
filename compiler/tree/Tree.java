package tree;

import xml.Node;

public interface Tree {

    Node toXml();

    void accept(Visitor v);
}
