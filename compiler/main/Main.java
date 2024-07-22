package main;

import token.Scanner;
import token.Token;
import xml.XmlUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: pangs
 * @Date: 2024/7/22
 * @description: main
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Path source = Paths.get("./jack/Square/SquareGame.jack");
        Path target = Paths.get("./jack/Square/SquareGameT.xml");

        XmlUtils.tokenizing(source, target);

    }
}
