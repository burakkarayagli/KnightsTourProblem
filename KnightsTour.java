import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class KnightsTour {
    
    private int size;
    private String search_method;
    private int time_limit;
    private int[][] board;
    public final int[] move_x = {2, 1, -1, -2, -2, -1, 1, 2};
    public final int[] move_y = {1, 2, 2, 1, -1, -2, -2, -1};
    private boolean solution_found = false;
    private static int expanded_node_count = 0;

    public KnightsTour(int size, String search_method, int time_limit) {
        this.size = size;
        this.search_method = search_method;
        this.time_limit = time_limit * 1000;
        this.board = new int[size][size];
        this.solution_found = false;
    }

    public boolean is_valid_move(int x, int y, int[][] board) {
        return x >= 0 && x < size && y >= 0 && y < size && board[x][y] == 0;
    }

    public void general_search() {
        long startTime = System.currentTimeMillis();

        List<Node> frontier = new ArrayList<>();

        board[0][0] = 1;
        frontier.add(new Node(0, 0, board, 1));
        boolean isBFS = search_method.equals("bfs");
        //Run the search until the solution is found or the time limit is reached
        while (!frontier.isEmpty() && System.currentTimeMillis() - startTime < time_limit) {


            Node current_node = frontier.remove(0);
            expanded_node_count++;

            //If the solution is found, print the board and create the path
            if (current_node.move == size * size) {
                solution_found = true;
                create_path(current_node.board, size, "path.txt");
                create_board_file(current_node.board, size, "board.txt");
                break;
            }
            //If BFS, add all the nodes to the end of the frontier
            if (isBFS) {
                frontier.addAll(move_selector(current_node, search_method));
            } else {
                //If DFS, add all the nodes to the beginning of the frontier
                frontier.addAll(0, move_selector(current_node, search_method));
            }
        }
        long endTime = System.currentTimeMillis();
        if (!solution_found) {
            System.out.println("No solution found within the time limit.");
            System.out.println("Expanded node count: " + expanded_node_count);
        }
        else {
            System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
            System.out.println("Expanded node count: " + expanded_node_count);
        }

    }


    //Nodes to be added to the frontier
    private ArrayList<Node> move_selector(Node node, String search_method) {
        int x = node.x;
        int y = node.y;
        int[][] board = deep_copy(node.board);
        int move = node.move;

        ArrayList<Node> nodes = new ArrayList<>();
        switch(search_method) {
            //If BFS, add all the nodes to the frontier
            case "bfs":
                for (int i = 0; i < 8; i++) {
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    if (is_valid_move(new_x, new_y, board)) {
                        int[][] new_board = deep_copy(board);
                        new_board[new_x][new_y] = move + 1;
                        Node new_node = new Node(new_x, new_y, new_board, move + 1);
                        nodes.add(new_node);
                    }
                }
                break;
            //If DFS, add all the nodes to the frontier
            case "dfs":
                for (int i = 0; i < 8; i++) {
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    if (is_valid_move(new_x, new_y, board)) {
                        int[][] new_board = deep_copy(board);
                        new_board[new_x][new_y] = move + 1;
                        Node new_node = new Node(new_x, new_y, new_board, move + 1);
                        nodes.add(new_node);
                    }
                }
                break;
            //If h1b sort by the number of possible moves ascending
            case "h1b": //Visit square with least number of moves
                //[x, y, possible_move_count]
                ArrayList<int[]> possible_move_counts = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    int possible_move_count = 0;
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    
                    if (is_valid_move(new_x, new_y, board)) {
                        int[][] new_board = deep_copy(board);
                        new_board[new_x][new_y] = move + 1;
                        for (int j = 0; j < 8; j++) {
                            int new_x2 = new_x + move_x[j];
                            int new_y2 = new_y + move_y[j];
                            if (is_valid_move(new_x2, new_y2, new_board)) {
                                possible_move_count++;
                            }
                        }
                        possible_move_counts.add(new int[]{new_x, new_y, possible_move_count});
                    }
                }
                Collections.sort(possible_move_counts, (a, b) -> Integer.compare(a[2], b[2]));
                for (int[] move_pair : possible_move_counts) {
                    int[][] new_board = deep_copy(board);
                    new_board[move_pair[0]][move_pair[1]] = move + 1;
                    Node new_node = new Node(move_pair[0], move_pair[1], new_board, move + 1);
                    nodes.add(new_node);
                }
                break;
            case "h2":
                //[x,y,possible_move_count,distance_to_corner]
                possible_move_counts = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    int possible_move_count = 0;
                    int distance_to_corner = 0;
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    
                    if (is_valid_move(new_x, new_y, board)) {
                        int[][] new_board = deep_copy(board);
                        new_board[new_x][new_y] = move + 1;
                        for (int j = 0; j < 8; j++) {
                            int new_x2 = new_x + move_x[j];
                            int new_y2 = new_y + move_y[j];
                            if (is_valid_move(new_x2, new_y2, new_board)) {
                                possible_move_count++;
                                distance_to_corner = min_distance_to_corner(new_x2, new_y2, size);
                            }
                        }
                        possible_move_counts.add(new int[]{new_x, new_y, possible_move_count, distance_to_corner});
                    }
                }
                Collections.sort(possible_move_counts, (a, b) -> {
                    // Compare by number of possible moves
                    if (a[2] != b[2]) {
                        return Integer.compare(a[2], b[2]); // Ascending order
                    }
                    // In case of a tie, compare by distance to the corner
                    return Integer.compare(a[3], b[3]); // Ascending order
                });
                for (int[] move_pair : possible_move_counts) {
                    int[][] new_board = deep_copy(board);
                    new_board[move_pair[0]][move_pair[1]] = move + 1;
                    Node new_node = new Node(move_pair[0], move_pair[1], new_board, move + 1);
                    nodes.add(new_node);
                }
                break;
        }
        return nodes;
    }

    private int[][] deep_copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    
    //Calculate the minimum distance to the corner with the manhattan distance
    private int min_distance_to_corner(int x, int y, int size) {
        int min_distance_to_upper_left = Math.abs(x - 0) + Math.abs(y - 0);
        int min_distance_to_upper_right = Math.abs(size - 1 - x) + Math.abs(y - 0);
        int min_distance_to_lower_left = Math.abs(x - 0) + Math.abs(size - 1 - y);
        int min_distance_to_lower_right = Math.abs(size - 1 - x) + Math.abs(size - 1 - y);
        return Math.min(Math.min(min_distance_to_upper_left, min_distance_to_upper_right), Math.min(min_distance_to_lower_left, min_distance_to_lower_right));
    }

    private void create_path(int board[][], int size, String path_file) {
        int [][] path = new int[size*size][2];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                path[board[i][j]-1][0] = i;
                path[board[i][j]-1][1] = j;
            }
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(path_file))) {
            for (int[] move_pair : path) {
                writer.println(move_pair[0] + "," + move_pair[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void create_board_file(int board[][], int size, String board_file) {
        int max_value = size * size; //Maximum move count
        int cell_width = String.valueOf(max_value).length(); //Cell width (number of digits)
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(board_file))) {
            for (int i = size - 1; i >= 0; i--) {
                for (int j = 0; j < size; j++) {
                    writer.printf("%" + (cell_width + 2) + "s", "[" + board[j][i] + "]");
                }
                writer.println(); // Row end
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        //Inputs: size, search_method, time_limit
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the size of the board: ");
        int size = scanner.nextInt();
        System.out.print("Enter the search method (a,b,c,d): ");
        String search_method = scanner.next();
        System.out.print("Enter the time limit in seconds: ");
        int time_limit = scanner.nextInt();
        scanner.close();
        if (search_method.equals("a")) {
            search_method = "bfs";
        } else if (search_method.equals("b")) {
            search_method = "dfs";
        } else if (search_method.equals("c")) {
            search_method = "h1b";
        } else if (search_method.equals("d")) {
            search_method = "h2";
        }

        KnightsTour knights_tour = new KnightsTour(size, search_method, time_limit);
        try {
            knights_tour.general_search();
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory error");
            System.out.println("Expanded node count: " + expanded_node_count);
        }
    }
}