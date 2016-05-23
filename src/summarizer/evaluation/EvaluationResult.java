package summarizer.evaluation;

import summarizer.utils.FileIO;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by atone on 16/1/26.
 * 这个类用于统计评估的实验结果
 */
public class EvaluationResult {

    private Map<String, Pair> task1UidMap;
    private Map<String, String> task2UidMap;

    public void parseUidMapTask1(String logFilePath) {
        task1UidMap = new HashMap<String, Pair>();
        List<String> logs = FileIO.readLines(logFilePath);
        for (String log : logs) {
            String[] pieces = log.split("\t");
            if (pieces.length != 3) {
                continue;
            }
            String key = pieces[2];
            Pair value = new Pair();
            value.first = pieces[0];
            value.second = pieces[1];
            value.uid = pieces[2];
            task1UidMap.put(key, value);
        }
        System.err.println("task1 uid map parsed, size: " + task1UidMap.size());
    }

    public void parseUidMapTask2(String logFilePath) {
        task2UidMap = new HashMap<String, String>();
        List<String> logs = FileIO.readLines(logFilePath);
        for (String log : logs) {
            String[] pieces = log.split("\t");
            if (pieces.length != 2) {
                continue;
            }
            task2UidMap.put(pieces[1], pieces[0]);
        }
        System.err.println("task2 uid map parsed, size: " + task2UidMap.size());
    }

    public void printTask1Statics(String task1ResultDir) {
        List[][] data = new List[5][];

        for (int i = 0; i < 5; i++) {
            data[i] = new List[10];
            Path firstHalfPath = Paths.get(task1ResultDir, String.format("task1_%d-1.txt", i + 1));
            Path secondHalfPath = Paths.get(task1ResultDir, String.format("task1_%d-2.txt", i + 1));

            List<String> stringList = FileIO.readLines(firstHalfPath.toString());
            stringList.addAll(FileIO.readLines(secondHalfPath.toString()));

            int counter = 0;
            for (String line : stringList) {
                String[] pieces = line.split("\t");

                if (pieces.length == 2 && pieces[0].length() == 8 && pieces[1].length() == 1) {
                    if (counter % 6 == 0) {
                        data[i][counter / 6] = new ArrayList();
                    }
                    Pair p = task1UidMap.get(pieces[0]);
                    int result = Integer.parseInt(pieces[1]);
                    Task1Result task1Result = new Task1Result(p.first, p.second, result);
                    data[i][counter / 6].add(task1Result);
                    ++counter;
                }
            }
        }

        Task1Result[] sumResults = new Task1Result[] { new Task1Result("basicSum", "lexrank"), new Task1Result("basicSum", "opinosis"),
                new Task1Result("basicSum", "reviewSum"), new Task1Result("lexrank", "opinosis"), new Task1Result("lexrank", "reviewSum"),
                new Task1Result("opinosis", "reviewSum")};
        for (int j = 0; j < 10; j++) {
            Task1Result[] results = new Task1Result[] { new Task1Result("basicSum", "lexrank"), new Task1Result("basicSum", "opinosis"),
            new Task1Result("basicSum", "reviewSum"), new Task1Result("lexrank", "opinosis"), new Task1Result("lexrank", "reviewSum"),
            new Task1Result("opinosis", "reviewSum")};
            for (int i = 0; i < 5; i++) {
                for (Object o : data[i][j]) {
                    Task1Result r = (Task1Result) o;
                    for (Task1Result result : results) {
                        if (result.isSameKind(r)) {
                            result.addResult(r);
                            break;
                        }
                    }
                }
            }
            System.out.println("======== product id : " + j + " ========");
            for (int i = 0; i < results.length; i++) {
                System.out.println(results[i].toString());
                sumResults[i].addResult(results[i]);
            }

            String[] algos = new String[] {"basicSum", "lexrank", "opinosis", "reviewSum"};
            for (String algo : algos) {
                int sum = 0;
                for (Task1Result result : results) {
                    sum += result.getCount(algo);
                }
                System.out.printf("%s: (%d)%.2f%%\n", algo, sum, (double)sum / 15 * 100);
            }
        }
        System.out.println("####### total #######");
        for (Task1Result result : sumResults) {
            System.out.println(result.toString());
        }
        String[] algos = new String[] {"basicSum", "lexrank", "opinosis", "reviewSum"};
        for (String algo : algos) {
            int sum = 0;
            for (Task1Result result : sumResults) {
                sum += result.getCount(algo);
            }
            System.out.printf("%s: (%d)%.2f%%\n", algo, sum, (double)sum / 150 * 100);
        }

    }

