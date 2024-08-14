package sym;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: scope
 */
public class Scope {

    Map<String, Symbol> table;
    Scope next;

    public Scope(Scope next){
        table = new HashMap<>();
        this.next = next;
    }

}
