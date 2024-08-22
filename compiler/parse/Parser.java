package parse;

import token.Scanner;
import token.Token;
import tree.*;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static token.Token.*;
import static tree.TreeMaker.*;
import static tree.Statement.*;
import static tree.Expression.*;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: parser
 */
public class Parser {

    private final Scanner s;
    private boolean existReturn;
    private boolean isArrayAccess;

    public Parser(Path source) throws IOException {
        s = new Scanner(source);
        s.nextToken();
    }

    public Tree parse(){
        accept(CLASS);
        String className = accept(IDENTIFIER);
        if(!s.className().equals(className)){
            exit("文件名与类名不一致");
        }
        accept(LBRACE);
        List<VarDecl> varDecls = parseVarDecls(true);
        List<SubroutineDecl> subroutineDecls = parseSubroutines();
        accept(RBRACE);
        accept(EOF);
        ClassDecl classDecl = ClassDecl(className, varDecls, subroutineDecls);
        classDecl.fileName = s.fileName();
        return classDecl;
    }

    TypeDecl parseType(List<Token> tokens){
        Token lastToken = s.token();
        String typeName = accept(tokens);
        if(s.token() == LBRACKET){
            accept(LBRACKET);
            accept(RBRACKET);
            switch (lastToken){
                case INT: return IntArrayType();
                case BOOLEAN: return BoolArrayType();
                case IDENTIFIER: return ArrayType(typeName);
                default:
                    exit("dont support void array");
            }
        }
        switch (lastToken){
            case INT: return IntType();
            case BOOLEAN: return BoolType();
            case VOID: return VoidType();
            default: return ClassType(typeName);
        }
    }

    List<VarDecl> parseVarDecls(boolean isClassVar){
        List<VarDecl> varDecls = new ArrayList<>();
        List<Token> varToken = isClassVar ? Arrays.asList(STATIC, FIELD): Collections.singletonList(VAR);
        while (varToken.contains(s.token())){
            VarType varType = s.token() == STATIC ? VarType.STATIC : s.token() == FIELD ? VarType.FIELD: VarType.VAR;
            s.nextToken();
            TypeDecl typeDecl = parseType(Arrays.asList(INT, BOOLEAN, IDENTIFIER));
            String varName = accept(IDENTIFIER);
            List<String> varNames = new ArrayList<>();
            varNames.add(varName);
            while (s.token() == COMMA){
                s.nextToken();
                varNames.add(accept(IDENTIFIER));
            }
            accept(SEMI);
            varDecls.add(VarDecl(varType, typeDecl, varNames));
        }
        return varDecls;
    }

    List<SubroutineDecl> parseSubroutines(){
        List<SubroutineDecl> subroutineDecls = new ArrayList<>();
        while (s.token() == CONSTRUCTOR || s.token() == FUNCTION || s.token() == METHOD){
            existReturn = false;
            SubroutineType subroutineType = s.token() == FUNCTION ? SubroutineType.FUNCTION : SubroutineType.METHOD;
            TypeDecl typeDecl;
            String subroutineName;
           if(s.token() == CONSTRUCTOR){
              typeDecl = VoidType();
              subroutineName = CONSTRUCTOR.name;
              s.nextToken();
           }else {
               s.nextToken();
               typeDecl = parseType(Arrays.asList(INT, BOOLEAN, VOID, IDENTIFIER));
               subroutineName = accept(IDENTIFIER);
           }
            accept(LPAREN);
            List<VarDecl> parameterDecls = parseParameter();
            accept(RPAREN);
            accept(LBRACE);
            List<VarDecl> varDecls = parseVarDecls(false);
            List<Statement> statements = parseStatements();
            accept(RBRACE);
            if(!existReturn){
                if (typeDecl != TypeDecl.voidType){
                    Utils.exit(s.fileName() + ": subroutine " + subroutineName + ": lack of return statement");
                }
                statements.add(ReturnStatement(null));
            }
            subroutineDecls.add(SubroutineDecl(subroutineType, typeDecl, subroutineName, parameterDecls, varDecls, statements));
        }
        return subroutineDecls;
    }

