package tree;

import token.Token;
import xml.ListNode;
import xml.Node;
import xml.ValueNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: expression
 */
public abstract class Expression implements Tree {


    public static class IntegerConstant extends Expression{
        public Integer value;

        public IntegerConstant(Integer value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIntegerConstant(this);
        }

        @Override
        public Node toXml() {
            return ValueNode.of("integer", value.toString());
        }
    }

    public static class StringConstant extends Expression{
        public String value;

        public StringConstant(String value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitStringConstant(this);
        }

        @Override
        public Node toXml() {
            return ValueNode.of("string", value);
        }
    }

    public static class KeyWordConstant extends Expression {
        public KeyWord value;

        public KeyWordConstant(KeyWord value) {
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitKeyWordConstant(this);
        }

        @Override
        public Node toXml() {
            return ValueNode.of("keyword", value.name);
        }
    }

    public static class Identifier extends Expression{
        public String name;

        public Identifier(String name) {
            this.name = name;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIdentifier(this);
        }

        @Override
        public Node toXml() {
            return ValueNode.of("identifier", name);
        }
    }

    public static class ArrayAccess extends Expression {
        public Expression arrayName;
        public Expression index;

        public ArrayAccess(Expression arrayName, Expression index) {
            this.arrayName = arrayName;
            this.index = index;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayAccess(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("name", Collections.singletonList(arrayName.toXml())));
            nodeList.add(ListNode.of("index", Collections.singletonList(index.toXml())));
            return ListNode.of("arrayAccess", nodeList);
        }
    }

    public static class FieldAccess extends Expression{
        public String name;
        public Expression own;

        public FieldAccess(String name, Expression own) {
            this.name = name;
            this.own = own;
        }

        @Override
        public void accept(Visitor v) {
            v.visitFieldAccess(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("own", Collections.singletonList(own.toXml())));
            nodeList.add(ValueNode.of("name", name));
            return ListNode.of("filedAccess", nodeList);
        }

    }


    public static class SubroutineCall extends Expression {
        public Expression subroutineName;
        public List<Expression> args;

        public SubroutineCall(Expression subroutineName, List<Expression> args) {
            this.subroutineName = subroutineName;
            this.args = args;
        }

        @Override
        public void accept(Visitor v) {
            v.visitSubroutineCall(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ListNode.of("subroutineName", Collections.singletonList(subroutineName.toXml())));
            List<Node> argsList = new ArrayList<>();
            for(Expression tree: args) argsList.add(tree.toXml());
            nodeList.add(ListNode.of("args", argsList));
            return ListNode.of("subroutineCall", nodeList);
        }
    }

    public static class Parens extends Expression{
        public Expression expr;

        public Parens(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitParens(this);
        }

        @Override
        public Node toXml() {
            return ListNode.of("paren", Collections.singletonList(expr.toXml()));
        }
    }

    public static class NewClass extends Expression{
        public String className;
        public List<Expression> args;

        public NewClass(String className, List<Expression> args){
            this.className = className;
            this.args = args;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNewClass(this);
        }

        @Override
        public Node toXml() {
           List<Node> nodeList = new ArrayList<>();
           nodeList.add(ValueNode.of("className", className));
           List<Node> argsList = new ArrayList<>();
           for(Expression tree: args) argsList.add(tree.toXml());
           nodeList.add(ListNode.of("args", argsList));
           return ListNode.of("newClass", nodeList);
        }

    }

    public static class NewArray extends Expression{
        public TypeDecl type;
        public Expression size;

        public NewArray(TypeDecl type, Expression size){
            this.type = type;
            this.size = size;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNewArray(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ValueNode.of("typeName", type.name));
            nodeList.add(ListNode.of("size", Collections.singletonList(size.toXml())));
            return ListNode.of("newArray", nodeList);
        }
    }

    public static class Unary extends Expression{
        public Op op;
        public Expression term;

        public Unary(Op op, Expression term) {
            this.op = op;
            this.term = term;
        }

        @Override
        public void accept(Visitor v) {
            v.visitUnary(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ValueNode.of("op", op.name));
            nodeList.add(ListNode.of("term", Collections.singletonList(term.toXml())));
            return ListNode.of("unary", nodeList);
        }
    }

    public static class Binary extends Expression {
        public Op op;
        public Expression expr1;
        public Expression expr2;

        public Binary(Op op, Expression expr1, Expression expr2) {
            this.op = op;
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBinary(this);
        }

        @Override
        public Node toXml() {
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(ValueNode.of("op", op.name));
            nodeList.add(ListNode.of("expr1", Collections.singletonList(expr1.toXml())));
            nodeList.add(ListNode.of("expr2", Collections.singletonList(expr2.toXml())));
            return ListNode.of("binary", nodeList);
        }
    }




}
