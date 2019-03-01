package cl.architeq.displayParking.util;

import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Util {

    public static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }


    // NO funciona en windows, solo en Linux ..
    public static boolean pingIpAddr(String ipAddr) {
        boolean bln = false;
        try {
            InetAddress ping = InetAddress.getByName(ipAddr);
            bln = (ping.isReachable(5000)) ? true : false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bln;
    }

}
