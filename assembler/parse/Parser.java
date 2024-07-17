package parse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static parse.CommandType.*;

/**
 * @Author: pangs
 * @Date: 2024/6/21
 * @description: Assembler parse.Parser
 */
public class Parser {

    private final List<Line> lines;
    private int index;
    private final int size;
    private Line line;


    private static final List<String> JUMP_SET = new ArrayList<String>(){{
       add("JGT");add("JEQ");add("JGE");add("JLT");add("JNE");add("JLE");add("JMP");
    }};


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

    public void restart(){
        index = 0;
    }

    public void advance(){
        Line line = lines.get(index);
        if(line.commandType == null) {
            List<String> tokens = lineTokens(line.value);
            if("@".equals(tokens.get(0))){
                handleACommand(line, tokens);
            }else if("(".equals(tokens.get(0))){
                handleLCommand(line, tokens);
            }else {
                handleCCommand(line, tokens);
            }
        }
        this.line = line;
        index++;
    }

    public CommandType commandType(){
        return line.commandType;
    }

    public String symbol(){
        return line.symbol;
    }

    public boolean destM(){
        return line.destM;
    }

    public boolean destA(){
        return line.destA;
    }

    public boolean destD(){
        return line.destD;
    }

    public String comp(){
        return line.comp;
    }

    public String jump(){
        return line.jump;
    }

    private void handleACommand(Line line, List<String> tokens){
        if(tokens.size() < 2){
            exit(line.number,"A命令格式：@ + 非负十进制整数/符号");
        }
        String symbol = tokens.get(1);
        if(!checkSymbol(symbol)){
            exit(line.number, "非法符号：" + symbol);
        }
        if(tokens.size() > 2){
            exit(line.number, "非法符号：" + tokens.get(2));
        }
        line.commandType = A_COMMAND;
        line.symbol = symbol;
    }

    private void handleLCommand(Line line, List<String> tokens){
        if(tokens.size() < 2){
            exit(line.number,"L命令格式：(符号)");
        }
        String symbol = tokens.get(1);
        if(!checkSymbol(symbol)){
            exit(line.number, "非法符号：" + symbol);
        }
        if(!")".equals(tokens.get(2))){
            exit(line.number,"L命令格式：(符号)");
        }
        if(tokens.size() > 3){
            exit(line.number, "非法符号：" + tokens.get(3));
        }
        line.commandType = L_COMMAND;
        line.symbol = symbol;
    }

