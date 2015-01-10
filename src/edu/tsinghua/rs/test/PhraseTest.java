package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.Phrase;
import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.utils.FileIO;

import java.util.*;

/**
 * Created by atone on 15-1-6.
 */
public class PhraseTest {
    public static void main(String[] args) {
        HashMap<Review, HashSet<Phrase>> rss = new HashMap<Review, HashSet<Phrase>>();
        ArrayList<String> strings = FileIO.readLines("data/note3_review_phrase.txt");
        System.out.println("总共记录条数：" + strings.size());

        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 9) {
                System.err.println("bad line: " + line);
            }
            Review r = new Review(s[7], s[8], s[1]);
            //String temp = s[0].replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]$", "");
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4]);

            if (rss.containsKey(r)) {
                rss.get(r).add(phrase);
            }
            else {
                HashSet<Phrase> phrases = new HashSet<Phrase>();
                phrases.add(phrase);
                rss.put(r, phrases);
            }
        }
        System.out.println("Review条数：" + rss.size());

        ArrayList<Map.Entry<Review, HashSet<Phrase>>> reviewEntries = new ArrayList<Map.Entry<Review, HashSet<Phrase>>>(rss.size());


        int maxPhraseNumInReview = 0;
        int minPhraseNumInReview = Integer.MAX_VALUE;
        for (Map.Entry<Review, HashSet<Phrase>> entry : rss.entrySet()) {
            int setSize = entry.getValue().size();
            if (setSize > maxPhraseNumInReview) {
                maxPhraseNumInReview = setSize;
            }
            if (setSize < minPhraseNumInReview) {
                minPhraseNumInReview = setSize;
            }
            if (setSize >= 2) {
                reviewEntries.add(entry);
            }
        }
        System.out.println("包含多于2条短语的Review数目为：" + reviewEntries.size());

        Comparator<Map.Entry<Review, HashSet<Phrase>>> comparator = new Comparator<Map.Entry<Review, HashSet<Phrase>>>() {
            @Override
            public int compare(Map.Entry<Review, HashSet<Phrase>> o1, Map.Entry<Review, HashSet<Phrase>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        };

        Collections.sort(reviewEntries, comparator);
        Map.Entry<Review, HashSet<Phrase>> max = reviewEntries.get(0);

        System.out.println("每条Review中最多包含" + maxPhraseNumInReview + "条短语");

        System.out.println("每条Review中包含最少" + minPhraseNumInReview + "条短语");

        System.out.println(max.getKey().content);
        System.out.print(max.getValue().size() + " ");
        for (Phrase p : max.getValue()) {
            System.out.print(p.content+ p.aspectID + " ");
        }
        System.out.println();



    }
}
