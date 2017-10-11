package schmitt.mmas.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class LogFile {

    private static boolean log = Boolean.FALSE;

    public static void writeInFile(int fromId, int toId, String msg) {
        if(log) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        "/home/joao/projects/master-degree/mmas-vrp/statistics/" + fromId + "->" + toId + ".txt", true));
                bw.write(msg);
                bw.newLine();
                bw.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