    private void handleCCommand(Line line, List<String> tokens){
        int start = 0;
        int length = tokens.size();
        if(tokens.size() > 1 && "=".equals(tokens.get(1))){
            String dest = tokens.get(0);
            for(int i = 0; i < dest.length(); i++){
                char c = dest.charAt(i);
                if(c == 'M' && !line.destM) line.destM = true;
                else if(c == 'A' && !line.destA) line.destA = true;
                else if(c == 'D' && !line.destD) line.destD = true;
                else exit(line.number, "非法dest符号：" + dest + "的" + c);
            }
            start = 2;
        }
        if(start >= length) {
            exit(line.number,"C命令格式：dest=comp;jump");
        }
        String next;
        String next_next;
        String s = tokens.get(start++);
        switch (s){
            case "0" :
                line.comp = "0";
                break;
            case "1" :
                if(start < length && "+".equals(tokens.get(start))){
                    start += 1;
                    if(start >= length) {
                        exit(line.number,"C命令：comp不合法");
                    }
                    next = tokens.get(start++);
                    if("A".equals(next)) line.comp = "A+1";
                    else if("D".equals(next)) line.comp = "D+1";
                    else if("M".equals(next)) line.comp = "M+1";
                    else exit(line.number,"C命令：comp不合法");
                }else {
                    line.comp = "1";
                }
                break;
            case "-" :
                if(start >= length){
                    exit(line.number,"C命令：comp不合法");
                }
                next = tokens.get(start++);
                if("A".equals(next)) line.comp = "-A";
                else if("D".equals(next)) line.comp = "-D";
                else if("M".equals(next)) line.comp = "-M";
                else if("1".equals(next)) line.comp = "-1";
                else exit(line.number,"C命令：comp不合法");
                break;
            case "!" :
                if(start >= length){
                    exit(line.number,"C命令：comp不合法");
                }
                next = tokens.get(start++);
                if("A".equals(next)) line.comp = "!A";
                else if("D".equals(next)) line.comp = "!D";
                else if("M".equals(next)) line.comp = "!M";
                else exit(line.number,"C命令：comp不合法");
                break;
            case "D" :
                if(start + 1 < length){
                    next = tokens.get(start);
                    next_next = tokens.get(start + 1);
                    switch (next){
                        case "+" :
                            if("1".equals(next_next)) line.comp = "D+1";
                            else if("A".equals(next_next)) line.comp = "D+A";
                            else if("M".equals(next_next)) line.comp = "D+M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "-" :
                            if("1".equals(next_next)) line.comp = "D-1";
                            else if("A".equals(next_next)) line.comp = "D-A";
                            else if("M".equals(next_next)) line.comp = "D-M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "&" :
                            if("A".equals(next_next)) line.comp = "D&A";
                            else if("M".equals(next_next)) line.comp = "D&M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "|" :
                            if("A".equals(next_next)) line.comp = "D|A";
                            else if("M".equals(next_next)) line.comp = "D|M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        default: line.comp = "D";
                    }
                }else {
                    line.comp = "D";
                }
                break;
            case "A" :
                if(start + 1 < length){
                    next = tokens.get(start);
                    next_next = tokens.get(start + 1);
                    switch (next){
                        case "+" :
                            if("1".equals(next_next)) line.comp = "A+1";
                            else if("D".equals(next_next)) line.comp = "D+A";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "-" :
                            if("1".equals(next_next)) line.comp = "A-1";
                            else if("D".equals(next_next)) line.comp = "A-D";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "&" :
                            if("D".equals(next_next)) line.comp = "D&A";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "|" :
                            if("D".equals(next_next)) line.comp = "D|A";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        default: line.comp = "A";
                    }
                }else {
                    line.comp = "A";
                }
                break;
            case "M" :
                if(start + 1 < length){
                    next = tokens.get(start);
                    next_next = tokens.get(start + 1);
                    switch (next){
                        case "+" :
                            if("1".equals(next_next)) line.comp = "M+1";
                            else if("D".equals(next_next)) line.comp = "D+M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "-" :
                            if("1".equals(next_next)) line.comp = "M-1";
                            else if("D".equals(next_next)) line.comp = "M-D";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "&" :
                            if("D".equals(next_next)) line.comp = "D&M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        case "|" :
                            if("D".equals(next_next)) line.comp = "D|M";
                            else exit(line.number,"C命令：comp不合法");
                            start+=2;
                            break;
                        default: line.comp = "M";
                    }
                } else {
                    line.comp = "M";
                }
                break;
            default: exit(line.number,"C命令：comp不合法");
        }
        if(start < length){
            next = tokens.get(start++);
            if(!";".equals(next)){
                exit(line.number,"C命令格式：dest=comp;jump");
            }
            if(start < length){
                next = tokens.get(start);
                if(!JUMP_SET.contains(next)) exit(line.number,"C命令：jump不合法");
                line.jump = next;
                if(start + 1 < length) exit(line.number, "非法符号：" + tokens.get(start + 1));
            }
        }

        line.commandType = C_COMMAND;
    }


    private List<String> lineTokens(String line){
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int length = line.length();
        for(int i = 0; i < length; i++){
            char c = line.charAt(i);
            if(checkSymbol(c)){
                token.append(c);
            }else {
                if(!"".equals(token.toString())){
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                if(c == ' ') continue;
                if(c == '/' &&  i + 1 < length && line.charAt(i + 1) == '/') break;
                tokens.add(Character.toString(c));
            }
        }
        if(!"".equals(token.toString())) tokens.add(token.toString());
        return tokens;
    }

    private boolean checkSymbol(String symbol){
        for(int i = 0; i < symbol.length(); i++){
            if(!checkSymbol(symbol.charAt(i))) return false;
        }
        return true;
    }

    private boolean checkSymbol(char c){
        return '0' <= c && c <= '9'
                || 'a' <= c && c <= 'z'
                || 'A' <= c && c <= 'Z'
                || c == '_' || c == '.' || c == '$' || c == ':';
    }

    private void exit(int number, String msg){
        System.err.println("第 " + number +" 行：" + msg);
        System.exit(-1);
    }

    private static class Line {
        public int number;
        public String value;
        public CommandType commandType;
        public String symbol;
        public boolean destM = false;
        public boolean destA = false;
        public boolean destD = false;
        public String comp;
        public String jump = null;
        Line(int number, String value) {
            this.number = number;
            this.value = value;
        }

    }

}