    List<VarDecl> parseParameter(){
        List<VarDecl> parameterDecls = new ArrayList<>();
        TypeDecl typeDecl;
        String parameterName;
        if (s.token() != RPAREN){
            typeDecl = parseType(Arrays.asList(INT, BOOLEAN, IDENTIFIER));
            parameterName = accept(IDENTIFIER);
            parameterDecls.add(ParameterDecl(typeDecl, parameterName));
            while (s.token() == COMMA){
                s.nextToken();
                typeDecl = parseType(Arrays.asList(INT, BOOLEAN, IDENTIFIER));
                parameterName = accept(IDENTIFIER);
                parameterDecls.add(ParameterDecl(typeDecl, parameterName));
            }
        }
        return parameterDecls;
    }


    List<Statement> parseStatements(){
        List<Token> tokens = Arrays.asList(LET, DO, IF, WHILE, RETURN);
        List<Statement> statements = new ArrayList<>();
        while (tokens.contains(s.token())){
            Statement statement;
            if(s.token() == LET) statement = parseLet();
            else if(s.token() == DO) statement = parseDo();
            else if(s.token() == IF) statement = parseIf();
            else if(s.token() == WHILE) statement = parseWhile();
            else statement = parseReturn();
            statements.add(statement);
        }
        return statements;
    }

    LetStatement parseLet(){
        accept(LET);
        Expression varName = parseIdentifier();
        accept(EQ);
        Expression init = parseExpression();
        accept(SEMI);
        return LetStatement(varName, init);
    }

    DoStatement parseDo(){
        accept(DO);
        SubroutineCall subroutineCall = parseSubroutineCall();
        accept(SEMI);
        return DoStatement(subroutineCall);
    }

    IfStatement parseIf(){
        accept(IF);
        accept(LPAREN);
        Expression condition = parseExpression();
        accept(RPAREN);
        accept(LBRACE);
        List<Statement> thenPart = parseStatements();
        accept(RBRACE);
        List<Statement> elsePart = new ArrayList<>();
        if(s.token() == ELSE){
            s.nextToken();
            accept(LBRACE);
            elsePart = parseStatements();
            accept(RBRACE);
        }
        return IfStatement(condition, thenPart, elsePart);
    }

    WhileStatement parseWhile(){
        accept(WHILE);
        accept(LPAREN);
        Expression condition = parseExpression();
        accept(RPAREN);
        accept(LBRACE);
        List<Statement> body = parseStatements();
        accept(RBRACE);
        return WhileStatement(condition, body);
    }

    ReturnStatement parseReturn(){
        existReturn = true;
        accept(RETURN);
        if(s.token() == SEMI){
            s.nextToken();
            return ReturnStatement(null);
        }
        Expression value = parseExpression();
        accept(SEMI);
        return ReturnStatement(value);
    }

    Expression parseIdentifier(){
        Token lastToken = s.token();
        String name = accept(Arrays.asList(IDENTIFIER, THIS));
        Expression expr;
        if(lastToken == THIS) expr = KeyWordConstant(KeyWord.THIS);
        else expr = Identifier(name);
        while (s.token() == DOT){
            s.nextToken();
            String fieldName = accept(IDENTIFIER);
            expr = FieldAccess(fieldName, expr);
        }
        isArrayAccess = false;
        if(s.token() == LBRACKET){
            s.nextToken();
            Expression index = parseExpression();
            accept(RBRACKET);
            isArrayAccess = true;
            return ArrayAccess(expr, index);
        }
        return expr;
    }

    SubroutineCall parseSubroutineCall(){
        Expression subroutineName = parseIdentifier();
        if(isArrayAccess) exit("cant call array subroutine");
        return SubroutineCall(subroutineName, parseArgs());
    }

    List<Expression> parseArgs(){
        accept(LPAREN);
        List<Expression> args = new ArrayList<>();
        if(s.token() != RPAREN){
            args.add(parseExpression());
            while (s.token() == COMMA){
                s.nextToken();
                args.add(parseExpression());
            }
        }
        accept(RPAREN);
        return args;
    }

