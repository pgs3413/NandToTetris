package tree;

import java.util.List;

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

    public static SubroutineDecl SubroutineDecl(SubroutineType subroutineType, TypeDecl returnType, String subroutineName, List<ParameterDecl> parameterDecls, List<VarDecl> varDecls, List<Statement> statements){
        return new SubroutineDecl(subroutineType, returnType, subroutineName, parameterDecls, varDecls, statements);
    }

    public static ParameterDecl ParameterDecl(TypeDecl typeDecl, String parameterName){
        return new ParameterDecl(typeDecl, parameterName);
    }

    public static TypeDecl CharType(){
        return TypeDecl.charType;
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

    public static TypeDecl ClassType(String className){
        return new TypeDecl(className, TypeKind.CLASS);
    }

    public static Statement.LetStatement LetStatement(Expression.Identifier varName, Expression init) {
        return new Statement.LetStatement(varName, init);
    }

    public static Statement.IfStatement IfStatement(Expression condition, List<Statement> thenPart, List<Statement> elsePart){
        return new Statement.IfStatement(condition, thenPart, elsePart);
    }

    public static Statement.WhileStatement WhileStatement(Expression condition, List<Statement> body){
        return new Statement.WhileStatement(condition, body);
    }

    public static Statement.DoStatement DoStatement(Expression.SubroutineCall subroutineCall){
        return new Statement.DoStatement(subroutineCall);
    }

    public static Statement.ReturnStatement ReturnStatement(Expression value){
        return new Statement.ReturnStatement(value);
    }

    public static Expression.IntegerConstant IntegerConstant(Integer value){
        return new Expression.IntegerConstant(value);
    }

    public static Expression.StringConstant StringConstant(String value){
        return new Expression.StringConstant(value);
    }

    public static Expression.KeyWordConstant KeyWordConstant(KeyWord value){
        return new Expression.KeyWordConstant(value);
    }

    public static Expression.Identifier Identifier(String name){
        return new Expression.Identifier(name);
    }

    public static Expression.ArrayAccess ArrayAccess(String name, Expression index){
        return new Expression.ArrayAccess(name, index);
    }

    public static Expression.FieldAccess FieldAccess(String name, Expression.Identifier own) {
        return new Expression.FieldAccess(name, own);
    }

    public static Expression.SubroutineCall SubroutineCall(Expression.Identifier subroutineName, List<Expression> args){
        return new Expression.SubroutineCall(subroutineName, args);
    }

    public static Expression.Parens Parens(Expression expr){
        return new Expression.Parens(expr);
    }

    public static Expression.Unary Unary(Op op, Expression.Term term){
        return new Expression.Unary(op, term);
    }

    public static Expression.Binary Binary(Op op, Expression.Term term1, Expression.Term term2){
        return new Expression.Binary(op, term1, term2);
    }


}
