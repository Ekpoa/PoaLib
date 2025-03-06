package poa.poalib.general;

import java.lang.management.ManagementFactory;

public class General {


    public static long getServerUptime(){
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

}
