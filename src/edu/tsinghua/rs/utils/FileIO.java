package edu.tsinghua.rs.utils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by atone on 14/12/29.
 */
public class FileIO {
    /**
     * Read lines of a file and return a list where each element is a line.
     * @param filename the file to read
     * @return the read lines
     */
    public static ArrayList<String> readLines(String filename) {
        ArrayList<String> ret = new ArrayList<String>();
        String line;

        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                ret.add(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Read the content of a file and return the content as a string
     * @param filename the file to read
     * @return the string which contains the content of the file
     */
    public static String readFile(String filename) {
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeFile(String filename, String toWrite) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
        out.print(toWrite);
        out.close();
    }
}
