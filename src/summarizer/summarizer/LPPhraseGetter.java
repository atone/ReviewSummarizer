package summarizer.summarizer;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import summarizer.model.PRCollection;
import summarizer.model.Phrase;
import summarizer.model.Review;
import summarizer.test.PreHandle;

import java.util.*;

/**
 * Created by atone on 15/4/28.
 * This summarizer uses ILP as global optimization methods.
 */
public class LPPhraseGetter implements PhraseGetter {

    public double alpha = 0.001;

    private HashMap<Phrase, PRCollection> phraseReviews;
    private HashSet<Review> reviewSet;
    private HashMap<Phrase, Double> specifyScore;

    public LPPhraseGetter(HashMap<Phrase, PRCollection> phraseReviews) {
        this.phraseReviews = phraseReviews;
        this.reviewSet = new HashSet<Review>();
        for (Map.Entry<Phrase, PRCollection> entry : this.phraseReviews.entrySet()) {
            this.reviewSet.addAll(entry.getValue().reviews);
        }
        this.specifyScore = PreHandle.calcSpecificScore(phraseReviews);
    }

    public Set<Phrase> solve(ArrayList<Phrase> phrases, ArrayList<Review> reviews, HashMap<Phrase, Double> specifyScore) throws LpSolveException {
        LpSolve lp;
        int numPhrase = phrases.size();
        int numReview = reviews.size();
        int nCol = numPhrase + numReview;
        double[] row = new double[nCol + 1];
        lp = LpSolve.makeLp(0, nCol);
        if (lp.getLp() == 0) {
            System.err.println("Error when getLP");
            System.exit(-1);
        }

        lp.setAddRowmode(true);

        // set binary mode
        for (int i=0; i<nCol; i++) {
            lp.setBinary(i+1, true);
        }

        // add consistency constraint
        int[] colNo = new int[2];
        double[] row_temp = new double[2];
        for (int j=0; j<numReview; j++) {
            setZero(row);
            for (int i=0; i<numPhrase; i++) {
                Phrase p = phrases.get(i);
                Review r = reviews.get(j);
                Set<Review> p_reviews = phraseReviews.get(p).reviews;
                if (p_reviews.contains(r)) {
                    row[i+1] = 1;

                    colNo[0] = i+1;
                    row_temp[0] = 1;
                    colNo[1] = numPhrase+1+j;
                    row_temp[1] = -1;
                    lp.addConstraintex(2, row_temp, colNo, LpSolve.LE, 0);
                }
            }
            row[numPhrase+1+j]=-1;
            lp.addConstraint(row, LpSolve.GE, 0);
        }

        // add polarity constraints
        int[] positivePhraseNum = new int[18];
        int[] negativePhraseNum = new int[18];
        for (int i=0; i<numPhrase; i++) {
            Phrase p = phrases.get(i);
            if (p.polarity > 0) {
                positivePhraseNum[p.aspectID]++;
            } else {
                negativePhraseNum[p.aspectID]++;
            }
        }
        colNo = new int[1];
        row_temp = new double[1];
        for (int i=0; i<numPhrase; i++) {
            int aspectID = phrases.get(i).aspectID;
            if (positivePhraseNum[aspectID] >= negativePhraseNum[aspectID]) {
                colNo[0] = i+1;
                row_temp[0] = phrases.get(i).polarity - 1;
                lp.addConstraintex(1, row_temp, colNo, LpSolve.GE, 0);
            } else {
                colNo[0] = i+1;
                row_temp[0] = phrases.get(i).polarity + 1;
                lp.addConstraintex(1, row_temp, colNo, LpSolve.LE, 0);
            }
        }

        // add count constraint
        for (int aspectID = 1; aspectID <= 17; aspectID++) {
            setZero(row);
            for (int i = 0; i < numPhrase; i++) {
                if (phrases.get(i).aspectID == aspectID) {
                    row[i + 1] = 1;
                }
            }
            lp.addConstraint(row, LpSolve.LE, 2); //同一aspect出现次数不多于2
        }

        // add aspect constraint, any 2 phrases that have the same aspect word will only be chosen one.
        colNo = new int[2];
        row_temp = new double[2];
        for (int i = 0; i < numPhrase - 1; i++) {
            for (int j = i + 1; j < numPhrase; j++) {
                Phrase pi = phrases.get(i);
                Phrase pj = phrases.get(j);
                if (pi.aspectID == pj.aspectID && pi.aspect.equals(pj.aspect)) {
                    colNo[0] = i + 1;
                    colNo[1] = j + 1;
                    row_temp[0] = 1;
                    row_temp[1] = 1;
                    lp.addConstraintex(2, row_temp, colNo, LpSolve.LE, 1);
                }
            }
        }

        lp.setAddRowmode(false);

        // set objective function
        setZero(row);
        for (int i=0; i<numPhrase; i++) {
            row[i+1] = specifyScore.get(phrases.get(i));
        }
        for (int j=0; j<numReview; j++) {
            row[numPhrase+1+j] = alpha;
        }
        lp.setObjFn(row);
        lp.setMaxim();

        //lp.writeLp("data/model.lp");
        lp.setVerbose(LpSolve.IMPORTANT);

        double[] variables = new double[nCol];
        if (lp.solve() == LpSolve.OPTIMAL) {
            lp.getVariables(variables);
        } else {
            System.err.println("ILP can not find optimal solution.");
            lp.getVariables(variables);
        }

        if (lp.getLp() != 0) {
            lp.deleteLp();
        }

        Set<Phrase> results = new HashSet<Phrase>();
        for (int i=0; i<numPhrase; i++) {
            if (variables[i] == 1.0) {
                results.add(phrases.get(i));
            }
        }
        return results;
    }


    void setZero(double [] arr) {
        for(int i = 0; i < arr.length; i++)
            arr[i] = 0;
    }


    public Set<Phrase> getPhraseSet() {
        ArrayList<Phrase> candidate = new ArrayList<Phrase>(phraseReviews.size());
        for (Phrase p : phraseReviews.keySet()) candidate.add(p);

        ArrayList<Review> reviews = new ArrayList<Review>(reviewSet.size());
        for (Review r : reviewSet) reviews.add(r);

        Set<Phrase> results = null;
        try {
            results = solve(candidate, reviews, specifyScore);
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        return results;
    }
}
