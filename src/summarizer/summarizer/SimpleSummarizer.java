package summarizer.summarizer;

import summarizer.model.Aspect;
import summarizer.model.Phrase;
import summarizer.tool.AspectOrder;
import summarizer.tool.SimpleLPPhraseGetter;
import summarizer.utils.FileIO;
import summarizer.utils.PreHandle;

import java.util.*;

/**
 * Created by atone on 16/1/17.
 * This summarizer use TF-IDF as the score of the phrase
 * Other constraints are the same as the ReviewSummarizer
 */
public class SimpleSummarizer {
    List<Phrase> phrases;
    List<String> reviewList;
    Map<String, Double> score;

    public SimpleSummarizer(List<Phrase> phrases, List<String> reviewList, Map<String, Double> score) {
        this.phrases = phrases;
        this.reviewList = reviewList;
        this.score = score;
    }

    private List<Set<Phrase>> getAspectedPhraseSet(Set<Phrase> oriPhraseSet) {
        List<Set<Phrase>> ret = new ArrayList<Set<Phrase>>(17);
        for (int aspectID = 1; aspectID <= 17; aspectID++) {
            int polarity = 1;
            Map<String, List<String>> aspectOpinionList = new HashMap<String, List<String>>();
            for (Phrase phrase : oriPhraseSet) {
                if (phrase.aspectID == aspectID) {
                    polarity = phrase.polarity;
                    String opinion = phrase.opinion;
                    String aspect = phrase.aspect;
                    if (aspectOpinionList.containsKey(aspect)) {
                        List<String> opinionList = aspectOpinionList.get(aspect);
                        opinionList.add(opinion);
                    } else {
                        List<String> opinionList = new ArrayList<String>();
                        opinionList.add(opinion);
                        aspectOpinionList.put(aspect, opinionList);
                    }
                }
            }
            Set<Phrase> phraseSet = new HashSet<Phrase>();
            for (Map.Entry<String, List<String>> entry : aspectOpinionList.entrySet()) {
                String aspect = entry.getKey();
                List<String> opinions = entry.getValue();
                Collections.sort(opinions, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.length() - o2.length();
                    }
                });
                String opinionStr = String.join("、", opinions);
                phraseSet.add(new Phrase(aspectID, -1, polarity, aspect+opinionStr, null));
            }
            ret.add(phraseSet);
        }
        return ret;
    }

    public String getSummary() {
        SimpleLPPhraseGetter phraseGetter = new SimpleLPPhraseGetter(phrases, score);
        Set<Phrase> phraseSet = phraseGetter.getPhraseSet();
        List<Set<Phrase>> phraseSetList = getAspectedPhraseSet(phraseSet);

        AspectOrder aspectOrder = new AspectOrder(phrases, reviewList);
        List<Aspect> optimalOrder = aspectOrder.optimalOrder();

//        StringBuilder pros = new StringBuilder();
//        StringBuilder cons = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        for (Aspect a : optimalOrder) {
            Set<Phrase> set = phraseSetList.get(a.id - 1);
            for (Phrase phrase : set) {
                sb.append(phrase.content).append("，");
//                if (phrase.polarity > 0) {
//                    pros.append(phrase.content).append("，");
//                } else {
//                    cons.append(phrase.content).append("，");
//                }

            }
        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("优点：");
//        if (pros.length() > 1) {
//            pros.replace(pros.length() - 1, pros.length(), "。");
//        }
//        sb.append(pros).append("\n");
//        sb.append("缺点：");
//        if (cons.length() > 1) {
//            cons.replace(cons.length() - 1, cons.length(), "。");
//        }
//        sb.append(cons).append("\n");
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "。");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        for (int id = 2911; id <= 2956; id++) {
            String phraseFile = String.format("data/phrases_new/%d_phrases.json", id);
            String reviewFile = String.format("data/all_reviews/%d_relevance.txt", id);
            String phraseScoreFile = String.format("data/sfMap/%d_phrase_score.txt", id);
            List<String> reviews = FileIO.readLines(reviewFile);
            List<Phrase> phrases = PreHandle.getPhrases(FileIO.readJSON(phraseFile));
            Map<String, Double> phraseScore = FileIO.readPhraseScoreMap(phraseScoreFile);


            SimpleSummarizer summarizer = new SimpleSummarizer(phrases, reviews, phraseScore);
            System.err.println("summarizing " + id + "...");
            FileIO.writeFile(String.format("data/summary/%d_basicSum_summary.txt", id), summarizer.getSummary());
        }
    }

}
