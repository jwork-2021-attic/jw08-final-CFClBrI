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
    private int dimension;
    private int initAreaLength;
    private int cherryLimit = 3;
    private int drugLimit = 2;
    private int playerCount = 2;
    private int[][] playerPositons;

    public MazeGenerator(int dim, int playerCount) {
        maze = new int[dim][dim];
        playerPositons = new int[playerCount][2];
        dimension = dim;
        this.playerCount = playerCount;
        initAreaLength = dim - 10;
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
        setPlayers();
        setThings();
    }

    public int[][] getMaze() {
        return maze;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int[][] getPlayerPositions() {
        return playerPositons;
    }

    private void setPlayers() {
        for (int i = 0; i < playerCount; i++) {
            int pos = 0, posX = 0, posY = 0;
            do {
                pos = rand.nextInt(initAreaLength * initAreaLength);
                posX = pos / initAreaLength;
                posY = pos % initAreaLength;
            } while (posX == 0 || posY == 0 || maze[posX][posY] == 0);
            playerPositons[i][0] = posX;
            playerPositons[i][1] = posY;
            maze[posX][posY] = 0;
        }
    }

    private void setThings() {
        int cherryCount = 0, drugCount = 0, beanCount = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (maze[i][j] == 0 || i == dimension - 1 || j == dimension - 1) {
                    continue;
                } 
                if (rand.nextInt(10000) % 2 == 0) { //bean
                    maze[i][j] = 2;
                    beanCount++;
                }      
                else if (cherryCount < cherryLimit && 
                        rand.nextInt(10000) % 80 == 0) { //cherry
                    maze[i][j] = 3;
                    cherryCount++;
                }
                else if (drugCount < drugLimit &&
                        rand.nextInt(10000) % 80 == 0) { //drug
                    maze[i][j] = 4;
                    drugCount++;
                }                          
            }
        }
        if (beanCount % playerCount == 0) {
            maze[0][0] = 2;
        }
        else {
            maze[0][0] = 1;
        }        
    }

    private void bfs() {
        int[][] seen = new int[dimension][dimension];
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
        maze = seen;
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