    Expression parseExpression(){
        Expression expr = parseTerm();
        if(s.token().op == null) return expr;
        Deque<Expression> exprStack = new ArrayDeque<>();
        Deque<Op> opStack = new ArrayDeque<>();
        exprStack.addLast(expr);
        opStack.addLast(Op.NULL);
        while (s.token().op != null){
            Op topOp = s.token().op;
            s.nextToken();
            Expression topExpr = parseTerm();
            opStack.addLast(topOp);
            exprStack.addLast(topExpr);
            while (topOp != Op.NULL &&
                    (s.token().op == null || Op.opPriority.get(topOp) >= Op.opPriority.get(s.token().op))
                ){
                topOp = opStack.pollLast();
                Expression expr2 = exprStack.pollLast();
                Expression expr1 = exprStack.pollLast();
                expr = Binary(topOp, expr1, expr2);
                exprStack.addLast(expr);
                topOp = opStack.getLast();
            }
        }
        return expr;
    }

    Expression parseTerm(){
        switch (s.token()){
            case INTCONSTANT:
                String number = accept(INTCONSTANT);
                return IntegerConstant(Integer.parseUnsignedInt(number));
            case STRINGCONSTANT:
                String value = accept(STRINGCONSTANT);
                return StringConstant(value);
            case TRUE:
                s.nextToken();
                return KeyWordConstant(KeyWord.TRUE);
            case FALSE:
                s.nextToken();
                return KeyWordConstant(KeyWord.FALSE);
            case NULL:
                s.nextToken();
                return KeyWordConstant(KeyWord.NULL);
            case LPAREN:
                s.nextToken();
                Expression expr = parseExpression();
                accept(RPAREN);
                return Parens(expr);
            case SUB: case TILDE: case BANG:
                Op op = s.token() == SUB ? Op.NEG : s.token() == TILDE ? Op.NOT : Op.BOOLNOT;
                s.nextToken();
                Expression term = parseTerm();
                return Unary(op, term);
            case NEW:
                s.nextToken();
                Token lastToken = s.token();
                String typeName = accept(Arrays.asList(INT, BOOLEAN, IDENTIFIER));
                switch (s.token()){
                    case LBRACKET:
                        s.nextToken();
                        Expression size = parseExpression();
                        accept(RBRACKET);
                        if(lastToken == INT) return NewArray(IntType(), size);
                        else if(lastToken == BOOLEAN) return NewArray(BoolType(), size);
                        else return NewArray(ClassType(typeName), size);
                    case LPAREN:
                        if(lastToken != IDENTIFIER) expectedError(Collections.singletonList(IDENTIFIER));
                        List<Expression> newArgs = parseArgs();
                        return NewClass(typeName, newArgs);
                    default:
                        expectedError(Arrays.asList(LBRACKET, LBRACKET));
                }
            case THIS:
            case IDENTIFIER:
                Expression expr2 = parseIdentifier();
                if(s.token() == LPAREN){
                    if(isArrayAccess) exit("cant call array subroutine");
                    return SubroutineCall(expr2, parseArgs());
                }
                return expr2;
            default:
                expectedError(Arrays.asList(INTCONSTANT, STRINGCONSTANT,
                        TRUE, FALSE, NULL, LPAREN, SUB, TILDE, BANG, NEW, THIS, IDENTIFIER));
                return null;
        }
    }

    String accept(Token token){
        if(s.token() != token){
            expectedError(Collections.singletonList(token));
        }
        String name = s.name();
        s.nextToken();
        return name;
    }

    String accept(List<Token> tokens){
        if(!tokens.contains(s.token())){
            expectedError(tokens);
        }
        String name = s.name();
        s.nextToken();
        return name;
    }

    void expectedError(List<Token> tokens){
        Utils.exit(s.fileName(), s.line(), s.linePos() - s.name().length(), "expected " +
                tokens.stream().map(token -> token.name).collect(Collectors.joining("|"))
                );
    }

    void exit(String msg){
        Utils.exit(s.fileName(), s.line(), s.linePos() - s.name().length(), msg);
    }

}
