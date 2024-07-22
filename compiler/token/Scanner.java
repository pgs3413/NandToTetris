package token;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static token.Token.*;
import static utils.Utils.*;

/**
 * @Author: pangs
 * @Date: 2024/7/21
 * @description: scanner
 */
public class Scanner {

    private static final char EOI = 0x1A;

    private final char[] buf;
    private int pos = -1;
    private char ch;

    private int line = 1;
    private int linePos = 0;

    private Token token;
    private String name;

    public Scanner(Path source) throws IOException {
        byte[] bytes = Files.readAllBytes(source);
        buf = new char[bytes.length + 1];
        //不支持非ASCII码
        for(int i = 0; i < bytes.length; i++) buf[i] = (char) bytes[i];
        buf[bytes.length] = EOI;
        scanChar();
    }

    public Token token(){
        return token;
    }

    public String name(){
        return name;
    }

    private void scanChar(){
        ch = buf[++pos];
        if(ch == '\n' || ch == '\r'){
            line++;
            linePos = 0;
        }else {
            linePos++;
        }
    }


    public void nextToken() {

        while (true){

            if(ch == EOI){
                token = EOF;
                name = "EOF";
                return;
            }

            if(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
                scanChar();
                continue;
            }

            if(ch == '/'){
                scanChar();
                if(ch == '/'){ //注释
                    while (ch != '\n' && ch != '\r' && ch != EOI) scanChar();
                    continue;
                } else if(ch == '*'){ /*注释*/
                    while (true){
                        scanChar();
                        if(ch == EOI) break;
                        if(ch == '*' && buf[pos + 1] == '/') {
                            scanChar();
                            scanChar();
                            break;
                        }
                    }
                    continue;
                } else {
                    token = SLASH;
                    name = SLASH.name;
                    return;
                }
            }

            token = SYMBOL_MAP.get(Character.toString(ch));
            if(token != null) {
                name = token.name;
                scanChar();
                return;
            }

            if('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_'){
                StringBuilder sb = new StringBuilder();
                while (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r' && ch != EOI && !SYMBOL_MAP.containsKey(Character.toString(ch))){
                    sb.append(ch);
                    scanChar();
                }
                String identifier = sb.toString();
                if(!checkIdentifier(identifier)) exit(line, linePos - identifier.length(), "非法的标识符：" + identifier);
                token = KEYWORD_MAP.get(identifier);
                if (token == null) {
                    token = IDENTIFIER;
                    name = identifier;
                }else {
                    name = token.name;
                }
                return;
            }

            if('0' <= ch && ch <= '9'){
                StringBuilder sb = new StringBuilder();
                while (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r' && ch != EOI && !SYMBOL_MAP.containsKey(Character.toString(ch))){
                    sb.append(ch);
                    scanChar();
                }
                String number = sb.toString();
                if(!checkNumber(number)) exit(line, linePos - number.length(), "非法的数字：" + number);
                token = INTCONSTANT;
                name = number;
                return;
            }

            if(ch == '"'){
                StringBuilder sb = new StringBuilder();
                scanChar();
                while (ch != '"' && ch != '\n' && ch != '\r' && ch != EOI){
                    sb.append(ch);
                    scanChar();
                }
                if(ch == EOI || ch == '\n' || ch == '\r') exit(line, linePos, "字符串常量此处缺少\"");
                token = STRINGCONSTANT;
                name = sb.toString();
                scanChar();
                return;
            }

            exit(line, linePos, "未知符号：" + ch);

        }

    }

}
