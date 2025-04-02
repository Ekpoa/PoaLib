package poa.poalib.messages;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import poa.poalib.PoaLib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Log {

    private static final ConcurrentMap<File, PrintWriter> map = new ConcurrentHashMap<>();

    @SneakyThrows
    public static void logToFile(File file, String string, boolean addTime){
        final String[] text = {string};
        Bukkit.getScheduler().runTaskAsynchronously(PoaLib.libINSTANCE, () -> {
            try {

                file.mkdirs();
                file.createNewFile();

                PrintWriter printWriter;
                if (map.containsKey(file))
                    printWriter = map.get(file);
                else {
                    printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                    map.put(file, printWriter);
                }
                if (addTime)
                    text[0] = new Date(System.currentTimeMillis()) + ": " + text[0];

                printWriter.println(text[0]);
                printWriter.flush();
            }
            catch (Exception ignored){}
    });
    };

}
