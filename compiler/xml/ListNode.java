package xml;

import java.util.List;

public class ListNode extends Node {

    private List<Node> values;

    public ListNode(String name) {
        super(name);
    }

    public ListNode(String name, List<Node> values) {
        super(name);
        this.values = values;
    }

    public void setValues(List<Node> values) {
        this.values = values;
    }

    public static xml.ListNode of(String name) {
        return new xml.ListNode(name);
    }

    public static xml.ListNode of(String name, List<Node> values) {
        return new xml.ListNode(name, values);
    }

    @Override
    public String toString(int spaceCnt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaceCnt; i++) sb.append(" ");
        sb.append("<").append(name).append(">").append("\n");
        if (values != null) {
            for (Node node : values) sb.append(node.toString(spaceCnt + 2));
        }
        for (int i = 0; i < spaceCnt; i++) sb.append(" ");
        sb.append("</").append(name).append(">").append("\n");
        return sb.toString();
    }

}
