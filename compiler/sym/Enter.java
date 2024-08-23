package sym;

import tree.ClassDecl;
import tree.Tree;
import tree.Visitor;
import utils.Utils;

import static sym.Symbol.*;
import static sym.Type.*;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: 符号收集第一阶段
 */
public class Enter extends Visitor {

    Scope rootScope;

    public Enter(Scope rootScope){
        this.rootScope = rootScope;
    }

    public void classEnter(Tree tree){
            tree.accept(this);
    }

    @Override
    public void visitClassDecl(ClassDecl that) {
       ClassSymbol classSymbol = new ClassSymbol();
       classSymbol.name = that.className;
       classSymbol.scope = new Scope(classSymbol);
       classSymbol.tree = that;
       classSymbol.type = new ClassType(classSymbol);
       rootScope.put(that.className, classSymbol, () -> Utils.exit("class name duplicate: " + that.className));
    }
}
