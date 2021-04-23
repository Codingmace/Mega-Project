
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author MasterWard
 */
public class Pieces {

}

class Pawn extends Piece {

    private boolean wasMoved;

    Pawn(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    boolean move(Square fin) {
        boolean b = super.move(fin);
        wasMoved = true;
        return b;
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();
        int c = this.getColor();

        if (c == 0) {
            if (!wasMoved) {
                if (!board[y + 2][x].isOccupied()) {
                    legalMoves.add(board[y + 2][x]);
                }
            }

            if (y + 1 < 8) {
                if (!board[y + 1][x].isOccupied()) {
                    legalMoves.add(board[y + 1][x]);
                }
            }

            if (x + 1 < 8 && y + 1 < 8) {
                if (board[y + 1][x + 1].isOccupied()) {
                    legalMoves.add(board[y + 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y + 1 < 8) {
                if (board[y + 1][x - 1].isOccupied()) {
                    legalMoves.add(board[y + 1][x - 1]);
                }
            }
        }

        if (c == 1) {
            if (!wasMoved) {
                if (!board[y - 2][x].isOccupied()) {
                    legalMoves.add(board[y - 2][x]);
                }
            }

            if (y - 1 >= 0) {
                if (!board[y - 1][x].isOccupied()) {
                    legalMoves.add(board[y - 1][x]);
                }
            }

            if (x + 1 < 8 && y - 1 >= 0) {
                if (board[y - 1][x + 1].isOccupied()) {
                    legalMoves.add(board[y - 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y - 1 >= 0) {
                if (board[y - 1][x - 1].isOccupied()) {
                    legalMoves.add(board[y - 1][x - 1]);
                }
            }
        }

        return legalMoves;
    }
}

class Queen extends Piece {

    Queen(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        int[] occups = getLinearOccupations(board, x, y);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y) {
                legalMoves.add(board[i][x]);
            }
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x) {
                legalMoves.add(board[y][i]);
            }
        }

        List<Square> bMoves = getDiagonalOccupations(board, x, y);

        legalMoves.addAll(bMoves);

        return legalMoves;
    }

}

class Rook extends Piece {

    Rook(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        int[] occups = getLinearOccupations(board, x, y);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y) {
                legalMoves.add(board[i][x]);
            }
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x) {
                legalMoves.add(board[y][i]);
            }
        }

        return legalMoves;
    }

}

class King extends Piece {

    King(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 1; i > -2; i--) {
            for (int k = 1; k > -2; k--) {
                if (!(i == 0 && k == 0)) {
                    try {
                        if (!board[y + k][x + i].isOccupied()
                                || board[y + k][x + i].getOccupyingPiece().getColor()
                                != this.getColor()) {
                            legalMoves.add(board[y + k][x + i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }

        return legalMoves;
    }

}

class Knight extends Piece {

    Knight(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 2; i > -3; i--) {
            for (int k = 2; k > -3; k--) {
                if (Math.abs(i) == 2 ^ Math.abs(k) == 2) {
                    if (k != 0 && i != 0) {
                        try {
                            legalMoves.add(board[y + k][x + i]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }
            }
        }

        return legalMoves;
    }

}

class Bishop extends Piece {

    Bishop(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    List<Square> getLegalMoves(Board b) {
        Square[][] board = b.getSquareArray();
        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        return getDiagonalOccupations(board, x, y);
    }
}
