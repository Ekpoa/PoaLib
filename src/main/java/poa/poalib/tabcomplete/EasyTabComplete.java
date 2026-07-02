package poa.poalib.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EasyTabComplete {

    public static List<String> correctTabComplete(List<String> list, String arg){
        List<String> tr = new ArrayList<>();
        for(String s : list)
            if(s.toLowerCase().startsWith(arg.toLowerCase()))
                tr.add(s);
        return tr;
    }

    public static List<String> correctTabComplete(String arg, List<String> list){
        return correctTabComplete(list, arg);
    }

    public static List<String> correctTabComplete(String arg, String... completions){
        return correctTabComplete(arg, Arrays.stream(completions).toList());
    }



}
