package sym;

import java.util.*;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: scope
 */
public class Scope {

    private final Map<String, Symbol> table = new HashMap<>();
    public Symbol symbol;

    public Scope(Symbol symbol){
        this.symbol = symbol;
    }

    public Symbol get(String name, Runnable f){
        Symbol symbol = table.get(name);
        if(symbol == null){
            f.run();
            return null;
        }
        return symbol;
    }

    public void put(String name, Symbol symbol, Runnable f){
        if(table.containsKey(name)){
            f.run();
        }
        table.put(name, symbol);
    }

    public Collection<Symbol> allSymbols(){
        return table.values();
    }

}
