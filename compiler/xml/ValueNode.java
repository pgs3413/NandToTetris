package xml;

public class ValueNode extends Node {

    private final String value;

    public ValueNode(String name, String value) {
        super(name);
        this.value = value;
    }

    public static xml.ValueNode of(String name, String value) {
        return new xml.ValueNode(name, value);
    }

    @Override
    public String toString(int spaceCnt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaceCnt; i++) sb.append(" ");
        sb.append("<").append(name).append(">").append(" ")
                .append(value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
                .append(" ").append("</").append(name).append(">").append("\n");
        return sb.toString();
    }

}
