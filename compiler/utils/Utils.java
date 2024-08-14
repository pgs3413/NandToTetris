package utils;

/**
 * @Author: pangs
 * @Date: 2024/7/22
 * @description: utils.Utils
 */
public class Utils {


    public static boolean checkIdentifier(String identifier){
        for(int i = 0; i < identifier.length(); i++){
            char c = identifier.charAt(i);
            if(!('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '_')) return false;
        }
        return true;
    }

    public static boolean checkNumber(String number){
        if(number.length() != 1 && number.charAt(0) == '0') return false;
        for(int i = 0; i < number.length(); i++){
            char c = number.charAt(i);
            if(!('0' <= c && c <= '9')) return false;
        }
        return true;
    }

    public static void exit(String fileName, int line, int linePos, String msg){
        System.err.println(fileName + " 第 " + line + " 行，第 " + linePos + " 列发生错误：" + msg);
        System.exit(-1);
    }

    public static void exit(String msg){
        System.err.println(msg);
        System.exit(-1);
    }

}
