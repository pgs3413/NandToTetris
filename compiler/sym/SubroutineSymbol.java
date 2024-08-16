package sym;

import tree.SubroutineType;

import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: method symbol
 */
public class SubroutineSymbol extends Symbol{

    Scope scope;
    SubroutineType subroutineType;
    Type returnType;
    List<VarSymbol> params;


}
