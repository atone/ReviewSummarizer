package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.data.ShortSentence;
import edu.tsinghua.rs.utils.FileIO;

import java.util.*;

/**
 * Created by atone on 14/12/30.
 */
public class ReviewTest {
    public static void main(String[] args) {
        HashMap<Review, HashSet<ShortSentence>> rss = new HashMap<Review, HashSet<ShortSentence>>();
        ArrayList<String> strings = FileIO.readLines("data/note3_reviews_tabbed.txt");
        System.out.println("Total lines: " + strings.size());

        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 6) {
                System.err.println("bad line: " + line);
            }
            Review r = new Review(s[1], s[4], s[5]);
            String temp = s[0].replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]$", "");
            ShortSentence ss = new ShortSentence(temp, Integer.parseInt(s[2]), Integer.parseInt(s[3]));

            if (rss.containsKey(r)) {
                rss.get(r).add(ss);
            }
            else {
                HashSet<ShortSentence> shortSentences = new HashSet<ShortSentence>();
                shortSentences.add(ss);
                rss.put(r, shortSentences);
            }
        }
        System.out.println("Review number: " + rss.size());

        ArrayList<Map.Entry<Review, HashSet<ShortSentence>>> reviews = new ArrayList<Map.Entry<Review, HashSet<ShortSentence>>>();
        for (Map.Entry<Review, HashSet<ShortSentence>> e : rss.entrySet()) {
            if (e.getValue().size() >= 2) {
                reviews.add(e);
            }
        }
        System.out.println(reviews.size());

        Comparator<Map.Entry<Review, HashSet<ShortSentence>>> comparator = new Comparator<Map.Entry<Review, HashSet<ShortSentence>>>() {
            @Override
            public int compare(Map.Entry<Review, HashSet<ShortSentence>> t0, Map.Entry<Review, HashSet<ShortSentence>> t1) {
                return t1.getValue().size() - t0.getValue().size();
            }
        };
        Collections.sort(reviews, comparator);

        for (Map.Entry<Review, HashSet<ShortSentence>> e : reviews) {
            System.out.println(e.getKey().content.substring(0,20) + "||"+ e.getValue().size());
        }

    }
}
