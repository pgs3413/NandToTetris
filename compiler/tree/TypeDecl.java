package tree;

import xml.Node;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: type decl
 */
public abstract class TypeDecl implements Tree{

    public static class IntType extends TypeDecl{
        @Override
        public Node toXml() {
            return Node.ValueNode.of("keyword", "int");
        }
    }

    public static class CharType extends  TypeDecl{
        @Override
        public Node toXml() {
            return Node.ValueNode.of("keyword", "char");
        }
    }

    public static class BoolType extends TypeDecl{
        @Override
        public Node toXml() {
            return Node.ValueNode.of("keyword", "boolean");
        }
    }

    public static class VoidType extends TypeDecl{
        @Override
        public Node toXml() {
            return Node.ValueNode.of("keyword", "void");
        }
    }

    public static class ClassType extends TypeDecl{
        public String className;
        public ClassType(String className) {
            this.className = className;
        }
        public Node toXml() {
            return Node.ValueNode.of("identifier", className);
        }
    }

}
