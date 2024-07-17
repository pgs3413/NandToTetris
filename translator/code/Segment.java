package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/7/12
 * @description: segment
 */
public class Segment {

    public static final String ARGUMENT = "argument";
    public static final String LOCAL = "local";
    public static final String STATIC = "static";
    public static final String CONSTANT = "constant";
    public static final String THIS = "this";
    public static final String THAT = "that";
    public static final String POINTER = "pointer";
    public static final String TEMP = "temp";
    public static final List<String> SEGMENTS = Arrays.asList(ARGUMENT, LOCAL, STATIC, CONSTANT, THIS, THAT, POINTER, TEMP);

}
