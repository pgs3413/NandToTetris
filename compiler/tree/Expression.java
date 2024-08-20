package tree;

import xml.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: expression
 */
public abstract class Expression implements Tree {

    public abstract static class Term extends Expression {
        @Override
        public Node toXml() {
            return Node.ListNode.of("expression", Collections.singletonList(toTermNode()));
        }
        public Node toTermNode(){
            return Node.ListNode.of("term", toNodeList());
        }
        abstract List<Node> toNodeList();
    }

    public static class IntegerConstant extends Term{
        public Integer value;

        public IntegerConstant(Integer value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIntegerConstant(this);
        }

        @Override
        List<Node> toNodeList() {
            return Collections.singletonList(Node.ValueNode.of("integerConstant", value.toString()));
        }
    }

    public static class StringConstant extends Term{
        public String value;

        public StringConstant(String value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitStringConstant(this);
        }

        @Override
        List<Node> toNodeList() {
            return Collections.singletonList(Node.ValueNode.of("stringConstant", value));
        }

    }

    public static class KeyWordConstant extends Term {
        public KeyWord value;

        public KeyWordConstant(KeyWord value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitKeyWordConstant(this);
        }

        @Override
        List<Node> toNodeList() {
            return Collections.singletonList(Node.ValueNode.of("keyword", value.name));
        }
    }

    public static class Identifier extends Term{
        public String name;

        public Identifier(String name) {
            this.name = name;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIdentifier(this);
        }

        @Override
        public List<Node> toNodeList(){
            return Collections.singletonList(Node.ValueNode.of("identifier", name));
        }
    }

    public static class ArrayAccess extends Identifier {
        public Expression index;

        public ArrayAccess(String name, Expression index) {
            super(name);
            this.index = index;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayAccess(this);
        }

        @Override
        public List<Node> toNodeList(){
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("identifier", name));
            nodeList.add(Node.ValueNode.of("symbol", "["));
            nodeList.add(index.toXml());
            nodeList.add(Node.ValueNode.of("symbol", "]"));
            return nodeList;
        }
    }

    public static class FieldAccess extends Identifier{
        public Identifier own;

        public FieldAccess(String name, Identifier own) {
            super(name);
            this.own = own;
        }

        @Override
        public List<Node> toNodeList(){
            List<Node> nodeList = new ArrayList<>(own.toNodeList());
            nodeList.add(Node.ValueNode.of("symbol", "."));
            nodeList.add(Node.ValueNode.of("identifier", name));
            return nodeList;
        }
    }


    public static class SubroutineCall extends Term {
        public Identifier subroutineName;
        public List<Expression> args;

        public SubroutineCall(Identifier subroutineName, List<Expression> args) {
            this.subroutineName = subroutineName;
            this.args = args;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSubroutineCall(this);
        }

        public List<Node> toNodeList(){
            List<Node> nodeList = new ArrayList<>(subroutineName.toNodeList());
            nodeList.add(Node.ValueNode.of("symbol", "("));
            //expressionList
            List<Node> expressionList = new ArrayList<>();
            if(args.size() > 0){
                expressionList.add(args.get(0).toXml());
                for(int i = 1; i < args.size(); i++){
                    expressionList.add(Node.ValueNode.of("symbol", ","));
                    expressionList.add(args.get(i).toXml());
                }
            }
            nodeList.add(Node.ListNode.of("expressionList", expressionList));
            nodeList.add(Node.ValueNode.of("symbol", ")"));
            return nodeList;
        }

    }

    public static class Parens extends Term{
        public Expression expr;

        public Parens(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitParens(this);
        }

        @Override
        List<Node> toNodeList() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("symbol", "("));
            nodeList.add(expr.toXml());
            nodeList.add(Node.ValueNode.of("symbol", ")"));
            return nodeList;
        }
    }

    public static class Unary extends Term{
        public Op op;
        public Term term;

        public Unary(Op op, Term term) {
            this.op = op;
            this.term = term;
        }

        @Override
        public void accept(Visitor v) {
            v.visitUnary(this);
        }

        @Override
        List<Node> toNodeList() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(Node.ValueNode.of("symbol", op.name));
            nodeList.add(term.toTermNode());
            return nodeList;
        }
    }

    public static class Binary extends Expression {
        public Op op;
        public Term term1;
        public Term term2;

        public Binary(Op op, Term term1, Term term2) {
            this.op = op;
            this.term1 = term1;
            this.term2 = term2;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBinary(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(term1.toTermNode());
            nodeList.add(Node.ValueNode.of("symbol", op.name));
            nodeList.add(term2.toTermNode());
            return Node.ListNode.of("expression", nodeList);
        }
    }




}
