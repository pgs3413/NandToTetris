package main;

import code.Coder;
import parse.CommandType;
import parse.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static parse.CommandType.*;

/**
 * @Author: pangs
 * @Date: 2024/7/12
 * @description: Main
 */
public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 1){
           exit("请输入源文件路径");
        }

        List<Path> sourcePaths = new ArrayList<>();
        Path targetPath = null;

        Path sourcePath = Paths.get(args[0]);
        File sourceFile = sourcePath.toFile();
        if(sourceFile.isDirectory()){
            File[] files = sourceFile.listFiles(f -> f.isFile() && f.getName().endsWith(".vm"));
           if(files == null || files.length == 0){
               exit(args[0] + "源文件路径下无VM文件");
           }
            assert files != null;
            for(File f : files) sourcePaths.add(f.toPath());
            targetPath = Paths.get(args[0], sourceFile.getName()  + ".asm");
        }else if(sourceFile.isFile()){
            sourcePaths.add(sourcePath);
            targetPath = Paths.get(sourceFile.getParent() + "/" + sourceFile.getName().split("\\.")[0] + ".asm");
        }else {
            exit("非法文件路径 " + args[0]);
        }

        assert targetPath != null;
        BufferedWriter targetWriter = Files.newBufferedWriter(targetPath);

        //VM初始化
        List<String> init = Coder.init();
        for(String s : init){
            targetWriter.append(s);
            targetWriter.newLine();
        }

        for(Path path : sourcePaths){
            Parser parser = new Parser(path);
            String functionName = "f";
            while (parser.hasMoreCommands()){
                parser.advance();
                CommandType commandType = parser.commandType();
                String arg1 = parser.arg1();
                int arg2 = parser.arg2();
                List<String> vms = null;
                if(C_ARITHMETIC == commandType){
                    vms = Coder.arithmetic(arg1);
                }else if (C_PUSH == commandType){
                    vms = Coder.pushPop(C_PUSH, arg1, arg2);
                }else if(C_POP == commandType){
                    vms = Coder.pushPop(C_POP, arg1, arg2);
                }else if(C_LABEL == commandType){
                    vms = Coder.label(arg1, functionName);
                }else if(C_GOTO == commandType){
                    vms = Coder._goto(arg1, functionName);
                }else if(C_IF == commandType){
                    vms = Coder.if_goto(arg1, functionName);
                }else if(C_FUNCTION == commandType){
                    functionName = arg1;
                    vms = Coder.function(arg1, arg2);
                }else if(C_RETURN == commandType){
                    vms = Coder._return();
                }else if(C_CALL == commandType){
                    vms = Coder.call(arg1, arg2);
                } else {
                    exit("未知类型：" + commandType.toString());
                }
                assert vms != null;
                for(String vm : vms) {
                    targetWriter.append(vm);
                    targetWriter.newLine();
                }
            }
            Coder.STATIC_CNT++;
        }

        System.out.println("翻译成功！");
        targetWriter.close();

    }

    public static void exit(String msg){
        System.err.println(msg);
        System.exit(-1);
    }

}
