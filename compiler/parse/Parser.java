package parse;

import token.Scanner;
import token.Token;
import tree.*;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public Parser(Path source) throws IOException {
        s = new Scanner(source);
        s.nextToken();
    }

    public Tree parse(){
        accept(CLASS);
        String className = accept(IDENTIFIER);
        accept(LBRACE);
        List<VarDecl> varDecls = parseClassVar();
        List<SubroutineDecl> subroutineDecls = parseSubroutines();
        accept(RBRACE);
        accept(EOF);
        return ClassDecl(className, varDecls, subroutineDecls);
    }

    TypeDecl parseType(List<Token> tokens){
        Token lastToken = s.token();
        String typeName = accept(tokens);
        TypeDecl typeDecl;
        if(lastToken == INT) typeDecl = IntType();
        else if(lastToken == CHAR) typeDecl = CharType();
        else if(lastToken == BOOLEAN) typeDecl = BoolType();
        else if(lastToken == VOID) typeDecl = VoidType();
        else typeDecl = ClassType(typeName);
        return typeDecl;
    }

    List<VarDecl> parseClassVar(){
        List<VarDecl> varDecls = new ArrayList<>();
        while (s.token() == STATIC || s.token() == FIELD){
            VarType varType = s.token() == STATIC ? VarType.STATIC : VarType.FIELD;
            s.nextToken();
            TypeDecl typeDecl = parseType(Arrays.asList(INT, CHAR, BOOLEAN, IDENTIFIER));
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
            SubroutineType subroutineType = s.token() == CONSTRUCTOR ? SubroutineType.CONSTRUCTOR :
                    s.token() == FUNCTION ? SubroutineType.FUNCTION : SubroutineType.METHOD;
            s.nextToken();
            TypeDecl typeDecl = parseType(Arrays.asList(INT, CHAR, BOOLEAN, VOID, IDENTIFIER));
            String subroutineName = accept(IDENTIFIER);
            accept(LPAREN);
            List<ParameterDecl> parameterDecls = parseParameter();
            accept(RPAREN);
            accept(LBRACE);
            List<VarDecl> varDecls = parseSubroutineVar();
            List<Statement> statements = parseStatements();
            accept(RBRACE);
            subroutineDecls.add(SubroutineDecl(subroutineType, typeDecl, subroutineName, parameterDecls, varDecls, statements));
        }
        return subroutineDecls;
    }

    List<ParameterDecl> parseParameter(){
        List<ParameterDecl> parameterDecls = new ArrayList<>();
        TypeDecl typeDecl;
        String parameterName;
        if (s.token() != RPAREN){
            typeDecl = parseType(Arrays.asList(INT, CHAR, BOOLEAN, IDENTIFIER));
            parameterName = accept(IDENTIFIER);
            parameterDecls.add(ParameterDecl(typeDecl, parameterName));
            while (s.token() == COMMA){
                s.nextToken();
                typeDecl = parseType(Arrays.asList(INT, CHAR, BOOLEAN, IDENTIFIER));
                parameterName = accept(IDENTIFIER);
                parameterDecls.add(ParameterDecl(typeDecl, parameterName));
            }
        }
        return parameterDecls;
    }

    List<VarDecl> parseSubroutineVar(){
        List<VarDecl> varDecls = new ArrayList<>();
        while (s.token() == VAR){
            s.nextToken();
            TypeDecl typeDecl = parseType(Arrays.asList(INT, CHAR, BOOLEAN, IDENTIFIER));
            String varName = accept(IDENTIFIER);
            List<String> varNames = new ArrayList<>();
            varNames.add(varName);
            while (s.token() == COMMA){
                s.nextToken();
                varNames.add(accept(IDENTIFIER));
            }
            accept(SEMI);
            varDecls.add(VarDecl(VarType.VAR, typeDecl, varNames));
        }
        return varDecls;
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
        Identifier varName = parseIdentifier();
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
        List<Statement> elsePart = null;
        if(s.token() == ELSE){
            accept(ELSE);
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
        accept(RETURN);
        if(s.token() == SEMI){
            accept(SEMI);
            return ReturnStatement(null);
        }
        Expression value = parseExpression();
        accept(SEMI);
        return ReturnStatement(value);
    }

    Identifier parseIdentifier(){
        String name = accept(IDENTIFIER);
        if(s.token() == DOT){
            accept(DOT);
            String fieldName = accept(IDENTIFIER);
            return FieldAccess(fieldName, Identifier(name));
        }
        if(s.token() == LBRACKET){
            accept(LBRACKET);
            Expression index = parseExpression();
            accept(RBRACKET);
            return ArrayAccess(name, index);
        }
        return Identifier(name);
    }

    SubroutineCall parseSubroutineCall(){
        Identifier subroutineName = parseIdentifier();
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
        return SubroutineCall(subroutineName, args);
    }

    Expression parseExpression(){
        Term term1 = parseTerm();
        if(Arrays.asList(PLUS, SUB, STAR, SLASH, AND, OR, LT, GT, EQ, NOT).contains(s.token())){
            Token lastToken = s.token();
            s.nextToken();
            Term term2 = parseTerm();
            Op op;
            switch (lastToken){
                case PLUS: op = Op.PLUS;break;
                case SUB: op = Op.SUB;break;
                case STAR: op = Op.STAR;break;
                case SLASH: op = Op.SLASH;break;
                case AND: op = Op.AND;break;
                case OR: op = Op.OR;break;
                case LT: op = Op.LT;break;
                case GT: op = Op.GT;break;
                case EQ: op = Op.EQ;break;
                default: op = Op.NOT;
            }
            return Binary(op, term1, term2);
        }
        return term1;
    }

    Term parseTerm(){
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
            case THIS:
                s.nextToken();
                return KeyWordConstant(KeyWord.THIS);
            case LPAREN:
                accept(LPAREN);
                Expression expr = parseExpression();
                accept(RPAREN);
                return Parens(expr);
            case SUB: case NOT:
                Op op = s.token() == SUB ? Op.SUB : Op.NOT;
                s.nextToken();
                Term term = parseTerm();
                return Unary(op, term);
            case IDENTIFIER:
                Identifier identifier = parseIdentifier();
                if(s.token() == LPAREN){
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
                    return SubroutineCall(identifier, args);
                }
                return identifier;
            default:
                expectedError(Arrays.asList(INTCONSTANT, STRINGCONSTANT,
                        TRUE, FALSE, NULL, THIS, LPAREN, SUB, NOT, IDENTIFIER));
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
        Utils.exit(s.line(), s.linePos() - s.name().length(), "expected " +
                tokens.stream().map(token -> token.name).collect(Collectors.joining("|"))
                );
    }

}
