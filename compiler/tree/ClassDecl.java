package tree;

import xml.ListNode;
import xml.Node;
import xml.ValueNode;

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
        nodeList.add(ValueNode.of("className", className));
        for(VarDecl x : varDecls){
            nodeList.add(x.toXml());
        }
        for(SubroutineDecl x : subroutineDecls){
            nodeList.add(x.toXml());
        }
        return ListNode.of("class", nodeList);
    }
}
