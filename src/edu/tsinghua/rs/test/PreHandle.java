package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.PRCollection;
import edu.tsinghua.rs.data.Phrase;
import edu.tsinghua.rs.data.Review;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by atone on 15/1/21.
 */
public class PreHandle {
    public static HashMap<Phrase, PRCollection> getPhraseReviewCollection(ArrayList<String> recordStrings) {
        HashMap<Phrase, PRCollection> phraseReview = new HashMap<Phrase, PRCollection>();
        for (String line : recordStrings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
            }

            Review review = new Review(s[9], s[10], s[1]);
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], s[5], s[6]);
            if (phraseReview.containsKey(phrase)) {
                PRCollection collection = phraseReview.get(phrase);
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
            }
            else {
                PRCollection collection = new PRCollection();
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
                phraseReview.put(phrase, collection);
            }
        }
        return phraseReview;
    }

    public static HashMap<Phrase, Double> calcSpecificScore(HashMap<Phrase, PRCollection> phraseReviews) {
        HashMap<Phrase, Double> specificScoreMap = new HashMap<Phrase, Double>();

        return specificScoreMap;
    }

}
