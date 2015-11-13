package summarizer.utils;

import java.util.Stack;

/**
 * Created by atone on 15/6/23.
 * TSP using the farthest neighbor
 */
public class TSPFarthestNeighbor {
    private int numberOfNodes;
    private Stack<Integer> stack;

    public TSPFarthestNeighbor() {
        stack = new Stack<Integer>();
    }

    private int getFirstNode(int adjacencyMatrix[][]) {
        int numberOfNodes = adjacencyMatrix[1].length - 1;
        int max = Integer.MIN_VALUE;
        int ret = 0;

        for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = 1; j <= numberOfNodes; j++) {
                if (adjacencyMatrix[i][j] > max) {
                    max = adjacencyMatrix[i][j];
                    ret = i;
                }
            }
        }
        return ret;
    }

    public int[] tsp(int adjacencyMatrix[][]) {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        boolean[] visited = new boolean[numberOfNodes + 1];
        int first = getFirstNode(adjacencyMatrix);
        visited[first] = true;
        stack.push(first);
        int element, dst = 0, i;
        int max = Integer.MIN_VALUE;
        boolean maxFlag = false;

        int[] path = new int[numberOfNodes];
        int pathIndex = 0;
        path[pathIndex++] = first;

        while (!stack.isEmpty()) {
            element = stack.peek();
            i = 1;
            max = Integer.MIN_VALUE;
            while (i <= numberOfNodes) {
                if (adjacencyMatrix[element][i] > 0 && !visited[i]) {
                    if (max < adjacencyMatrix[element][i]) {
                        max = adjacencyMatrix[element][i];
                        dst = i;
                        maxFlag = true;
                    }
                }
                i++;
            }

            if (maxFlag) {
                visited[dst] = true;
                stack.push(dst);
                path[pathIndex++] = dst;
                maxFlag = false;
                continue;
            }
            stack.pop();
        }
        return path;
    }
}
