package Chess;

public class AICaller extends Thread {

    /**
     * A reference to the parent Chess class.
     *
     * @see class Chess
     */
    Chess chess;

    boolean bStart = false;

    public void go() {
        bStart = true;
    }

    public void cancel() {
        bStart = false;
    }

    public void exit() {
        bRunning = false;
    }

    /**
     * A boolean variable indicating whether the AICaller class is running. This
     * prevents the possibility of two threads simultaneously manipulating the
     * chess board. This should never happen, but one can never be too safe.
     */
    private static boolean bRunning = false;

    public AICaller(Chess chess) {
        this.chess = chess;
    }

    /**
     * This function is called when a player moves a piece and it is the AI's
     * turn to make a move. It simply processes the search in a separate thread
     * so the application is not locked up. The thread will exit if
     * chess.bThinking is falsified.
     */
    @Override
    public void run() {
        if (bRunning) {
            return;
        }
        bRunning = true;

        while (!Chess.main.bQuit && bRunning) {
            if (bStart) {
                bStart = false;

                Chess.bThinking = true;

                Chess.main.difficultySlider.setEnabled(false);
                Chess.main.chk_IterativeDeep.setEnabled(false);
                Chess.main.butt_SetupBoard.setEnabled(false);

                ChessPosition n = chess.playGame(Chess.pos, Chess.PROGRAM);
                if (Chess.bThinking) {
                    Chess.bThinking = false; // consider removing the bRunning?
                    Chess.pos = n;
                }
                Chess.main.difficultySlider.setEnabled(true);
                Chess.main.chk_IterativeDeep.setEnabled(true);
                Chess.main.butt_SetupBoard.setEnabled(true);
            }

            try {
                Thread.currentThread().sleep(50);
            } catch (InterruptedException ex) {
                System.out.println("AICaller Thread Sleep Error");
            }
        }
        bRunning = false;
    }
}
