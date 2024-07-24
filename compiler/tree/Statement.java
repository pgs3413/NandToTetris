package tree;

import xml.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: statement
 */
public abstract class Statement implements Tree{


    public static class LetStatement extends Statement{
        public Expression.Identifier varName;
        public Expression init;

        public LetStatement(Expression.Identifier varName, Expression init) {
            this.varName = varName;
            this.init = init;
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("keyword", "let"));
            nodeList.addAll(varName.toNodeList());
            nodeList.add(Node.ValueNode.of("symbol", "="));
            nodeList.add(init.toXml());
            nodeList.add(Node.ValueNode.of("symbol", ";"));
            return Node.ListNode.of("letStatement", nodeList);
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
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("keyword", "if"));
            nodeList.add(Node.ValueNode.of("symbol", "("));
            nodeList.add(condition.toXml());
            nodeList.add(Node.ValueNode.of("symbol", ")"));
            nodeList.add(Node.ValueNode.of("symbol", "{"));
            //thenPart
            List<Node> statements1 = new ArrayList<>();
            for(Statement s : thenPart) statements1.add(s.toXml());
            nodeList.add(Node.ListNode.of("statements", statements1));
            nodeList.add(Node.ValueNode.of("symbol", "}"));
            //elsePart
            if(elsePart != null){
                nodeList.add(Node.ValueNode.of("keyword", "else"));
                nodeList.add(Node.ValueNode.of("symbol", "{"));
                List<Node> statements2 = new ArrayList<>();
                for(Statement s : elsePart) statements2.add(s.toXml());
                nodeList.add(Node.ListNode.of("statements", statements2));
                nodeList.add(Node.ValueNode.of("symbol", "}"));
            }
            return Node.ListNode.of("ifStatement", nodeList);
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
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("keyword", "while"));
            nodeList.add(Node.ValueNode.of("symbol", "("));
            nodeList.add(condition.toXml());
            nodeList.add(Node.ValueNode.of("symbol", ")"));
            nodeList.add(Node.ValueNode.of("symbol", "{"));
            List<Node> statements = new ArrayList<>();
            for(Statement s : body) statements.add(s.toXml());
            nodeList.add(Node.ListNode.of("statements", statements));
            nodeList.add(Node.ValueNode.of("symbol", "}"));
            return Node.ListNode.of("whileStatement", nodeList);
        }
    }

    public static class DoStatement extends Statement{
        public Expression.SubroutineCall subroutineCall;

        public DoStatement(Expression.SubroutineCall subroutineCall) {
            this.subroutineCall = subroutineCall;
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("keyword", "do"));
            nodeList.addAll(subroutineCall.toNodeList());
            nodeList.add(Node.ValueNode.of("symbol", ";"));
            return Node.ListNode.of("doStatement", nodeList);
        }
    }

    public static class ReturnStatement extends Statement{
        public Expression value;

        public ReturnStatement(Expression value) {
            this.value = value;
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("keyword", "return"));
            if(value != null) nodeList.add(value.toXml());
            nodeList.add(Node.ValueNode.of("symbol", ";"));
            return Node.ListNode.of("returnStatement", nodeList);
        }
    }

}
