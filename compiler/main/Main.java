package main;

import token.Scanner;
import token.Token;
import utils.Utils;
import xml.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: pangs
 * @Date: 2024/7/22
 * @description: main
 */
public class Main {

    public static void main(String[] args) throws IOException {

        Path path = Paths.get(args[0]);
        File file = path.toFile();
        if(!file.isDirectory()){
            Utils.exit(args[0] + " is not a directory");
        }

        File[] files = file.listFiles(((dir, name) -> name.endsWith(".jack")));
        if(files == null || files.length == 0){
            Utils.exit(args[0] + " has no jack file");
        }

        assert files != null;
        List<Path> jackFiles = Arrays.stream(files).map(File::toPath).collect(Collectors.toList());

        List<Path> osJackFiles = Arrays.stream(Objects.requireNonNull(Paths.get("./os").toFile().
                listFiles(((dir, name) -> name.endsWith(".jack")))))
                .map(File::toPath).collect(Collectors.toList());

        jackFiles.addAll(osJackFiles);

        Compiler compiler = new Compiler(jackFiles, path);
        compiler.compile();

        System.out.println("compile successfully!");
    }
}
