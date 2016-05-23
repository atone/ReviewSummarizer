package summarizer.evaluation;

import summarizer.utils.FileIO;

import java.io.File;
import java.util.*;

/**
 * Created by atone on 16/1/22.
 * 这个类用于生成评估所用的数据.
 */
public class EvaluationDataGen {

    public static void main(String[] args) {
        int[] taskIds = new int[]{2912, 2914, 2915, 2916, 2922, 2925, 2929, 2931, 2936, 2940};
        //int[] taskIds = new int[]{2918, 2919, 2920, 2923, 2933, 2934, 2937, 2938, 2939, 2942};
        StringBuilder sb = new StringBuilder();
        sb.append("****** TASK 1 ******\n");
        for (int i : taskIds) {
            sb.append("======").append(i).append("======\n");
            List<Pair> pairs = evaluationPairGenerator(i);
            for (Pair p : pairs) {
                sb.append(String.format("%s\t%s\t%s\n", p.first, p.second, p.uid));
            }
            sb.append("\n");
        }
        sb.append("****** TASK 2 ******\n");
        for (int i : taskIds) {
            sb.append("======").append(i).append("======\n");
            Map<String, String> map = evaluationGenTask2(i);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(String.format("%s\t%s\n", entry.getKey(), entry.getValue()));
            }
        }
        FileIO.writeFile("out/evaluation/evaluation.log", sb.toString());
    }

    public static Map<String, String> evaluationGenTask2(int productID) {
        Map<String, String> map = new HashMap<String, String>();
        String lexrank = FileIO.readFile(String.format("data/summary/%d_lexrank_summary.txt", productID));
        String opinosis = FileIO.readFile(String.format("data/summary/%d_opinosis_summary.txt", productID));
        String basicSum = FileIO.readFile(String.format("data/summary/%d_basicSum_summary.txt", productID));
        String reviewSum = FileIO.readFile(String.format("data/summary/%d_reviewSum_summary.txt", productID));
        String lexrankUID = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String opinosisUID = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String basicSumUID = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String reviewSumUID = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        map.put("lexrank", lexrankUID);
        map.put("opinosis", opinosisUID);
        map.put("basicSum", basicSumUID);
        map.put("reviewSum", reviewSumUID);
        File dir = new File(String.format("out/evaluation/task2/%d", productID));
        dir.mkdirs();
        for (File file : dir.listFiles()) {
            file.delete();
        }
        FileIO.writeFile(String.format("out/evaluation/task2/%d/%s.txt", productID, lexrankUID), attachEvaluationInstruction(lexrank));
        FileIO.writeFile(String.format("out/evaluation/task2/%d/%s.txt", productID, opinosisUID), attachEvaluationInstruction(opinosis));
        FileIO.writeFile(String.format("out/evaluation/task2/%d/%s.txt", productID, basicSumUID), attachEvaluationInstruction(basicSum));
        FileIO.writeFile(String.format("out/evaluation/task2/%d/%s.txt", productID, reviewSumUID), attachEvaluationInstruction(reviewSum));
        return map;
    }

    private static String attachEvaluationInstruction(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("请首先认真阅读评测说明,然后再进行摘要评估.\n");
        sb.append("=========================================================\n");
        sb.append(content);
        sb.append("\n=========================================================\n");
        sb.append("语法性(1~10): \n");
        sb.append("非冗余性(1~10): \n");
        sb.append("一致性(1~10): \n");
        sb.append("描述性(1~10): \n");
        return sb.toString();
    }

    public static List<Pair> evaluationPairGenerator(int productID) {
        List<Pair> pairList = new ArrayList<Pair>();
        String lexrank = FileIO.readFile(String.format("data/summary/%d_lexrank_summary.txt", productID));
        String opinosis = FileIO.readFile(String.format("data/summary/%d_opinosis_summary.txt", productID));
        String basicSum = FileIO.readFile(String.format("data/summary/%d_basicSum_summary.txt", productID));
        String reviewSum = FileIO.readFile(String.format("data/summary/%d_reviewSum_summary.txt", productID));
        File dir = new File(String.format("out/evaluation/task1/%d", productID));
        dir.mkdirs();
        for (File file : dir.listFiles()) {
            file.delete();
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("lexrank", lexrank);
        map.put("opinosis", opinosis);
        map.put("basicSum", basicSum);
        map.put("reviewSum", reviewSum);
        String[] arr = new String[] {"lexrank", "opinosis", "basicSum", "reviewSum"};
        for (int i = 0; i < arr.length - 1; ++i) {
            for (int j = i + 1; j < arr.length; ++j) {
                String first, second;
                StringBuilder sb = new StringBuilder();
                if (Math.random() > 0.5) {
                    first = arr[i];
                    second = arr[j];
                } else {
                    first = arr[j];
                    second = arr[i];
                }
                sb.append("请首先认真阅读评测说明,然后再进行摘要评估.\n");
                sb.append("============================================\n");
                sb.append(map.get(first));
                sb.append("\n============================================\n");
                sb.append(map.get(second));
                sb.append("\n============================================\n");
                sb.append("如果你认为第一个比第二个更好,那么请写下1,如果你认为第二个比第一个更好,");
                sb.append("请写下2.如果你认为两个摘要很难选择哪个更好,请写下0.\n");
                sb.append("你最终的答案是: ");
                sb.append("\n");
                String uid = UUID.randomUUID().toString().substring(0,8).toUpperCase();

                FileIO.writeFile(String.format("out/evaluation/task1/%d/%s.txt", productID, uid), sb.toString());
                Pair pair = new Pair();
                pair.first = first;
                pair.second = second;
                pair.uid = uid;
                pairList.add(pair);
            }
        }
        return pairList;
    }
}

class Pair {
    public String first;
    public String second;
    public String uid;
}
