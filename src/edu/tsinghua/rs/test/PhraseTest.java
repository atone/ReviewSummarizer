package edu.tsinghua.rs.test;

import edu.tsinghua.rs.data.PRCollection;
import edu.tsinghua.rs.data.Phrase;
import edu.tsinghua.rs.data.Review;
import edu.tsinghua.rs.summarizer.GreedySummarizer;
import edu.tsinghua.rs.summarizer.LPSummarizer;
import edu.tsinghua.rs.utils.FileIO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by atone on 15-1-6.
 */
public class PhraseTest {
    public static void clusterOutput(String product) {
        String productFile = String.format("data/%s_relevance.txt", product);
        ArrayList<String> strings = FileIO.readLines(productFile);
        System.out.println("============ " + product + " ============");
        System.out.println("总共记录条数：" + strings.size());

        HashMap<Phrase, PRCollection> phraseReview = new HashMap<Phrase, PRCollection>();
        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
            }

            Review review = new Review(s[9], s[10], s[1]);
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], s[5], s[6], Integer.parseInt(s[8]));
            if (phraseReview.containsKey(phrase)) {
                PRCollection collection = phraseReview.get(phrase);
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
            }
            else {
                PRCollection collection = new PRCollection();
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
                phraseReview.put(phrase, collection);
            }
        }
        System.out.println("聚类后的短语数目：" + phraseReview.size());

        ArrayList<Map.Entry<Phrase,PRCollection>> entryArrayList = new ArrayList<Map.Entry<Phrase, PRCollection>>(phraseReview.size());
        for (Map.Entry<Phrase,PRCollection> entry : phraseReview.entrySet()) {
            entryArrayList.add(entry);
        }
        Comparator<Map.Entry<Phrase,PRCollection>> comparator = new Comparator<Map.Entry<Phrase, PRCollection>>() {
            @Override
            public int compare(Map.Entry<Phrase, PRCollection> t0, Map.Entry<Phrase, PRCollection> t1) {
                return t1.getValue().phrases.size() - t0.getValue().phrases.size();
            }
        };

        Collections.sort(entryArrayList, comparator);

        JSONArray outputArray = new JSONArray();
        for (Map.Entry<Phrase,PRCollection> entry : entryArrayList) {
            JSONObject j = new JSONObject();
            j.put("phrase", entry.getKey().content);
            JSONArray phraseArray = new JSONArray(entry.getValue().phrases.toArray());
            j.put("phraseSet", phraseArray);
//            JSONArray reviewArray = new JSONArray();
//            for (Review r : entry.getValue().reviews) {
//                reviewArray.put(r.content);
//            }
//            j.put("reviewSet", reviewArray);

            outputArray.put(j);
        }
        FileIO.writeFile(String.format("out/result/%s_cluster.json", product), outputArray.toString());


    }
    public static void reverseStatics(String product) {
        String productFile = String.format("data/%s_relevance.txt", product);
        ArrayList<String> strings = FileIO.readLines(productFile);
        System.out.println("============ " + product + " ============");
        System.out.println("总共记录条数：" + strings.size());

        HashMap<Phrase, PRCollection> phraseReview = new HashMap<Phrase, PRCollection>();
        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 11) {
                System.err.println("bad line: " + line);
            }

            Review review = new Review(s[9], s[10], s[1]);
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], s[5], s[6], Integer.parseInt(s[8]));
            if (phraseReview.containsKey(phrase)) {
                PRCollection collection = phraseReview.get(phrase);
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
            }
            else {
                PRCollection collection = new PRCollection();
                collection.reviews.add(review);
                collection.phrases.add(phrase.detailedContent);
                phraseReview.put(phrase, collection);
            }
        }
        System.out.println("聚类后的短语数目：" + phraseReview.size());

        int totalPhraseNum = 0;
        int maxPhraseNum = 0;
        int minPhraseNum = Integer.MAX_VALUE;
        int maxReviewNum = 0;
        int minReviewNum = Integer.MAX_VALUE;
        int phraseWithReviewGT2 = 0;
        for (Map.Entry<Phrase, PRCollection> entry : phraseReview.entrySet()) {
            int phraseNum = entry.getValue().phrases.size();
            totalPhraseNum += phraseNum;
            if (maxPhraseNum < phraseNum) {
                maxPhraseNum = phraseNum;
            }
            if (minPhraseNum > phraseNum) {
                minPhraseNum = phraseNum;
            }

            int reviewNum = entry.getValue().reviews.size();
            if (maxReviewNum < reviewNum) {
                maxReviewNum = reviewNum;
            }
            if (minReviewNum > reviewNum) {
                minReviewNum = reviewNum;
            }

            if (reviewNum >= 2) {
                phraseWithReviewGT2++;
            }
        }
        System.out.println("未聚类的短语数目：" + totalPhraseNum);
        System.out.printf("每个聚类中最多有 %d 条短语，最少有 %d 条短语\n", maxPhraseNum, minPhraseNum);
        System.out.printf("每一个短语聚类对应最多 %d 条review，最少 %d 条review\n", maxReviewNum, minReviewNum);
        System.out.printf("包含有两条review以上的短语聚类数目有 %d 个，占%5.2f%%\n", phraseWithReviewGT2, (float)100*phraseWithReviewGT2/phraseReview.size());

    }

    public static void statistics(String product) {
        String productFile = String.format("data/old/%s_review_phrase.txt", product);

        HashMap<Review, HashSet<Phrase>> rss = new HashMap<Review, HashSet<Phrase>>();
        ArrayList<String> strings = FileIO.readLines(productFile);
        System.out.println("============ " + product + " ============");
        System.out.println("总共记录条数：" + strings.size());

        for (String line : strings) {
            String[] s = line.split("\t");
            if (s.length != 9) {
                System.err.println("bad line: " + line);
            }
            Review r = new Review(s[7], s[8], s[1]);
            //String temp = s[0].replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]$", "");
            Phrase phrase = new Phrase(Integer.parseInt(s[2]), s[3], s[4], "", "", 1);

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


        System.out.println("每条Review中最多包含" + maxPhraseNumInReview + "条短语");

        System.out.println("每条Review中包含最少" + minPhraseNumInReview + "条短语");

        int[] reviewDistribution = new int[maxPhraseNumInReview + 1];
        for (Map.Entry<Review, HashSet<Phrase>> entry : rss.entrySet()) {
            reviewDistribution[entry.getValue().size()]++;
        }

        System.out.println("根据Review中包含的短语数目的分布：\n短语数\tReview数目");
        for (int i = minPhraseNumInReview; i <= maxPhraseNumInReview; i++) {
            System.out.println(i + "\t" + reviewDistribution[i]);
        }
    }

    public static void generatePhrases(String product) {
        String productFile = String.format("data/%s_relevance.txt", product);
        System.err.printf("Generate result for %s...\n", product);
        ArrayList<String> strings = FileIO.readLines(productFile);
        HashMap<Phrase, PRCollection> phraseReviews = PreHandle.getPhraseReviewCollection(strings);



        LPSummarizer lpSummarizer = new LPSummarizer(phraseReviews);
        lpSummarizer.K = 30;
        Set<Phrase> results = lpSummarizer.summarize();

        StringBuffer sb = new StringBuffer();
        sb.append("*********** Results by ILP Summarizer **********\n");
        for (int i=1; i<=17; i++) {
            sb.append(String.format("Aspect %d: ", i));

            for (Phrase p : results) {
                if (p.aspectID == i) {
                    sb.append(p + " ");
                }
            }
            sb.append("\n");
        }
        sb.append("*********** Results by Greedy Summarizer **********\n");
        GreedySummarizer greedySummarizer = new GreedySummarizer(phraseReviews);
        greedySummarizer.K = 30;
        results = greedySummarizer.summarize();
        for (int i=1; i<=17; i++) {
            sb.append(String.format("Aspect %d: ", i));

            for (Phrase p : results) {
                if (p.aspectID == i) {
                    sb.append(p + " ");
                }
            }
            sb.append("\n");
        }
        FileIO.writeFile(String.format("out/phrase_result/%s.txt", product), sb.toString());
    }
    public static void main(String[] args) {
//        generatePhrases("3x");
//        generatePhrases("galaxys4");
//        generatePhrases("iphone5s");
//        generatePhrases("mx3");
//        generatePhrases("note3");
        clusterOutput("3x");
    }
}
