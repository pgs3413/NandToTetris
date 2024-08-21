package tree;

import xml.ListNode;
import xml.Node;
import xml.ValueNode;

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
        nodeList.add(ValueNode.of("varType", varType.name));
        nodeList.add(typeDecl.toXml());
        for(String name: varNames){
            nodeList.add(ValueNode.of("name", name));
        }
        return ListNode.of(varType == VarType.VAR ? "var" : "classVar", nodeList);
    }
}
