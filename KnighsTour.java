import java.util.*;

public class KnighsTour {
    
    private int size;
    private String[] search_method;
    private int time_limit;
    private int[][] board;
    private boolean solution_found;

    public final int[] move_x = {2, 1, -1, -2, -2, -1, 1, 2};
    public final int[] move_y = {1, 2, 2, 1, -1, -2, -2, -1};
    

    public KnighsTour(int size, String[] search_method, int time_limit) {
        this.size = size;
        this.search_method = search_method;
        this.time_limit = time_limit;
        this.board = new int[size][size];
    }

    public boolean is_valid_move(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size && board[x][y] == 0;
    }

    private boolean is_valid_move_with_board(int x, int y, int[][] board) {
        return x >= 0 && x < size && y >= 0 && y < size && board[x][y] == 0;
    }

    public void general_search(String search_method) {
        long startTime = System.currentTimeMillis();
        List<int[]> frontier = new ArrayList<>();
        frontier.add(new int[]{0, 0});
        switch(search_method) {
            case "bfs":
                while (!frontier.isEmpty() && System.currentTimeMillis() - startTime < time_limit) {
                    int[] current_node = frontier.remove(0);
                    int x = current_node[0];
                    int y = current_node[1];
                    board[x][y] = 1;
                    frontier.addAll(move_selector(x, y, board, search_method));
                }
                break;
            case "dfs":
                while (!frontier.isEmpty() && System.currentTimeMillis() - startTime < time_limit) {
                    int[] current_node = frontier.remove(0);
                    int x = current_node[0];
                    int y = current_node[1];
                    board[x][y] = 1;
                    frontier.addAll(move_selector(x, y, board, search_method));
                }
                break;
            case "h1b":
                break;
            case "h2":
                break;
        }
    }

    private ArrayList<int[]> move_selector(int x, int y, int[][] board, String search_method) {
        //[x1, y1], [x2, y2], ...
        ArrayList<int[]> nodes = new ArrayList<>();
        switch(search_method) {
            case "bfs":
                for (int i = 0; i < 8; i++) {
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    if (is_valid_move(new_x, new_y)) {
                        nodes.add(new int[]{new_x, new_y});
                    }
                }
                break;
            case "dfs":
                for (int i = 0; i < 8; i++) {
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    if (is_valid_move(new_x, new_y)) {
                        nodes.add(new int[]{new_x, new_y});
                    }
                }
                break;
            case "h1b": //Visit square with least number of moves
                ArrayList<int[]> possible_move_counts = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    int possible_move_count = 0;
                    int new_x = x + move_x[i];
                    int new_y = y + move_y[i];
                    
                    if (is_valid_move(new_x, new_y)) {
                        int[][] temp_board = board.clone();
                        temp_board[new_x][new_y] = 1;
                        for (int j = 0; j < 8; j++) {
                            int new_x2 = new_x + move_x[j];
                            int new_y2 = new_y + move_y[j];
                            if (is_valid_move_with_board(new_x2, new_y2, temp_board)) {
                                possible_move_count++;
                            }
                        }
                        possible_move_counts.add(new int[]{new_x, new_y, possible_move_count});
                    }
                }
                Collections.sort(possible_move_counts, (a, b) -> Integer.compare(a[2], b[2]));
                for (int[] move : possible_move_counts) {
                    nodes.add(new int[]{move[0], move[1]});
                }
                break;
            case "h2":
                break;
        }
        return nodes;
    }

    



    




}
