package summarizer.test;

import summarizer.model.PRCollection;
import summarizer.model.Phrase;
import summarizer.model.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by atone on 15/1/21.
 * This class provides some static methods to pre-handle some missions.
 */
public class PreHandle {

    public static HashMap<Phrase, PRCollection> getPhrase_PRCollection(ArrayList<String> recordStrings) {
        HashMap<Phrase, PRCollection> phraseReview = new HashMap<Phrase, PRCollection>();
        for (String line : recordStrings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
                continue;
            }

            Review review = new Review(s[9], s[10], s[1]);
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], s[5], s[6], Integer.parseInt(s[8]));
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

    public static HashMap<Review, HashSet<Phrase>> getReview_PhraseCollection(ArrayList<String> recordStrings) {
        HashMap<Review, HashSet<Phrase>> reviewPhrase = new HashMap<Review, HashSet<Phrase>>();
        for (String line : recordStrings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
                continue;
            }

            Review review = new Review(s[9], s[10], s[1]);
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], s[5], s[6], Integer.parseInt(s[8]));
            if (reviewPhrase.containsKey(review)) {
                HashSet<Phrase> phraseSet = reviewPhrase.get(review);
                phraseSet.add(phrase);
            } else {
                HashSet<Phrase> phraseSet = new HashSet<Phrase>();
                phraseSet.add(phrase);
                reviewPhrase.put(review, phraseSet);
            }
        }
        return reviewPhrase;
    }



    public static HashMap<Phrase, Double> calcSpecificScore(HashMap<Phrase, PRCollection> phraseReviews) {
        HashMap<Phrase, Double> specificScoreMap = new HashMap<Phrase, Double>();
        Set<Phrase> phrases = phraseReviews.keySet();

        for (Phrase p : phrases) {
            String op = p.opinion_adj;
            String as = p.aspect;

            int opCount = 0;
            int asopCount = 0;

            for (Phrase phrase : phrases) {
                if (phrase.opinion_adj.contains(op)) {
                    opCount += phraseReviews.get(phrase).phrases.size();
                    if (phrase.aspect.equals(as)) {
                        asopCount += phraseReviews.get(phrase).phrases.size();
                    }
                }
            }

            specificScoreMap.put(p, (double)asopCount / opCount);
            //System.out.println(as + " " + op + " asopCount=" + asopCount + " opCount=" + opCount);
        }
        return specificScoreMap;
    }


}
