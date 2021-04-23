package Chess;

public class ChessPosition {

    final static public int BLANK = 0;
    final static public int PAWN = 1;
    final static public int KNIGHT = 2;
    final static public int BISHOP = 3;
    final static public int ROOK = 4;
    final static public int QUEEN = 5;
    final static public int KING = 6;

    /**
     * An array of board squares.
     */
    public int[] board = new int[80];

    /**
     * Stores the index of pieces on the board. Exists for faster move
     * generation.
     */
    boolean bWhiteKingMoved = false;
    boolean bBlackKingMoved = false;

    boolean bWhiteChecked = false;
    boolean bBlackChecked = false;

    int enPassantSquare = 0;

    /**
     * Applies the given move parameter to the board position saved in this
     * instance of the class.
     *
     * @param move Piece Moving
     */
    public void makeMove(ChessMove move) {
        board[move.to] = board[move.from];
        board[move.from] = 0;

        if (move.to >= 70) {
            if (board[move.to] == PAWN) {
                board[move.to] = QUEEN;
            }
        } else if (move.to < 8) {
            if (board[move.to] == -PAWN) {
                board[move.to] = -QUEEN;
            }
        } else if (board[move.to] == KING && !bWhiteKingMoved) {
            bWhiteKingMoved = true;
        } else if (board[move.to] == -KING && !bBlackKingMoved) {
            bBlackKingMoved = true;
        }
    }

    /**
     * Instantiates the board position by mirroring another board position. Used
     * extensively during alpha-beta search.
     *
     * @param p
     */
    public ChessPosition(ChessPosition p) {
        System.arraycopy(p.board, 0, board, 0, 80);
        //eval = p.eval;
        bWhiteKingMoved = p.bWhiteKingMoved;
        bBlackKingMoved = p.bBlackKingMoved;

        bWhiteChecked = p.bWhiteChecked;
        bBlackChecked = p.bBlackChecked;

    }

    /**
     * Constructs an empty chess board.
     */
    public ChessPosition() {
    }
}
