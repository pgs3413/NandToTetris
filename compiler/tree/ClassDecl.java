package tree;

import xml.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: class decl
 */
public class ClassDecl implements Tree {

    public String className;
    public List<VarDecl> varDecls;
    public List<SubroutineDecl> subroutineDecls;
    public String fileName;

    public ClassDecl(String className, List<VarDecl> varDecls, List<SubroutineDecl> subroutineDecls) {
        this.className = className;
        this.varDecls = varDecls;
        this.subroutineDecls = subroutineDecls;
    }


    @Override
    public void accept(Visitor v) {
        v.visitClassDecl(this);
    }

    @Override
    public Node toXml() {
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(Node.ValueNode.of("keyword", "class"));
        nodeList.add(Node.ValueNode.of("identifier", className));
        nodeList.add(Node.ValueNode.of("symbol", "{"));
        for(VarDecl x : varDecls){
            nodeList.add(x.toXml());
        }
        for(SubroutineDecl x : subroutineDecls){
            nodeList.add(x.toXml());
        }
        nodeList.add(Node.ValueNode.of("symbol", "}"));

        return Node.ListNode.of("class", nodeList);
    }
}
