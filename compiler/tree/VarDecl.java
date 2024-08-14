package tree;

import xml.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: var decl
 */
public class VarDecl implements Tree{

    public VarType varType;
    public TypeDecl typeDecl;
    public List<String> varNames;

    public VarDecl(VarType varType, TypeDecl typeDecl, List<String> varNames) {
        this.varType = varType;
        this.typeDecl = typeDecl;
        this.varNames = varNames;
    }

    @Override
    public void accept(Visitor v) {
        v.visitVarDecl(this);
    }

    @Override
    public Node toXml() {
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(Node.ValueNode.of("keyword", varType.name));
        nodeList.add(typeDecl.toXml());
        nodeList.add(Node.ValueNode.of("identifier", varNames.get(0)));
        for(int i = 1; i < varNames.size(); i++){
            nodeList.add(Node.ValueNode.of("symbol", ","));
            nodeList.add(Node.ValueNode.of("identifier", varNames.get(i)));
        }
        nodeList.add(Node.ValueNode.of("symbol", ";"));
        return Node.ListNode.of(varType == VarType.VAR ? "varDec" : "classVarDec", nodeList);
    }
}
