package summarizer.evaluation;

import org.json.JSONArray;
import org.json.JSONObject;
import summarizer.utils.FileIO;

/**
 * Created by atone on 16/2/21.
 * 统计每一种产品中,正向短语,负向短语,总短语的数目
 */
public class Statistics {
    public static void main(String[] args) {
        int[] ids = new int[] {2912, 2914, 2915, 2916, 2922, 2925, 2929, 2931, 2936, 2940};
        for (int id : ids) {
            String phraseFile = String.format("data/phrases_new/%d_phrases.json", id);
            System.out.print("id_" + id + ": ");
            printStatistics(FileIO.readJSON(phraseFile));
        }
    }

    public static void printStatistics(JSONObject jsonObject) {
        int positive = 0;
        int negative = 0;
        int total = 0;
        JSONArray phrases = jsonObject.getJSONArray("Phrase");
        for (int i = 0; i < phrases.length(); i++) {
            JSONObject phrase = phrases.getJSONObject(i);
            positive += phrase.getInt("#positive");
            negative += phrase.getInt("#negative");
            total += phrase.getInt("#totalfrequency");
        }
        System.out.printf("positive: %d\tnegative: %d\ttotal: %d\n", positive, negative, total);
    }
}
