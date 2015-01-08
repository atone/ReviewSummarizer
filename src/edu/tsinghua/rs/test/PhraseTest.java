package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.Phrase;
import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.utils.FileIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
            String temp = s[0].replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]$", "");
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

    }
}
