package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.data.ShortSentence;
import edu.tsinghua.rs.utils.FileIO;

import java.util.*;

/**
 * Created by atone on 14/12/30.
 */
public class ShortSentenceTest {
    public static void main(String[] args) {
        HashMap<ShortSentence, HashSet<Review>> ssr = new HashMap<ShortSentence, HashSet<Review>>();
        ArrayList<String> strings = FileIO.readLines("data/note3_reviews_sampled.txt");
        System.out.println("total lines: " + strings.size());
        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 6) {
                System.err.println("bad line: " + line);
            }
            Review r = new Review(s[1], s[4], s[5]);
            String temp = s[0].replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]$", "");
            ShortSentence ss = new ShortSentence(temp, Integer.parseInt(s[2]), Integer.parseInt(s[3]));
            if (ssr.containsKey(ss)) {
                ssr.get(ss).add(r);
            }
            else {
                HashSet<Review> reviews = new HashSet<Review>();
                reviews.add(r);
                ssr.put(ss, reviews);
            }
        }

        ArrayList<Map.Entry<ShortSentence, HashSet<Review>>> entries = new ArrayList<Map.Entry<ShortSentence, HashSet<Review>>>();

        for (Map.Entry<ShortSentence, HashSet<Review>> e : ssr.entrySet()) {
            if (e.getValue().size() >= 2) {
                entries.add(e);
            }
        }
        System.out.println(entries.size());

        Comparator<Map.Entry<ShortSentence, HashSet<Review>>> comparator = new Comparator<Map.Entry<ShortSentence, HashSet<Review>>>() {
            @Override
            public int compare(Map.Entry<ShortSentence, HashSet<Review>> t0, Map.Entry<ShortSentence, HashSet<Review>> t1) {
                return t1.getValue().size() - t0.getValue().size();
            }
        };
        Collections.sort(entries, comparator);
        for(Map.Entry<ShortSentence, HashSet<Review>> e : entries) {
            System.out.println(e.getKey().content + " " + e.getValue().size());
        }
    }
}
