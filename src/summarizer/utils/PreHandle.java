package summarizer.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import summarizer.model.Phrase;
import summarizer.model.Review;

import java.util.*;

/**
 * Created by atone on 16/1/16.
 * This class does some pre-handle work
 */
public class PreHandle {
    public static List<Phrase> getPhrases(JSONObject jsonObject) {
        List<Phrase> phrases = new ArrayList<Phrase>();
        JSONArray jsonPhrases = jsonObject.getJSONArray("Phrase");
        for (int i = 0; i < jsonPhrases.length(); i++) {
            JSONObject phrase = jsonPhrases.getJSONObject(i);
            int aspectID = phrase.getInt("#AspectID");
            JSONArray posPhrases = phrase.getJSONArray("#pos_topphrase");
            for (int clusterID = 0; clusterID < posPhrases.length(); clusterID++) {
                JSONObject posPhrase = posPhrases.getJSONObject(clusterID);
                JSONArray original = posPhrase.getJSONArray("original");
                for (int j = 0; j < original.length(); j++) {
                    JSONObject oriPhrase = original.getJSONObject(j);
                    String content = oriPhrase.getString("phrase");
                    JSONArray orevids = oriPhrase.getJSONArray("orevids");
                    Set<Review> reviews = new HashSet<Review>();
                    for (int k = 0; k < orevids.length(); k++) {
                        JSONObject orevid = orevids.getJSONObject(k);
                        String id = orevid.getString("orevid");
                        reviews.add(new Review(id));
                    }
                    phrases.add(new Phrase(aspectID, clusterID, 1, content, reviews));
                }
            }
            JSONArray negPhrases = phrase.getJSONArray("#neg_topphrase");
            int offset = posPhrases.length();
            for (int clusterID = offset; clusterID < offset + negPhrases.length(); clusterID++) {
                JSONObject negPhrase = negPhrases.getJSONObject(clusterID - offset);
                JSONArray original = negPhrase.getJSONArray("original");
                for (int j = 0; j < original.length(); j++) {
                    JSONObject oriPhrase = original.getJSONObject(j);
                    String content = oriPhrase.getString("phrase");
                    JSONArray orevids = oriPhrase.getJSONArray("orevids");
                    Set<Review> reviews = new HashSet<Review>();
                    for (int k = 0; k < orevids.length(); k++) {
                        JSONObject orevid = orevids.getJSONObject(k);
                        String id = orevid.getString("orevid");
                        reviews.add(new Review(id));
                    }
                    phrases.add(new Phrase(aspectID, clusterID, -1, content, reviews));
                }
            }
        }
        return phrases;
    }

    public static Map<String, Set<Phrase>> getReviewMap(List<Phrase> phrases, List<String> reviewList) {
        Map<String, Set<Phrase>> reviewMap = new HashMap<String, Set<Phrase>>();
        for (Phrase phrase : phrases) {
            for (Review r : phrase.reviews) {
                String review = reviewList.get(r.getIndex());
                if (reviewMap.containsKey(review)) {
                    Set<Phrase> phraseSet = reviewMap.get(review);
                    phraseSet.add(phrase);
                } else {
                    Set<Phrase> phraseSet = new HashSet<Phrase>();
                    phraseSet.add(phrase);
                    reviewMap.put(review, phraseSet);
                }
            }
        }
        return reviewMap;
    }

    public static Map<Phrase, Double> calcSpecificity(List<Phrase> phrases) {
        Map<Phrase, Double> score = new HashMap<Phrase, Double>();
        for (Phrase p : phrases) {
            int opCount = 0;
            int asopCount = 0;

            int aspectID = p.aspectID;
            String opinion = p.opinion;
            for (Phrase phrase : phrases) {
                if (phrase.opinion.contains(opinion) || opinion.contains(phrase.opinion)) {
                    opCount += phrase.reviews.size(); //opinion词相同的短语出现次数
                    if (phrase.aspectID == aspectID) {
                        asopCount += phrase.reviews.size(); //属于该aspect下,且opinion词相同的短语出现次数
                    }
                }
            }
            score.put(p, (double) asopCount / opCount);
        }
        return score;
    }
}
