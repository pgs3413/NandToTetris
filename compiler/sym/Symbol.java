package sym;

import tree.ClassDecl;
import tree.SubroutineType;
import tree.VarType;

import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: symbol
 */
public abstract class Symbol {

    public String name;
    public Type type;

    public static class ClassSymbol extends Symbol {

        public Scope scope;
        public ClassDecl tree;

    }

    public static class VarSymbol extends Symbol {

        public VarType varType;
        public int index;

    }

    public static class SubroutineSymbol extends Symbol {

        public Scope scope;
        public SubroutineType subroutineType;
        public List<VarSymbol> params;


    }


}
