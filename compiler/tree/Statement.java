package tree;

import xml.ListNode;
import xml.Node;
import xml.ValueNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: statement
 */
public abstract class Statement implements Tree{


    public static class LetStatement extends Statement{
        public Expression varName;
        public Expression init;

        public LetStatement(Expression varName, Expression init) {
            this.varName = varName;
            this.init = init;
        }

        @Override
        public void accept(Visitor v) {
            v.visitLetStatement(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("target", Collections.singletonList(varName.toXml())));
            nodeList.add(ListNode.of("init", Collections.singletonList(init.toXml())));
            return ListNode.of("let", nodeList);
        }
    }

    public static class IfStatement extends Statement{
        public Expression condition;
        public List<Statement> thenPart;
        public List<Statement> elsePart;

        public IfStatement(Expression condition, List<Statement> thenPart, List<Statement> elsePart) {
            this.condition = condition;
            this.thenPart = thenPart;
            this.elsePart = elsePart;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIfStatement(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("condition", Collections.singletonList(condition.toXml())));
            //thenPart
            List<Node> thenStatement = new ArrayList<>();
            for(Statement s : thenPart) thenStatement.add(s.toXml());
            nodeList.add(ListNode.of("then", thenStatement));
            //elsePart
            List<Node> elseStatement = new ArrayList<>();
            for(Statement s : elsePart) elseStatement.add(s.toXml());
            nodeList.add(ListNode.of("else", elseStatement));
            return ListNode.of("if", nodeList);
        }
    }

    public static class WhileStatement extends Statement{
        public Expression condition;
        public List<Statement> body;

        public WhileStatement(Expression condition, List<Statement> body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public void accept(Visitor v) {
            v.visitWhileStatement(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("condition", Collections.singletonList(condition.toXml())));
            List<Node> statements = new ArrayList<>();
            for(Statement s : body) statements.add(s.toXml());
            nodeList.add(ListNode.of("body", statements));
            return ListNode.of("while", nodeList);
        }
    }

    public static class DoStatement extends Statement{
        public Expression.SubroutineCall subroutineCall;

        public DoStatement(Expression.SubroutineCall subroutineCall) {
            this.subroutineCall = subroutineCall;
        }

        @Override
        public void accept(Visitor v) {
            v.visitDoStatement(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(subroutineCall.toXml());
            return ListNode.of("do", nodeList);
        }
    }

    public static class ReturnStatement extends Statement{
        public Expression value;

        public ReturnStatement(Expression value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitReturnStatement(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            if(value != null) nodeList.add(value.toXml());
            return ListNode.of("return", nodeList);
        }
    }

}
