package poa.poalib.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class EasyTabComplete {

    public static List<String> correctTabComplete(List<String> list, String arg){
        List<String> tr = new ArrayList<>();
        for(String s : list)
            if(s.toLowerCase().startsWith(arg.toLowerCase()))
                tr.add(s);
        return tr;
    }

    public static List<String> correctTabComplete(String arg, String... completions){
        List<String> tr = new ArrayList<>();
        for(String s : completions)
            if(s.toLowerCase().startsWith(arg.toLowerCase()))
                tr.add(s);
        return tr;
    }

}
