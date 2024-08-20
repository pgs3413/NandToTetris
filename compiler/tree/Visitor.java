package tree;

import utils.Utils;
import static tree.Statement.*;
import static tree.Expression.*;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: tree visitor
 */
public abstract class Visitor {

    public void visitTree(Tree that){
        Utils.exit("访问到abstract visitor visitTree");
    }

    public void visitClassDecl(ClassDecl that){
        visitTree(that);
    }

    public void visitVarDecl(VarDecl that){
        visitTree(that);
    }

    public void visitSubroutineDecl(SubroutineDecl that){
        visitTree(that);
    }

    public void visitTypeDecl(TypeDecl that){
        visitTree(that);
    }

    public void visitParameterDecl(ParameterDecl that){
        visitTree(that);
    }

    public void visitLetStatement(LetStatement that){
        visitTree(that);
    }

    public void visitIfStatement(IfStatement that){
        visitTree(that);
    }

    public void visitWhileStatement(WhileStatement that){
        visitTree(that);
    }

    public void visitDoStatement(DoStatement that){
        visitTree(that);
    }

    public void visitReturnStatement(ReturnStatement that){
        visitTree(that);
    }

    public void visitIntegerConstant(IntegerConstant that){
        visitTree(that);
    }

    public void visitStringConstant(StringConstant that){
        visitTree(that);
    }

    public void visitKeyWordConstant(KeyWordConstant that){
        visitTree(that);
    }

    public void visitIdentifier(Identifier that){
        visitTree(that);
    }

    public void visitArrayAccess(ArrayAccess that){
        visitTree(that);
    }

    public void visitSubroutineCall(SubroutineCall that){
        visitTree(that);
    }

    public void visitParens(Parens that){
        visitTree(that);
    }

    public void visitUnary(Unary that){
        visitTree(that);
    }

    public void visitBinary(Binary that){
        visitTree(that);
    }

}
