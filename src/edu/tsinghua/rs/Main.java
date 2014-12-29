package edu.tsinghua.rs;

import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.data.ShortSentence;
import edu.tsinghua.rs.utils.FileIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        HashMap<ShortSentence, HashSet<Review>> ssr = new HashMap<ShortSentence, HashSet<Review>>();
        ArrayList<String> strings = FileIO.readLines("data/note3_reviews_sampled.txt");
        System.out.println(strings.size());
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
        System.out.println("hashmap key/value pairs: " + ssr.size());
        for (ShortSentence s : ssr.keySet()) {
            if (ssr.get(s).size() >= 2) {
                System.out.print(s.content + " " + ssr.get(s).size());
//                for (Review r : ssr.get(s)) {
//                    System.out.print(" " + r.content);
//                }
                System.out.println();
            }
        }
    }
}
