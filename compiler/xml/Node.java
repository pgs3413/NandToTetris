package xml;

import java.util.List;

public abstract class Node {

    protected String name;

    public Node(String name){
        this.name = name;
    }

    public abstract String toString(int spaceCnt);

    public static class ListNode extends Node {

        private List<Node> values;

        public ListNode(String name){
            super(name);
        }
        public void setValues(List<Node> values){
            this.values = values;
        }

        public static ListNode of(String name){
            return new ListNode(name);
        }

        @Override
        public String toString(int spaceCnt){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < spaceCnt; i++) sb.append(" ");
            sb.append("<").append(name).append(">").append("\n");
            if(values != null){
                for(Node node : values) sb.append(node.toString(spaceCnt + 2));
            }
            for(int i = 0; i < spaceCnt; i++) sb.append(" ");
            sb.append("</").append(name).append(">").append("\n");
            return sb.toString();
        }

    }

    public static class ValueNode extends Node {

        private final String value;

        public ValueNode(String name, String value){
            super(name);
            this.value = value;
        }

        public static ValueNode of(String name, String value){
            return new ValueNode(name, value);
        }

        @Override
        public String toString(int spaceCnt){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < spaceCnt; i++) sb.append(" ");
            sb.append("<").append(name).append(">").append(" ")
                    .append(value.replace("&","&amp;").replace("<","&lt;").replace(">", "&gt;"))
                    .append(" ").append("</").append(name).append(">").append("\n");
            return sb.toString();
        }

    }

}
