package summarizer.summarizer;

import summarizer.model.Aspect;
import summarizer.model.PRCollection;
import summarizer.model.Phrase;
import summarizer.test.PreHandle;
import summarizer.utils.FileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by atone on 15/6/23.
 * This is the main class of this project
 * This class will calculate the summary of the product
 */
public class ReviewSummarizer {
    ArrayList<String> recordStrings;

    public ReviewSummarizer(ArrayList<String> recordStrings) {
        this.recordStrings = recordStrings;
    }

    public String getSummary() {
        HashMap<Phrase, PRCollection> phraseReviews = PreHandle.getPhrase_PRCollection(recordStrings);

        AspectStat as = new AspectStat(recordStrings);

        Set<Integer> popularIDSet = as.popularAspectIDs(0.05);

        // remove phrases whose aspectID is not popular
        Iterator<Phrase> it = phraseReviews.keySet().iterator();
        while (it.hasNext()) {
            int aspectID = it.next().aspectID;
            if (!popularIDSet.contains(aspectID)) {
                it.remove();
            }
        }

        LPPhraseGetter phraseGetter = new LPPhraseGetter(phraseReviews);
        Set<Phrase> phraseSet = phraseGetter.getPhraseSet();

        AspectOrder aspectOrder = new AspectOrder(recordStrings);
        ArrayList<Aspect> optimalOrder = aspectOrder.optimalOrder();

        StringBuilder pros = new StringBuilder();
        StringBuilder cons = new StringBuilder();

        for (Aspect a : optimalOrder) {
            if (!popularIDSet.contains(a.id)) {
                continue;
            }
            for (Phrase phrase : phraseSet) {
                if (phrase.aspectID == a.id) {
                    if (phrase.polarity > 0) {
                        pros.append(phrase.content).append("，");
                    } else {
                        cons.append(phrase.content).append("，");
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("优点：");
        if (pros.length() > 1) {
            pros.replace(pros.length() - 1, pros.length(), "。");
        }
        sb.append(pros).append("\n");
        sb.append("缺点：");
        if (cons.length() > 1) {
            cons.replace(cons.length() - 1, cons.length(), "。");
        }
        sb.append(cons).append("\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        File dataDir = new File("data");
        for (String s : dataDir.list()) {
            if (s.endsWith("_relevance.txt")) {
                String productFile = String.format("data/%s", s);
                ArrayList<String> strings = FileIO.readLines(productFile);
                strings.remove(0); //去掉第一行不相关的内容
                ReviewSummarizer summarizer = new ReviewSummarizer(strings);
                System.out.println("summarizing " + s + " ...");
                FileIO.writeFile(String.format("out/summary/%s", s), summarizer.getSummary());
            }
        }
    }
}
