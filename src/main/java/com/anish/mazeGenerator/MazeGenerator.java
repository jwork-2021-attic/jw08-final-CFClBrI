package com.anish.mazeGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import java.util.Random;

public class MazeGenerator {
    
    private Stack<Node> stack = new Stack<>();
    private Queue<Node> queue = new LinkedList<>();
    private Random rand = new Random();
    private int[][] maze;
    private int[][] seen;
    private int dimension;

    public MazeGenerator(int dim) {
        maze = new int[dim][dim];
        seen = new int[dim][dim];
        dimension = dim;
    }

    public void generateMaze() {
        stack.push(new Node(0,0));
        while (!stack.empty()) {
            Node next = stack.pop();
            if (validNextNode(next)) {
                maze[next.y][next.x] = 1;
                ArrayList<Node> neighbors = findNeighbors(next);
                randomlyAddNodesToStack(neighbors);
            }
        }
        randomlyReviseNodes();
        bfs();
    }

    public int[][] getMaze() {
        return seen;
    }



    private void bfs() {
        seen[0][0] = 2;
        queue.add(new Node(0, 0));
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            ArrayList<Node> neighbors = findNeighbors(curr);
            for (int i = 0; i < neighbors.size(); i++) {
                Node neighbor = neighbors.get(i);
                if (seen[neighbor.y][neighbor.x] == 0 &&
                    maze[neighbor.y][neighbor.x] == 1) {
                    seen[neighbor.y][neighbor.x] = 2;
                    queue.add(neighbor);
                }                
            }
            seen[curr.y][curr.x] = 1;
        }
    }

    private void randomlyReviseNodes() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (maze[i][j] == 0 && rand.nextInt(10000) % 2 == 0) {
                    maze[i][j] = 1;
                }
            }
        }
    }

    private boolean validNextNode(Node node) {
        int numNeighboringOnes = 0;
        for (int y = node.y-1; y < node.y+2; y++) {
            for (int x = node.x-1; x < node.x+2; x++) {
                if (pointOnGrid(x, y) && pointNotNode(node, x, y) && maze[y][x] == 1) {
                    numNeighboringOnes++;
                }
            }
        }
        return (numNeighboringOnes < 3) && maze[node.y][node.x] != 1;
    }

    private void randomlyAddNodesToStack(ArrayList<Node> nodes) {
        int targetIndex;
        while (!nodes.isEmpty()) {
            targetIndex = rand.nextInt(nodes.size());
            stack.push(nodes.remove(targetIndex));
        }
    }

    private ArrayList<Node> findNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();
        for (int y = node.y-1; y < node.y+2; y++) {
            for (int x = node.x-1; x < node.x+2; x++) {
                if (pointOnGrid(x, y) && pointNotCorner(node, x, y)
                    && pointNotNode(node, x, y)) {
                    neighbors.add(new Node(x, y));
                }
            }
        }
        return neighbors;
    }

    private Boolean pointOnGrid(int x, int y) {
        return x >= 0 && y >= 0 && x < dimension && y < dimension;
    }

    private Boolean pointNotCorner(Node node, int x, int y) {
        return (x == node.x || y == node.y);
    }
    
    private Boolean pointNotNode(Node node, int x, int y) {
        return !(x == node.x && y == node.y);
    }
}