public class Node {
    public int x;
    public int y;
    public int[][] board;
    public int move;

    public Node(int x, int y, int[][] board, int move) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.move = move;
    }
}
