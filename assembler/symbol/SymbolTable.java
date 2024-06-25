package symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pangs
 * @Date: 2024/6/25
 * @description: Assembler SymbolTable
 */
public class SymbolTable {

    private Map<String, String> table = new HashMap<>();

    public SymbolTable(){
        table.put("SP", convert(0));
        table.put("LCL", convert(1));
        table.put("ARG", convert(2));
        table.put("THIS", convert(3));
        table.put("THAT", convert(4));
        table.put("SCREEN", convert(0x4000));
        table.put("KBD", convert(0x6000));
        for(int i = 0; i <= 15; i++){
            table.put("R" + i, convert(i));
        }
    }

    public String convert(int address){
        if(address < 0 || address > 65535){
            System.err.println("address非法：" + address);
            System.exit(-1);
        }
        StringBuilder result = new StringBuilder();
        for(int i = 14; i >= 0; i--){
            result.append((address >>> i & 1) > 0 ? "1" : "0");
        }
        return result.toString();
    }

    public void addEntry(String symbol, int address){
        table.put(symbol, convert(address));
    }

    public boolean contains(String symbol){
        return table.containsKey(symbol);
    }

    public String getAddress(String symbol){
        return table.get(symbol);
    }

}
