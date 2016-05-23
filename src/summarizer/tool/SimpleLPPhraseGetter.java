package summarizer.tool;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import summarizer.model.Phrase;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by atone on 16/1/17.
 * This summarizer uses ILP as global optimization methods.
 * But it is simple because only TF-IDF score is used for scoring phrases
 */
public class SimpleLPPhraseGetter {
    private List<Phrase> phrases;
    private Map<String, Double> score;

    public SimpleLPPhraseGetter(List<Phrase> phrases, Map<String, Double> score) {
        this.phrases = phrases;
        this.score = score;
    }

    public Set<Phrase> solve(List<Phrase> phrases, Map<String, Double> score) throws LpSolveException {
        LpSolve lp;
        int nCol = phrases.size();
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

        // add polarity constraints
        int[] positivePhraseNum = new int[18];
        int[] negativePhraseNum = new int[18];
        for (int i=0; i<nCol; i++) {
            Phrase p = phrases.get(i);
            if (p.polarity > 0) {
                positivePhraseNum[p.aspectID]++;
            } else {
                negativePhraseNum[p.aspectID]++;
            }
        }
        int[] colNo = new int[1];
        double[] row_temp = new double[1];
        for (int i = 0; i< nCol; i++) {
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

//        // add length constraint
//        setZero(row);
//        for (int i = 0; i < nCol; i++) {
//            row[i + 1] = phrases.get(i).content.length();
//        }
//        lp.addConstraint(row, LpSolve.LE, 100); //摘要长度不多于100字

        // add count constraint
        for (int aspectID = 1; aspectID <= 17; aspectID++) {
            setZero(row);
            for (int i = 0; i < nCol; i++) {
                if (phrases.get(i).aspectID == aspectID) {
                    row[i + 1] = 1;
                }
            }
            lp.addConstraint(row, LpSolve.LE, 2); //同一aspect下的短语出现次数不多于2
        }

        // add cluster constraint
        for (int aspectID = 1; aspectID <= 17; aspectID++) {
            int clusterID = 0;
            boolean clusterHasElements = true;
            while (clusterHasElements) {
                setZero(row);
                clusterHasElements = false;
                for (int i = 0; i < nCol; i++) {
                    Phrase p = phrases.get(i);
                    if (p.aspectID == aspectID && p.clusterID == clusterID) {
                        clusterHasElements = true;
                        row[i + 1] = 1;
                    }
                }
                lp.addConstraint(row, LpSolve.LE, 1); //同一aspect下同一聚类下的所有短语最多只有1个被选择
                clusterID++;
            }
        }

        lp.setAddRowmode(false);

        // set objective function
        setZero(row);
        for (int i=0; i<nCol; i++) {
            Double s = score.get(phrases.get(i).content);
            if (s != null) {
                row[i+1] = s;
            }
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
        for (int i=0; i<nCol; i++) {
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
        Set<Phrase> results = null;
        try {
            results = solve(phrases, score);
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        return results;
    }
}
