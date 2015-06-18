package summarizer.summarizer;

import summarizer.model.PRCollection;
import summarizer.model.Phrase;
import summarizer.model.Review;
import summarizer.test.PreHandle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by FlyFish on 2014/12/29.
 * This is a summarizer which uses the greedy algorithm.
 */

public class GreedySummarizer implements Summarizer {
    public int K = 20;
    public double alpha = 1000.0;
    public double beta = 1000.0;

    private HashMap<Phrase, PRCollection> phraseReviews;
    private HashMap<Phrase, Double> specifyScore;

    public GreedySummarizer(HashMap<Phrase, PRCollection> phraseReviews) {
        this.phraseReviews = phraseReviews;
        this.specifyScore = PreHandle.calcSpecificScore(phraseReviews);
    }

    public Set<Phrase> summarize() {

        Set<Phrase> candidate = new HashSet<Phrase>(phraseReviews.keySet());
        Set<Phrase> chosen = new HashSet<Phrase>();

        while (chosen.size()<= K && candidate.size()>0) {
            Phrase p = getMaxIncrement(chosen, candidate);
            chosen.add(p);
            //System.err.printf("choose %s,\t\t\tscore=%6.2f\n", p, getObjectScore(chosen));
            candidate.remove(p);
        }

        return chosen;
    }

    private Phrase getMaxIncrement(Set<Phrase> chosen, Set<Phrase> remaining) {
        double currentScore = getObjectScore(chosen);
        double maxIncrement = 0;
        Phrase maxIncrementPhrase = null;

        for (Phrase p : remaining) {
            Set<Phrase> temp = new HashSet<Phrase>(chosen);
            temp.add(p);
            double newScore = getObjectScore(temp);
            if (newScore - currentScore > maxIncrement) {
                maxIncrement = newScore - currentScore;
                maxIncrementPhrase = p;
            }
        }
        return maxIncrementPhrase;
    }

    private double getObjectScore(Set<Phrase> phrases) {
        Set<Review> allReviews = new HashSet<Review>();
        double ss = 0;

        for (Phrase p : phrases) {

            allReviews.addAll(phraseReviews.get(p).reviews);


            ss += specifyScore.get(p);
        }

        //System.err.printf("%d\t %6.4f\t%6.4f\n", allReviews.size(), alpha * ss, beta * PreHandle.polarityScore(phrases));
        return allReviews.size() + (alpha * ss) + (beta * PreHandle.polarityScore(phrases));
    }

}