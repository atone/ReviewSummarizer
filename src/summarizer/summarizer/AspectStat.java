package summarizer.summarizer;

import summarizer.model.Review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by atone on 15/7/23.
 * This class is used for statistics of each aspect count
 */
public class AspectStat {
    int[] aspectCount = new int[18]; //共有17个aspect，下标对应的数值即为该aspect出现在review中的次数
    Set<Review> reviewSet = new HashSet<Review>();

    public AspectStat(ArrayList<String> recordStrings) {
        for (String line : recordStrings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
                continue;
            }
            Review review = new Review(s[9], s[10], s[1]);
            reviewSet.add(review);
            int aspectID = Integer.parseInt(s[2]);
            aspectCount[aspectID]++;
        }
    }

    public void printStatistics() {
        System.out.println("Reviews count: " + reviewSet.size());
        for (int i = 1; i < aspectCount.length; i++) {
            System.out.printf("%d\t%2.2f%%\n", aspectCount[i], 100 * (float) aspectCount[i] / reviewSet.size());
        }
    }

    public Set<Integer> popularAspectIDs(double threshold) {
        int totalReviewCount = reviewSet.size();
        Set<Integer> idSet = new HashSet<Integer>();
        for (int i = 1; i < aspectCount.length; i++) {
            double ratio = (double) aspectCount[i] / totalReviewCount;
            if (ratio >= threshold) {
                idSet.add(i);
            }
        }
        return idSet;
    }

}
