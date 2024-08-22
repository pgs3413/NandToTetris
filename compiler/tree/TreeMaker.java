package tree;

import java.util.Collections;
import java.util.List;

import static tree.Statement.*;
import static tree.Expression.*;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: tree maker
 */
public class TreeMaker {

    public static ClassDecl ClassDecl(String className, List<VarDecl> varDecls, List<SubroutineDecl> subroutineDecls){
        return new ClassDecl(className, varDecls, subroutineDecls);
    }

    public static VarDecl VarDecl(VarType varType, TypeDecl typeDecl, List<String> varNames){
        return new VarDecl(varType, typeDecl, varNames);
    }

    public static VarDecl ParameterDecl(TypeDecl typeDecl, String parameterName){
        return new VarDecl(VarType.PARAM, typeDecl, Collections.singletonList(parameterName));
    }

    public static SubroutineDecl SubroutineDecl(SubroutineType subroutineType, TypeDecl returnType, String subroutineName, List<VarDecl> parameterDecls, List<VarDecl> varDecls, List<Statement> statements){
        return new SubroutineDecl(subroutineType, returnType, subroutineName, parameterDecls, varDecls, statements);
    }


    public static TypeDecl IntType(){
        return  TypeDecl.intType;
    }

    public static TypeDecl BoolType(){
        return TypeDecl.boolType;
    }

    public static TypeDecl VoidType(){
        return TypeDecl.voidType;
    }

    public static TypeDecl IntArrayType(){
        return TypeDecl.intArrayType;
    }

    public static TypeDecl BoolArrayType(){
        return TypeDecl.boolArrayType;
    }

    public static TypeDecl ClassType(String className){
        return new TypeDecl(className, TypeKind.CLASS);
    }

    public static TypeDecl ArrayType(String typeName){
        return new TypeDecl(typeName, TypeKind.Array);
    }

    public static LetStatement LetStatement(Expression varName, Expression init) {
        return new LetStatement(varName, init);
    }

    public static IfStatement IfStatement(Expression condition, List<Statement> thenPart, List<Statement> elsePart){
        return new IfStatement(condition, thenPart, elsePart);
    }

    public static WhileStatement WhileStatement(Expression condition, List<Statement> body){
        return new WhileStatement(condition, body);
    }

    public static DoStatement DoStatement(SubroutineCall subroutineCall){
        return new DoStatement(subroutineCall);
    }

    public static ReturnStatement ReturnStatement(Expression value){
        return new ReturnStatement(value);
    }

    public static IntegerConstant IntegerConstant(Integer value){
        return new IntegerConstant(value);
    }

    public static StringConstant StringConstant(String value){
        return new StringConstant(value);
    }

    public static KeyWordConstant KeyWordConstant(KeyWord value){
        return new KeyWordConstant(value);
    }

    public static Identifier Identifier(String name){
        return new Identifier(name);
    }

    public static ArrayAccess ArrayAccess(Expression arrayName, Expression index){
        return new ArrayAccess(arrayName, index);
    }

    public static FieldAccess FieldAccess(String name, Expression own) {
        return new FieldAccess(name, own);
    }

    public static SubroutineCall SubroutineCall(Expression subroutineName, List<Expression> args){
        return new SubroutineCall(subroutineName, args);
    }

    public static Parens Parens(Expression expr){
        return new Parens(expr);
    }

    public static NewClass NewClass(String className, List<Expression> args){
        return new NewClass(className, args);
    }

    public static NewArray NewArray(TypeDecl type, Expression size){
        return new NewArray(type, size);
    }

    public static Unary Unary(Op op, Expression term){
        return new Unary(op, term);
    }

    public static Binary Binary(Op op, Expression expr1, Expression expr2){
        return new Binary(op, expr1, expr2);
    }


}
