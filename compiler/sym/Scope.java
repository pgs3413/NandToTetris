package sym;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: scope
 */
public class Scope {

    public Map<String, Symbol> table;
    public Scope next;
    public Symbol sym;

    public Scope(Scope next, Symbol symbol){
        table = new HashMap<>();
        this.next = next;
        this.sym = symbol;
    }

}
