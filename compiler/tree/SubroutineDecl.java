package tree;

import sym.SubroutineSymbol;
import xml.ListNode;
import xml.Node;
import xml.ValueNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: subroutine decl
 */
public class SubroutineDecl implements Tree{

    public SubroutineType subroutineType;
    public TypeDecl returnType;
    public String subroutineName;
    public List<VarDecl> parameterDecls;
    public List<VarDecl> varDecls;
    public List<Statement> statements;
    public SubroutineSymbol sym;

    public SubroutineDecl(SubroutineType subroutineType, TypeDecl returnType, String subroutineName, List<VarDecl> parameterDecls, List<VarDecl> varDecls, List<Statement> statements) {
        this.subroutineType = subroutineType;
        this.returnType = returnType;
        this.subroutineName = subroutineName;
        this.parameterDecls = parameterDecls;
        this.varDecls = varDecls;
        this.statements = statements;
    }

    @Override
    public void accept(Visitor v) {
        v.visitSubroutineDecl(this);
    }

    @Override
    public Node toXml() {

        List<Node> nodeList = new ArrayList<>();

        nodeList.add(ValueNode.of("subroutineType", subroutineType.name));
        nodeList.add(returnType.toXml());
        nodeList.add(ValueNode.of("name", subroutineName));
        //parameters
        for(VarDecl tree: parameterDecls){
            nodeList.add(tree.toXml());
        }

        //local vars
        for(VarDecl tree : varDecls) {
            nodeList.add(tree.toXml());
        }

        //statements
        for(Statement tree : statements){
            nodeList.add(tree.toXml());
        }

        return ListNode.of("subroutine", nodeList);
    }
}
