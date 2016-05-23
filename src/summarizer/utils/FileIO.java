package summarizer.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by atone on 14/12/29.
 * This class handles file input/output.
 */
public class FileIO {

    public static JSONObject readJSON(String filename) {
        String content = readFile(filename);
        JSONObject ret = null;
        try {
            ret = new JSONObject(content);
        } catch (JSONException e) {
            System.err.println("Can't parse content to JSON!");
        }
        return ret;
    }

    public static Map<String, Double> readPhraseScoreMap(String filename) {
        List<String> lines = readLines(filename);
        Map<String, Double> phraseScoreMap = new HashMap<String, Double>(lines.size());
        for (String line : lines) {
            String[] pieces = line.split(":");
            String word = pieces[0];
            double sf = Double.parseDouble(pieces[1]);
            phraseScoreMap.put(word, sf);
        }
        return phraseScoreMap;
    }

    /**
     * Read lines of a file and return a list where each element is a line.
     * @param filename the file to read
     * @return the read lines
     */
    public static List<String> readLines(String filename) {
        List<String> ret = new ArrayList<String>();
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
                sb.append(line).append("\n");
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