    public void printTask2Statics(String task2ResultDir) {

        double[] basicSum = new double[4];
        double[] lexrank = new double[4];
        double[] opinosis = new double[4];
        double[] reviewSum = new double[4];
        int cnt = 0;
        for (int i = 0; i < 5; ++i) {
            Path firstHalfPath = Paths.get(task2ResultDir, String.format("task2_%d-1.txt", i + 1));
            Path secondHalfPath = Paths.get(task2ResultDir, String.format("task2_%d-2.txt", i + 1));

            List<String> stringList = FileIO.readLines(firstHalfPath.toString());
            stringList.addAll(FileIO.readLines(secondHalfPath.toString()));

            List<Task2Item> task2Items = new ArrayList<Task2Item>();
            for (String line : stringList) {
                String[] pieces = line.split("\t");
                if (pieces.length == 5 && pieces[0].length() == 8) {
                    String algoName = task2UidMap.get(pieces[0]);
                    double d1 = Double.parseDouble(pieces[1]);
                    double d2 = Double.parseDouble(pieces[2]);
                    double d3 = Double.parseDouble(pieces[3]);
                    double d4 = Double.parseDouble(pieces[4]);
                    if (algoName == null) {
                        System.err.println("error occurred");
                        continue;
                    }
                    Task2Item item = new Task2Item();
                    item.UUID = pieces[0];
                    item.algoName = algoName;
                    item.scores = new double[] {d1, d2, d3, d4};
                    task2Items.add(item);
                    cnt++;
                }
            }
            // 将每个人的得分正则化到0~10之间
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (Task2Item item : task2Items) {
                if (min > item.minScore()) {
                    min = item.minScore();
                }
                if (max < item.maxScore()) {
                    max = item.maxScore();
                }
            }
            for (Task2Item item : task2Items) {
                for (int j = 0; j < item.scores.length; j++) {
                    item.scores[j] = (item.scores[j] - min) / (max - min) * 10.0;
                }
            }

            for (Task2Item item : task2Items) {
                if (item.algoName.equals("basicSum")) {
                    basicSum[0] += item.scores[0];
                    basicSum[1] += item.scores[1];
                    basicSum[2] += item.scores[2];
                    basicSum[3] += item.scores[3];
                } else if (item.algoName.equals("lexrank")) {
                    lexrank[0] += item.scores[0];
                    lexrank[1] += item.scores[1];
                    lexrank[2] += item.scores[2];
                    lexrank[3] += item.scores[3];
                } else if (item.algoName.equals("opinosis")) {
                    opinosis[0] += item.scores[0];
                    opinosis[1] += item.scores[1];
                    opinosis[2] += item.scores[2];
                    opinosis[3] += item.scores[3];
                } else if (item.algoName.equals("reviewSum")) {
                    reviewSum[0] += item.scores[0];
                    reviewSum[1] += item.scores[1];
                    reviewSum[2] += item.scores[2];
                    reviewSum[3] += item.scores[3];
                } else {
                    System.err.println("No matching algorithm name");
                }
            }

        }
        cnt /= 4;
        System.out.println("============== task 2 =================");
        System.out.printf("basicSum:\t%f\t%f\t%f\t%f\n", basicSum[0]/cnt, basicSum[1]/cnt, basicSum[2]/cnt, basicSum[3]/cnt);
        System.out.printf("lexrank:\t%f\t%f\t%f\t%f\n", lexrank[0]/cnt, lexrank[1]/cnt, lexrank[2]/cnt, lexrank[3]/cnt);
        System.out.printf("opinosis:\t%f\t%f\t%f\t%f\n", opinosis[0]/cnt, opinosis[1]/cnt, opinosis[2]/cnt, opinosis[3]/cnt);
        System.out.printf("reviewSum:\t%f\t%f\t%f\t%f\n", reviewSum[0]/cnt, reviewSum[1]/cnt, reviewSum[2]/cnt, reviewSum[3]/cnt);
    }


    public static void main(String[] args) {
        String logFilePath = "data/evaluation_data/evaluation.log";
        EvaluationResult result = new EvaluationResult();
        result.parseUidMapTask1(logFilePath);
        result.parseUidMapTask2(logFilePath);
        result.printTask1Statics("data/evaluation_data/task1");
        result.printTask2Statics("data/evaluation_data/task2");
    }
}

class Task1Result {
    public String first;
    public String second;
    public int firstCnt = 0;
    public int secondCnt = 0;
    public int noPrefCnt = 0;
    public Task1Result(String first, String second) {
        this.first = first;
        this.second = second;
    }
    public Task1Result(String first, String second, int result) {
        if (first.compareTo(second) < 0) {
            this.first = first;
            this.second = second;
            if (result == 0) noPrefCnt++;
            if (result == 1) firstCnt++;
            if (result == 2) secondCnt++;
        } else {
            this.first = second;
            this.second = first;
            if (result == 0) noPrefCnt++;
            if (result == 1) secondCnt++;
            if (result == 2) firstCnt++;
        }
    }

    public int getCount(String string) {
        if (string.equals(first)) {
            return firstCnt;
        } else if (string.equals(second)) {
            return secondCnt;
        } else {
            return 0;
        }
    }

    public void addResult(Task1Result another) {
        if (isSameKind(another)) {
            firstCnt += another.firstCnt;
            secondCnt += another.secondCnt;
            noPrefCnt += another.noPrefCnt;
        }
    }

    public boolean isSameKind(Task1Result another) {
        return first.equals(another.first) && second.equals(another.second);
    }

    @Override
    public String toString() {
        return String.format("%s vs %s\t%s:%d\t%s:%d\t%s:%d", first, second, "noPref", noPrefCnt, first, firstCnt, second, secondCnt);
    }
}

class Task2Item {
    public String UUID;
    public String algoName;
    public double[] scores;
    public double minScore() {
        double min = Double.MAX_VALUE;
        for (double score : scores) {
            if (min > score) {
                min = score;
            }
        }
        return min;
    }
    public double maxScore() {
        double max = Double.MIN_VALUE;
        for (double score : scores) {
            if (max < score) {
                max = score;
            }
        }
        return max;
    }
}