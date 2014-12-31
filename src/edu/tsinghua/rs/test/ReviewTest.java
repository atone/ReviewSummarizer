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
        System.out.println("总共记录条数：" + strings.size());

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
        System.out.println("Review条数：" + rss.size());

        double review_avg_length = 0;
        int review_max_length = 0;
        int review_min_length = Integer.MAX_VALUE;
        double ss_avg_num = 0;
        int ss_max_num = 0;
        int ss_min_num = Integer.MAX_VALUE;

        ArrayList<Map.Entry<Review, HashSet<ShortSentence>>> reviews = new ArrayList<Map.Entry<Review, HashSet<ShortSentence>>>();
        for (Map.Entry<Review, HashSet<ShortSentence>> e : rss.entrySet()) {
            int current_review_length = e.getKey().content.length();
            review_avg_length += current_review_length;
            if (review_max_length < current_review_length) {
                review_max_length = current_review_length;
            }
            if (review_min_length > current_review_length) {
                review_min_length = current_review_length;
            }

            ss_avg_num += e.getValue().size();
            if (ss_max_num < e.getValue().size()) {
                ss_max_num = e.getValue().size();
            }
            if (ss_min_num > e.getValue().size()) {
                ss_min_num = e.getValue().size();
            }

            if (e.getValue().size() >= 2) {
                reviews.add(e);
            }
        }
        review_avg_length /= rss.size();
        ss_avg_num /= rss.size();
        System.out.println("平均Review的长度为" + review_avg_length + "字符");
        System.out.println("最长Review为" + review_max_length + "字符，最短为" + review_min_length + "字符。");
        System.out.println("平均每个Review包含短句数目为" + ss_avg_num + "个，每个Review最多包含短语数目" + ss_max_num + "个，最少短语数目" + ss_min_num + "个。");
        System.out.println("含有2条短句以上的Review条数：" + reviews.size());



//        Comparator<Map.Entry<Review, HashSet<ShortSentence>>> comparator = new Comparator<Map.Entry<Review, HashSet<ShortSentence>>>() {
//            @Override
//            public int compare(Map.Entry<Review, HashSet<ShortSentence>> t0, Map.Entry<Review, HashSet<ShortSentence>> t1) {
//                return t1.getValue().size() - t0.getValue().size();
//            }
//        };
//        Collections.sort(reviews, comparator);
//
//        for (Map.Entry<Review, HashSet<ShortSentence>> e : reviews) {
//            System.out.print(e.getKey().content);
//            for (ShortSentence s : e.getValue()) {
//                System.out.print("|" + s.content);
//            }
//            System.out.println();
//        }

    }
}
