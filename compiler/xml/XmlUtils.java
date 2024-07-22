package xml;

import token.Scanner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import token.Token;
import xml.Node.*;

/**
 * @Author: pangs
 * @Date: 2024/7/22
 * @description: xmlUtils
 */
public class XmlUtils {

    public static void tokenizing(Path source, Path target) throws IOException {

        Scanner s = new Scanner(source);

        List<Node> nodeList = new ArrayList<>();

        while (true){

            s.nextToken();
            if(s.token() == Token.EOF) break;
            nodeList.add(ValueNode.of(s.token().type.name, s.name()));

        }

        ListNode root = ListNode.of("tokens");
        root.setValues(nodeList);
        String xml = root.toString(0);

        Files.write(target, xml.getBytes(StandardCharsets.UTF_8));

    }
}
