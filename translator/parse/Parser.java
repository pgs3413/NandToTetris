package parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static code.ALCommand.*;
import static parse.CommandType.*;
import static code.Segment.*;

/**
 * @Author: pangs
 * @Date: 2024/7/7
 * @description: translator parser
 */
public class Parser {


    private final List<Line> lines;
    private int index;
    private final int size;
    private Line line;


    public Parser(Path fileName) throws IOException {
        AtomicInteger startNumber = new AtomicInteger(1);
        lines = Files.readAllLines(fileName)
                .stream().map(String::trim)
                .map(value -> new Line(startNumber.getAndIncrement(), value))
                .filter(line -> line.value.length() > 0 && !line.value.startsWith("//")).collect(Collectors.toList());
        index = 0;
        size = lines.size();
    }

    public boolean hasMoreCommands(){
        return index < size;
    }

    public void advance(){
        Line line = lines.get(index);
        List<String> tokens = lineTokens(line.value);
        String command = tokens.get(0);
        if(A_L_COMMANDS.contains(command)){
            if(tokens.size() != 1) exit(line.number, command + "命令不应该有参数");
            line.commandType = C_ARITHMETIC;
            line.arg1 = command;
        }else if("push".equals(command) || "pop".equals(command)){
            if(tokens.size() != 3) exit(line.number, "格式应为 push/pop segment index");
            String segment = tokens.get(1);
            String index = tokens.get(2);
            if(!SEGMENTS.contains(segment)) exit(line.number, segment + "为非法的segment符号");
            line.commandType = "push".equals(command) ? C_PUSH : C_POP;
            line.arg1 = segment;
            line.arg2 = toNumber(index, line.number);
        } else if("label".equals(command) || "goto".equals(command) || "if-goto".equals(command)){
            if(tokens.size() != 2) exit(line.number, "格式应为 label/goto/if-goto labelName");
            String labelName = tokens.get(1);
            if(!checkName(labelName)) exit(line.number, "labelName非法: " + labelName);
            line.commandType = "label".equals(command) ? C_LABEL : "goto".equals(command) ? C_GOTO : C_IF;
            line.arg1 = labelName;
        } else if("function".equals(command) || "call".equals(command)){
            if(tokens.size() != 3) exit(line.number, "格式应为 function/call functionName n");
            String functionName = tokens.get(1);
            String argsCnt = tokens.get(2);
            if(!checkName(functionName)) exit(line.number, "functionName非法: " + functionName);
            line.commandType = "function".equals(command) ? C_FUNCTION : C_CALL;
            line.arg1 = functionName;
            line.arg2 = toNumber(argsCnt, line.number);
        }else if("return".equals(command)){
            if(tokens.size() != 1) exit(line.number, "格式应为 return");
            line.commandType = C_RETURN;
        } else {
            exit(line.number, "非法的命令：" + command);
        }
        this.line = line;
        index++;
    }

    public CommandType commandType(){
        return line.commandType;
    }

    public String arg1(){
        return line.arg1;
    }

    public int arg2(){
        return line.arg2;
    }

    private List<String> lineTokens(String line){
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int length = line.length();
        for(int i = 0; i < length; i++){
            char c = line.charAt(i);
            if(c == '/' && i + 1 < length && line.charAt(i + 1) == '/') break;
            if(c == ' ' || c == '\t'){
                if(!"".equals(token.toString())){
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
            }else {
                token.append(c);
            }
        }
        if(!"".equals(token.toString())) tokens.add(token.toString());
        return tokens;
    }

    private int toNumber(String number, int lineNumber){
        try{
            return Integer.parseUnsignedInt(number, 10);
        }catch (Exception e){
            exit(lineNumber, "非法的数字 " + number);
        }
        return 0;
    }

    private boolean checkName(String name){
        char start = name.charAt(0);
        if('0' <= start && start <= '9') return false;
        for(int i = 0; i < name.length(); i++){
            char c = name.charAt(i);
            if(!( '0' <= c && c <= '9' || 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '_' == c || '.' == c || ':' == c)) return false;
        }
        return true;
    }

    private void exit(int number, String msg){
        System.err.println("第 " + number +" 行：" + msg);
        System.exit(-1);
    }

    private static class Line {
        int number;
        String value;
        CommandType commandType;
        String arg1;
        int arg2;
        Line(int number, String value){
            this.number = number;
            this.value = value;
        }
    }

}
