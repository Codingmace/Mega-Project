package Chess;

public class ChessMove {// implements Comparator

    public int from;
    public int to;

    private static char[] rankNames = {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a', '/', '/'};

    public String squareString(int square_index) {
        return "" + rankNames[square_index % 10] + (square_index / 10 + 1);
    }

    @Override
    public String toString() {
        if (from == 0 && to == 0) {
            return "..";
        }
        return squareString(from) + squareString(to);
    }

    public ChessMove() {
    }

    public ChessMove(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public ChessMove(ChessMove m) {
        from = m.from;
        to = m.to;
    }
}
