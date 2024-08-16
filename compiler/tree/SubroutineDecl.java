package tree;

import sym.SubroutineSymbol;
import sym.Symbol;
import xml.Node;

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
    public List<ParameterDecl> parameterDecls;
    public List<VarDecl> varDecls;
    public List<Statement> statements;
    public SubroutineSymbol sym;

    public SubroutineDecl(SubroutineType subroutineType, TypeDecl returnType, String subroutineName, List<ParameterDecl> parameterDecls, List<VarDecl> varDecls, List<Statement> statements) {
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

        nodeList.add(Node.ValueNode.of("keyword", subroutineType.name));
        nodeList.add(returnType.toXml());
        nodeList.add(Node.ValueNode.of("identifier", subroutineName));
        nodeList.add(Node.ValueNode.of("symbol", "("));

        //parameterList
        List<Node> parameterList = new ArrayList<>();
        if(parameterDecls.size() > 0){
            parameterList.add(parameterDecls.get(0).typeDecl.toXml());
            parameterList.add(Node.ValueNode.of("identifier", parameterDecls.get(0).parameterName));
            for(int i = 1; i < parameterDecls.size(); i++){
                parameterList.add(Node.ValueNode.of("symbol", ","));
                parameterList.add(parameterDecls.get(i).typeDecl.toXml());
                parameterList.add(Node.ValueNode.of("identifier", parameterDecls.get(i).parameterName));
            }
        }
        nodeList.add(Node.ListNode.of("parameterList", parameterList));

        nodeList.add(Node.ValueNode.of("symbol", ")"));

        //subroutineBody
        List<Node> subroutineBody = new ArrayList<>();
        subroutineBody.add(Node.ValueNode.of("symbol", "{"));
        for(VarDecl x : varDecls) subroutineBody.add(x.toXml());

        //statements
        List<Node> statementNodes = new ArrayList<>();
        for(Statement s : statements) statementNodes.add(s.toXml());
        subroutineBody.add(Node.ListNode.of("statements", statementNodes));

        subroutineBody.add(Node.ValueNode.of("symbol", "}"));

        nodeList.add(Node.ListNode.of("subroutineBody", subroutineBody));

        return Node.ListNode.of("subroutineDec", nodeList);
    }
}
