package summarizer.tool;

import summarizer.model.Aspect;
import summarizer.model.Phrase;
import summarizer.utils.FileIO;
import summarizer.utils.PreHandle;
import summarizer.utils.TSPFarthestNeighbor;

import java.util.*;

/**
 * Created by atone on 15/6/18.
 * This class determines the aspect order.
 */
public class AspectOrder {
    private List<List<Aspect>> aspectOrderList;
    public int[][] countTable;

    public AspectOrder(List<Phrase> phrases, List<String> reviews) {
        aspectOrderList = getAspectOrderList(phrases, reviews);
        countTable = new int[18][18];
    }

    // 返回最终计算出的最优aspect顺序
    public List<Aspect> optimalOrder() {
        List<Aspect> aspects = new ArrayList<Aspect>(17); //一共有17种aspects
        // 计算每一种aspect路径的数量
        for (List<Aspect> aspectOrder : aspectOrderList) {
            if (aspectOrder.size() <= 1) continue;
            for (int i = 0; i < aspectOrder.size() - 1; ++i) {
                if (aspectOrder.get(i).id == aspectOrder.get(i + 1).id) continue;
                countTable[aspectOrder.get(i).id][aspectOrder.get(i + 1).id]++;
            }
        }
        TSPFarthestNeighbor tsp = new TSPFarthestNeighbor();
        int[] optimalPath = tsp.tsp(countTable);

        int index = 0;
        while (index < optimalPath.length && optimalPath[index] != 0) {
            Aspect aspect = new Aspect(optimalPath[index]);
            aspects.add(aspect);
            index++;
        }

        return aspects;
    }


    private List<List<Aspect>> getAspectOrderList(List<Phrase> phrases, List<String> reviewContentList) {
        Map<String, Set<Phrase>> reviewMap = PreHandle.getReviewMap(phrases, reviewContentList);
        List<List<Aspect>> aspectOrderList = new ArrayList<List<Aspect>>(reviewMap.size());

        for (Map.Entry<String, Set<Phrase>> entry : reviewMap.entrySet()) {
            List<Aspect> aspectOrder = new ArrayList<Aspect>();
            String review = entry.getKey();
            Set<Phrase> phraseSet = entry.getValue();
            for (Phrase p : phraseSet) {
                String phrase = p.content;
                int position = review.indexOf(phrase);
                if (position == -1) {
                    System.err.println(String.format("Error: [%s] not in [%s]", phrase, review));
                    continue;
                }
                Aspect aspect = new Aspect(p.aspectID);
                aspect.position = position;
                aspectOrder.add(aspect);
            }
            Collections.sort(aspectOrder);
            aspectOrderList.add(aspectOrder);
        }
        return aspectOrderList;
    }

    public static void main(String[] args) {
        for (int id = 2911; id <= 2956; id++) {
            String phraseFile = String.format("data/phrases_new/%d_phrases.json", id);
            String reviewFile = String.format("data/all_reviews/%d_relevance.txt", id);
            List<String> reviews = FileIO.readLines(reviewFile);
            List<Phrase> phrases = PreHandle.getPhrases(FileIO.readJSON(phraseFile));
            AspectOrder order = new AspectOrder(phrases, reviews);
            System.out.println(order.optimalOrder());
        }
    }
}
