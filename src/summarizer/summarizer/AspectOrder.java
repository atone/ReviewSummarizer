package summarizer.summarizer;

import summarizer.model.Aspect;
import summarizer.model.Phrase;
import summarizer.model.Review;
import summarizer.test.PreHandle;
import summarizer.utils.FileIO;
import summarizer.utils.TSPFarthestNeighbor;

import java.util.*;

/**
 * Created by atone on 15/6/18.
 * This class determines the aspect order.
 */
public class AspectOrder {
    private ArrayList<ArrayList<Aspect>> aspectOrderList;
    public int[][] countTable;

    public AspectOrder(ArrayList<String> recordStrings) {
        aspectOrderList = getAspectOrderList(recordStrings);
        countTable = new int[18][18];
    }

    // 返回最终计算出的最优aspect顺序
    public ArrayList<Aspect> optimalOrder() {
        ArrayList<Aspect> aspects = new ArrayList<Aspect>(17); //一共有17种aspects
        // 计算每一种aspect路径的数量
        for (ArrayList<Aspect> aspectOrder : aspectOrderList) {
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


    // 每一个record是一个review，将每一个review中的aspect顺序提取出来
    private ArrayList<ArrayList<Aspect>> getAspectOrderList(ArrayList<String> recordStrings) {
        HashMap<Review, HashSet<Phrase>> reviewPhraseSet = PreHandle.getReview_PhraseCollection(recordStrings);
        ArrayList<ArrayList<Aspect>> aspectOrderList = new ArrayList<ArrayList<Aspect>>(reviewPhraseSet.size());

        Set<Map.Entry<Review, HashSet<Phrase>>> entries = reviewPhraseSet.entrySet();
        for (Map.Entry<Review, HashSet<Phrase>> entry : entries) {
            ArrayList<Aspect> aspectOrder = new ArrayList<Aspect>();
            String reviewStr = entry.getKey().content;
            HashSet<Phrase> phrases = entry.getValue();

            for (Phrase p : phrases) {
                String phrase = p.detailedContent;
                int position = reviewStr.indexOf(phrase);
                if (position == -1) {
                    //System.err.println("Error: Can't find \"" + phrase + "\" in \"" + reviewStr + "\"");
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
        for (String product : new String[]{"3x", "galaxys4", "iphone5s", "mx3", "note3"}) {
            String productFile = String.format("data/%s_relevance.txt", product);
            ArrayList<String> strings = FileIO.readLines(productFile);
            AspectOrder aspectOrder = new AspectOrder(strings);
            ArrayList<Aspect> optimalOrder = aspectOrder.optimalOrder();

            System.out.print(product + ": ");
            for (Aspect aspect : optimalOrder) {
                System.out.print(aspect.name + " ");
            }
            System.out.println();
        }

    }
}
