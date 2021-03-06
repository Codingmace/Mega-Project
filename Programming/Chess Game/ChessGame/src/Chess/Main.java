package Chess;


/**
 * Things to do 
 * Modify the Help icon to a the correct logo
 * Modifying the load to do the correct thing
 * Modify the load for a file selector for the path that is already set;
 * Adjust the size of the window
 * Change the size 
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import java.util.TimerTask;
import java.util.Vector;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.JTable;

public class Main extends JFrame implements Runnable, MouseListener, MouseMotionListener, ActionListener, ChangeListener {

    /**
     * Our version counter
     */
    final public static String VERSION = "1.4.1";

    /**
     * The omni-present Chess reference, used nearly everywhere for calculations
     * and drawing.
     */
    public static Chess chess;

    /**
     * Stores the width to which Chessmate should size its window.
     */
    public static final int WINDOW_WIDTH = 500 + 220; // 160 is the extra to width

    /**
     * Stores the height to which Chessmate should size its window.
     */
    public static final int WINDOW_HEIGHT = 400 + 67 + 40; // 160 is the extra to the height

    /**
     * The width of a chess board square, drawn as either light or dark.
     */
    public static final char TILE_WIDTH = 50;

    /**
     * The height of a chess board square, drawn as either light or dark.
     */
    public static final char TILE_HEIGHT = 50;

    /**
     * The width of the chess board, used for drawing calculations.
     */
    public static final int BOARD_WIDTH = TILE_WIDTH * 8;

    /**
     * The height of the chess board, used for drawing calculations.
     */
    public static final int BOARD_HEIGHT = TILE_HEIGHT * 8;

    /**
     * The height of the informative tabbed pane to the right of the screen.
     */
    public static final int TAB_WIDTH = 290;
    public static final int TAB_HEIGHT = 440;

    /**
     * The horizontal drawing offset, used by double-buffering routine.
     */
    public static int HORZ_OFFSET = 3;

    /**
     * The vertical drawing offset, used by double-buffering routine.
     */
    public static int VERT_OFFSET = 100;

    /**
     * Horizontal board drawing offset. Added to HORZ_OFFSET by double-buffering
     * technique.
     */
    public static final int BOARD_HORZ_OFFSET = 0;

    /**
     * Vertical board drawing offset. Added to HORZ_OFFSET by double-buffering
     * technique.
     */
    public static final int BOARD_VERT_OFFSET = 100;

    /**
     * An off-screen buffer used for double buffering. See the update(...)
     * function.
     */
    private Image offScreen;

    /**
     * When bRedraw is true, the main thread will redraw the window in less than
     * 25ms.
     */
    private boolean bRedraw = true; // the board will be redrawn when this is true

    int moveTime = 0;

    /**
     * Our right-aligned tab which keeps all our game information.
     */
    private final JTabbedPane tabbedPane;

    /**
     * A graph showing an AI interpretation of who's winning.
     */
    public Graph graph = new Graph();

    /**
     * A scroll accompanying the Graph class
     *
     * @see graph
     */
    private JScrollPane graphScroll = new JScrollPane(graph);

    /**
     * A difficulty slider for adjusting AI search depth
     */
    JSlider difficultySlider = new JSlider(1, 6, Chess.maxDepth);

    /**
     * A list of moves by the players to be displayed in a table.
     *
     * @see moveTable
     * @see moveTable_dataModel
     */

    ArrayList moveList = new ArrayList();

    /**
     * An abstract table model for rendering the moves list.
     */
    TableModel moveTable_dataModel = new AbstractTableModel() {
        @Override
        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            if (moveList.size() == 1) {
                return 1;
            }
            return moveList.size() / 2 + moveList.size() % 2;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (moveList.size() > 0) {
                if (col == 0) {
                    Integer r = row + 1;
                    return (Object) r;
                }

                int i = row * 2;
                if (col == 2) {
                    ++i;
                }
                if (moveList.size() > i) {
                    return moveList.get(i);
                }
            }
            return "";
        }
    };

    /**
     * A table for drawing the moves stored in moveList.
     *
     * @see moveList
     */
    JTable moveTable = new JTable(moveTable_dataModel);

    /**
     * A scroll pane accompanying the moves table.
     *
     * @see moveTable
     * @see moveList
     */
    JScrollPane moveTable_scrollPane = new JScrollPane(moveTable);

    /**
     * An array of toolbar images.
     */
    static Image[] toolbarImages = new Image[6];

    JButton butt_NewGame;
    JButton butt_LoadGame;
    JButton butt_SaveGame;
    JButton butt_Takeback;
    JButton butt_SetupBoard;
    JButton butt_Flip;

    /**
     * A panel to contain the toolbar buttons
     *
     * @see butt_NewGame
     * @see butt_LoadGame
     * @see butt_SaveGame
     * @see butt_Takeback
     * @see butt_SetupBoard
     */
    Panel toolPanel;

    /**
     * A menu bar to contain the menu items listed below.
     */
    MenuBar menuBar;

    // Menu Items
    Menu menu_Game;

    MenuItem menu_Game_Takeback;
    MenuItem menu_Game_Exit;

    Menu menu_Options;

    /**
     * A timer to update the screen and the Game Tab for node information
     */
    java.util.Timer timer = new java.util.Timer();

    /**
     * The AICaller class handles AI threading so nothing gets stucky.
     */
    static AICaller aiCaller;

    /**
     * hoverPiece holds the index of the piece currently picked up by the
     * player.
     */
    static int hoverPiece = 0;

    /**
     * Stores the most recent mouse coordinates. Used by drawing routines.
     */
    static int mouse_x = 0, mouse_y = 0;

    /**
     * If bQuit is true, all threads will exit and the application will close.
     */
    public boolean bQuit = false;

    /**
     * bPlaying is true when the game is in progress - not for example when the
     * board is being set up.
     */
    public boolean bPlaying = true; // are we playing
    /**
     * bSetPosition is true when the player is setting up the board. Kind of
     * self-explanatory.
     */
    public static boolean bSetPosition = false;

    /**
     * The MediaTracker keeps track of all images loaded, making sure they are
     * loaded before drawing them.
     */
    MediaTracker tracker;

    /**
     * An integer holding the number of images tracked by the MediaTracker.
     * Useful for ID assignment by media tracker.
     *
     * @see tracker
     */
    static int trackerCount = 0; // incremented for each image to be loaded

    /**
     * An image holding the strip of black and white pieces.
     */
    static Image strip; // the image strip

    /**
     * An array of images holding the piece images extracted from the image
     * strip. Used by drawing routines.
     *
     * @see strip
     */
    static Image images[]; // the constituent images

    /**
     * Holds the number of piece images in the image strip - should always be
     * 12.
     */
    int num_images = 0;
    int piece_width = 50; // width of each image in strip
    int height;

    JLabel field_Nodes = new JLabel("0");
    JLabel field_NodesSecond = new JLabel("0");
    JLabel field_Depth = new JLabel("0");
    JLabel field_Score = new JLabel("0");
    JLabel field_Thinking = new JLabel();
    JLabel field_MoveTime = new JLabel("0 seconds");

    JRadioButton radio_White = new JRadioButton("Play as White", true);
    JRadioButton radio_Black = new JRadioButton("Play as Black");
    ButtonGroup radioGroup = new ButtonGroup();
    JPanel radioPanel = new JPanel(new GridLayout(0, 1));

    JCheckBox chk_VisualThinking = new JCheckBox("Visual Thinking (affects performance)", false);
    static boolean bVisualThinking = false;

    JCheckBox chk_SlowRedraws = new JCheckBox("Slow Redraws (better performance)", false);
    static boolean bSlowRedraws = false;

    JCheckBox chk_IterativeDeep = new JCheckBox("Iterative Deepening (prunes search tree)", true);

    Panel infoPanel;

    static boolean bFlipBoard = true;

    /**
     * Alert Displays a dialog containing useful information.
     *
     * @param title
     * @param message
     */
    public void alert(String title, String message) {
        String[] SaveOptionNames = {"Continue"};
        JLabel label = new JLabel(message);

        JOptionPane.showOptionDialog(this, label, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, SaveOptionNames, SaveOptionNames[0]);
    }

    /**
     * Extract the constituent images from a strip. There are num_images to
     * extract, each with the specified width and height.
     *
     * @param strip
     * @param images
     * @param num_images
     * @param width
     * @param height
     */
    public void extractImages(Image strip, Image images[], int num_images, int width, int height) {
        ImageProducer source = strip.getSource();
        for (int i = 0; i < num_images; i++) {
            // define filter to pull image at (i*width,0) with
            // dimensions (width,height)
            ImageFilter extractFilter = new CropImageFilter(i * width, 0, width, height);
            // define producer from source and filter
            ImageProducer producer = new FilteredImageSource(source, extractFilter);
            // extract the subimage!
            images[i] = createImage(producer);
        }
    }

    /**
     * This determines the square to which a certain mouse position refers.
     */
    int calcSquare(int mx, int my) {
        int sx = mx / TILE_WIDTH;
        int sy = my / TILE_HEIGHT;
        if (mx < 0 || my < 0 || sx >= 8 || sy >= 8) { // Out of the grid
            return -1;
        }
        return (bFlipBoard ? (7 - sy) * 10 + (7 - sx) : sy * 10 + sx);
    }

    /**
     * Instead of close database could do a dialog box to ask if saving
     *
     * @return
     */
    public int Exit() {
        dispose();

        System.exit(0);

        return 0;
    }

    /**
     * Used for drawing a blue rectangle around most recent move.
     */
    public static ChessMove lastMove = new ChessMove();

    public void playerMoved(boolean player, ChessMove move) {
        // test to enable en-passant
        if (Chess.pos.board[move.from] == (player ? ChessPosition.PAWN : -ChessPosition.PAWN)) {
            int offset = move.to - move.from;
            if (offset < 0) {
                offset = -offset;
            }
            if (offset == 20) { // i.e. moved two square
                Chess.pos.enPassantSquare = move.to;
            }
        } else {
            Chess.pos.enPassantSquare = 0;
        }

        // adding previous position to the board history
        ChessPosition p = new ChessPosition(Chess.pos);
        Chess.boardHistory.push(p);

        ChessPosition checkPos = new ChessPosition(p);

        checkPos.bBlackChecked = false;
        checkPos.bWhiteChecked = false;
        checkPos.makeMove(move);
        chess.calcPossibleMoves(checkPos, player);

        /**
         * Do some checkmate testing
         */
        if (!bSetPosition) {
            if (player == Chess.BLACK && checkPos.bBlackChecked) {
                Chess.bThinking = false;
                alert("Checkmate", "Black is checkmated.");
            } else if (player == Chess.WHITE && checkPos.bWhiteChecked) {
                Chess.bThinking = false;
                alert("Checkmate", "White is checkmated.");
            }
        }

        lastMove = move;

        // add the move to our move list
        moveList.add(move.toString());
        moveTable_scrollPane.getViewport().updateUI();

        moveTime = 0;

        if (player == Chess.PROGRAM) {
            Graph.data.add(Chess.bestMoveEval);
            graph.repaint();
        }
        if (Chess.bestMoveEval >= 1000.0f) {
            field_Score.setText("Mate in " + (int) (Chess.maxDepth - 1 - (int) (Chess.bestMoveEval / 1000.0f) / 2));
        } else if (Chess.bestMoveEval <= -1000.0f) {
            field_Score.setText("Mate in " + (int) (Chess.maxDepth - 1 - (Chess.bestMoveEval / -1000.0f) / 2));
        } else {
            field_Score.setText(Float.toString(Chess.bestMoveEval));
        }

        chess.bWhoseTurn = !player;

        // our little test for letting Chessmate play against himself :)
/*		chess.PROGRAM = !chess.PROGRAM;
		chess.HUMAN = !chess.HUMAN;
		if ( chess.maxDepth == 6 )
			chess.maxDepth = 4;
		else
		{
			chess.maxDepth = 6;
		}

		System.out.println( (chess.PROGRAM ? "White" : "Black") + " is playing at " + chess.maxDepth );

		aiCaller.go();
         */
        /**
         * We are not repainting, since the auto-painter thread will
         * auto-refresh our fields
         */
        bRedraw = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * When the player releases his mouse, this function checks if it is his
     * turn and calculates the appropriate squares. Unless the board is being
     * set up, this function also starts the AI thread.
     *
     * @param e
     */

    @Override
    public void mouseReleased(MouseEvent e) {
        while (true) // a dummy for easy error-checking
        {
            if (hoverPiece > 0 && !Chess.bThinking) {
                int x = e.getX() - HORZ_OFFSET; // - BOARD_HORZ_OFFSET;
                int y = e.getY() - VERT_OFFSET; // - BOARD_VERT_OFFSET;

                if (x <= BOARD_WIDTH && y <= BOARD_HEIGHT) {
                    int square = calcSquare(x, y);

                    if (square + 1 != hoverPiece) {
                        // Now we must test whether this move is valid

                        ChessMove move = new ChessMove();
                        move.from = hoverPiece - 1;
                        move.to = square;

                        if (bPlaying) {
                            if (!chess.isValidMove(Chess.pos, move)) {
                                break;
                            }
                        }

                        playerMoved(Chess.HUMAN, move);
                        Chess.pos.makeMove(move);

                        // This is the queue for the PC to start THINKING...
                        if (bPlaying) {
                            aiCaller.go();
                            //chess.pos = chess.playGame( chess.pos );
                        }
                    }
                }
            }
            break;
        }

        hoverPiece = 0;
        bRedraw = true;
    }

    /**
     * When the user clicks on the screen, this function checks whether he/she
     * has picked up a piece and stores it in hoverPiece.
     *
     * @param e
     */

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() - HORZ_OFFSET; // ..BOARD_HORZ_OFFSET;
        int y = e.getY() - VERT_OFFSET; // BOARD_VERT_OFFSET;

        if (x <= BOARD_WIDTH && y <= BOARD_HEIGHT) {
            int square = calcSquare(x, y);

            if (square >= 0 && square <= 80) {
                if (Chess.pos.board[square] != ChessPosition.BLANK) {
                    //System.out.println("Pressed on square " + square);
                    if (bSetPosition || (Chess.HUMAN ? (Chess.pos.board[square] > 0) : (Chess.pos.board[square] < 0))) {
                        hoverPiece = square + 1;
                    } else {
                        hoverPiece = 0;
                    }
                }
            }
        } else {
            hoverPiece = 0;
        }
    }

    /**
     * This function detects changes to the AI search depth slider and adjusts
     * the AI search depth appropriately.
     *
     * @param e
     */

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == difficultySlider) {
            Chess.maxDepth = difficultySlider.getValue();
        }
    }

    /**
     * All window messages arrive here, mainly menu and toolbar clicks.
     *
     * @param e
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == butt_NewGame) {
            NewGame();
        } else if (source == menu_Game_Takeback || source == butt_Takeback) {
            lastMove = new ChessMove();

            chess.Takeback();

            if (moveList.size() > 0) {
                moveList.remove(moveList.size() - 1);
            }

            if (Chess.bThinking) {
                Chess.bThinking = false;
            } else {
                chess.Takeback();
                if (moveList.size() > 0) {
                    moveList.remove(moveList.size() - 1);
                }
                if (Graph.data.size() > 0) {
                    Graph.data.removeElementAt(Graph.data.size() - 1);
                }
            }
            repaint();
        } else // Menu stuff
        if (source == butt_SetupBoard) {
            SetupBoard();
        } else if ( source == butt_LoadGame) {
            LoadGame();
        } else if (source == butt_SaveGame) {
            SaveGame();
        } else if (source == menu_Game_Exit) {
            System.out.println("#Test to save game here!");
            Exit();
        } else // Options Menu goes here
        if (source == butt_Flip) {
            bFlipBoard = !bFlipBoard;
        } else if (source == chk_IterativeDeep) {
            chess.bIterativeDeepening = chk_IterativeDeep.isSelected();
        }
        // Visual Thinking check box
        if (source == chk_VisualThinking) {
            bVisualThinking = ((JCheckBox) source).isSelected();//!bVisualThinking;
        } else if (source == chk_SlowRedraws) {
            bSlowRedraws = ((JCheckBox) source).isSelected();
        } else if (source == radio_White) {
            if (Chess.bThinking) {
                Chess.bThinking = false;
            }
            Chess.HUMAN = Chess.WHITE;
            Chess.PROGRAM = Chess.BLACK;
            bFlipBoard = true;
            if (chess.bWhoseTurn == Chess.PROGRAM) {
                aiCaller.go();
            }
        } else if (source == radio_Black) {
            if (Chess.bThinking) {
                Chess.bThinking = false;
            }
            Chess.HUMAN = Chess.BLACK;
            Chess.PROGRAM = Chess.WHITE;
            bFlipBoard = false;
            if (moveList.isEmpty() || chess.bWhoseTurn == Chess.PROGRAM) {
                aiCaller.go();
            }
        }

        repaint();
    }

    /**
     * Not used by Chessmate
     *
     * @param e
     */
    @Override

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * When the player has picked up a piece, and is moving around the mouse,
     * this function detects the movement and redraws the hovering piece
     * wherever it is.
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        if (hoverPiece != 0) {
            bRedraw = true;
        }
    }

    /**
     * Stores the most recent mouse coordinates in mouse_x and mouse_y.
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Loads all images and tracks them with MediaTracker
     *
     * @see tracker
     */
    public void LoadImages() {
        System.out.println("init() called");
        tracker = new MediaTracker(this);
        Image strip = Toolkit.getDefaultToolkit().getImage("images/alpha.gif");
        tracker.addImage(strip, 0);
        ++trackerCount;

        toolbarImages[0] = Toolkit.getDefaultToolkit().getImage("images/icon_new.gif");
        toolbarImages[1] = Toolkit.getDefaultToolkit().getImage("images/icon_loadgame.gif");
        toolbarImages[2] = Toolkit.getDefaultToolkit().getImage("images/icon_savegame.gif");
        toolbarImages[3] = Toolkit.getDefaultToolkit().getImage("images/icon_takeback.gif");
        toolbarImages[4] = Toolkit.getDefaultToolkit().getImage("images/icon_setupboard.gif");
        toolbarImages[5] = Toolkit.getDefaultToolkit().getImage("images/icon_help.gif");
        for (int i = 0; i < 6; i++) {
            tracker.addImage(toolbarImages[i], trackerCount++);
        }
        try {
            System.out.println("Loading images...");
            tracker.waitForAll();
        } catch (InterruptedException e) {
            System.out.println("There was an error loading the piece image strip.");
            return;
        }

        // Load Piece Images for Black & White from an image strip
        // define number of images in strip
        num_images = strip.getWidth(this) / piece_width;

        // define height of each image
        height = strip.getHeight(this);

        // define array of constituent images
        images = new Image[num_images];

        // extract constituent images
        extractImages(strip, images, num_images, piece_width, height);

        // Track the loading of images with MediaTracker
        for (int i = 0; i < num_images; i++) {
            tracker.addImage(images[i], trackerCount++);
        }

        try {
            System.out.println("Loading images, please wait...");
            tracker.waitForAll();
        } catch (InterruptedException e) {
            System.out.println("There was an error loading a piece's image file.");
        }

    }

    /**
     * A class to hold a loadable game from the database.
     */
    static class OldGame {

        int id; // all important!
        String desc;
        String szDate;
        ChessPosition pos;
    }

    /**
     * A list of old games for loading. Filled up by populateLoadGames()
     *
     * @see populateLoadGames
     */
    Vector loadGames = new Vector();

    /**
     * An abstract table model for loadable games in the Load Game window.
     */
    TableModel gameTable_dataModel = new AbstractTableModel() {
        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return loadGames.size();
        }

        public Object getValueAt(int row, int col) {
            OldGame og = (OldGame) loadGames.get(row);
            switch (col) {
                case 0:
                    return og.desc;
                case 1:
                    return og.szDate;
            }
            return new String();
        }
    };
    /**
     * A JTable for rendering loadable games to in the Load Game window.
     */
    JTable gameTable = new JTable(gameTable_dataModel);
    /**
     * A scroll pane to accompany the list of loadable games in the Load Game
     * window.
     *
     * @see gameTable
     */
    JScrollPane gameTable_scrollPane = new JScrollPane(gameTable);

    Button dlgButt_LoadGame = new Button("Load Game");
    /**
     * A reusable dialog for displaying input prompts, for example when loading
     * and saving games.
     */
    JDialog dialog;

    /**
     * This function is called when the user selects to load a game either from
     * the menu or by using the toolbar Load Game button. This function will
     * retrieve a list of loadable games from the data source and display them
     * in a table. The user may choose a game and the playing area will be
     * updated accordingly.
     *
     * @see dialog
     * @see dlgButt_LoadGame
     * @see gameTable
     * @see populateLoadGames
     */
    public void LoadGame() {
        dialog = new JDialog(this, "Load Game", true);
        dialog.setSize(600, 320);
        dialog.setResizable(false);

        /**
         * Write a file of what did In populate read in and put the placements
         * on the grid
         */
        gameTable.getColumnModel().getColumn(0).setHeaderValue("Game Description");
        gameTable.getColumnModel().getColumn(1).setHeaderValue("Date");

        Container con = dialog.getContentPane();

        populateLoadGames();

        gameTable.getTableHeader().setReorderingAllowed(false);

        con.setLayout(null);

        gameTable_scrollPane.setBounds(0, 0, 600, 250);
        gameTable.setBounds(0, 0, 600, 250);

        con.add(gameTable_scrollPane);

        dlgButt_LoadGame.setBounds(0, 250, 200, 40);
        con.add(dlgButt_LoadGame);
        dlgButt_LoadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = gameTable.getSelectedRow();
                if (index == -1) {
                    return;
                }

                OldGame og = (OldGame) loadGames.get(index);

                NewGame();

                Chess.main.setTitle(og.desc);
                Chess.pos = new ChessPosition(og.pos);

                chess.bWhoseTurn = Chess.HUMAN;

                dialog.dispose();
            }
        });

        Button dlgButt_Delete = new Button("Delete");
        dlgButt_Delete.setBounds(200, 250, 200, 40);
        dlgButt_Delete.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = gameTable.getSelectedRow();
                if (index < 0) {
                    return;
                }

                populateLoadGames();
                gameTable.repaint();

                System.out.println("Deleted game from database.");
            }
        }
        );
        con.add(dlgButt_Delete);

        Button dlgButt_Close = new Button("Close");
        dlgButt_Close.setBounds(400, 250, 200, 40);
        dlgButt_Close.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        }
        );
        con.add(dlgButt_Close);

        dialog.show();
    }

    /**
     * This function retrieves a list of loadable games from the data source
     * with a SELECT query and stores the games as an array of OldGame classes
     * in loadGames, to be displayed by the gameTable.
     *
     * @see gameTable
     * @see loadGames
     */
    int populateLoadGames() {
        System.out.println("Populating the field right now");
        System.out.println("Clear the current game");
        System.out.println("Write in what is in the file");
        return 0;
    }

    static String[] SaveOptionNames = {"Save", "Cancel"};
    static String SaveTitle = "Save Game";

    /**
     * Writes to folder the board stuff Need to add to check for the file and
     * folder exist to not have errors
     */
    public void SaveGame() {
        /**
         * Check for the file and if the folder exist
         */
//        System.out.println(Arrays.toString(paintPos.board));

        // Create the labels and text fields.
        JLabel gameDescLabel = new JLabel("Game Description: ", JLabel.RIGHT);
        JTextField gameDescField = new JTextField(getTitle());

        JPanel savePanel = new JPanel(false);
        savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.X_AXIS));

        JPanel namePanel = new JPanel(false);
        namePanel.setLayout(new GridLayout(1, 1));
        namePanel.add(gameDescLabel);

        JPanel fieldPanel = new JPanel(false);
        fieldPanel.setLayout(new GridLayout(1, 1));
        fieldPanel.add(gameDescField);

        savePanel.add(namePanel);
        savePanel.add(fieldPanel);

        if (JOptionPane.showOptionDialog(this, savePanel, SaveTitle,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, SaveOptionNames, SaveOptionNames[0]) == 0) {
            String szPos = Chess.encodePosition(Chess.pos);
            System.out.println(szPos);
            System.out.println("Saving game...");
            try {
                FileWriter fw = new FileWriter(new File("Saved\\" + SaveTitle));
                fw.write(szPos);
                fw.flush();
                fw.close();
            } catch (Exception e) {
                System.out.println("most likely ran into the error of file problems");
                System.err.print("shit");
            }
            setTitle(gameDescField.getText());
            System.out.println("Game saved.");
        }
    }

    /**
     * This function is called when the user selects to set-up the board from
     * either the menu or the toolbar. bPlaying is made false so that the AI
     * will not respond to moves. When the player activates the SetupBoard
     * function again, the bPlaying variable is re-instated and gameplay may
     * commence.
     */
    public void SetupBoard() {
        bSetPosition = !bSetPosition;
        bPlaying = !bSetPosition;
    }

    /**
     * When the player starts a new game or loads an old game, this function is
     * invoked to clear the moveList, graph and playing area of scattered
     * pieces. The traditional chess board position is also loaded.
     */
    public void NewGame() {
        lastMove = new ChessMove();
        Chess.bThinking = false;

        chess.NewGame();

        moveList.clear();
        moveTable.repaint();

        Graph.data.clear();
        graph.repaint();

        if (Chess.PROGRAM == Chess.WHITE) {
            aiCaller.go();
        }
    }

    static String tempString;

    /**
     * Main constructor. Called upon application entry. Adds a window listener
     * to the frame to listen for close messages. Initiates the program by
     * loading images, adding menu items, toolbar items and connecting to the
     * data source.
     */
    public Main() {
        thisMain = this;
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Exit();
            }
        }
        );

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        this.setResizable(false);

        LoadImages(); // Initialises Everything

        Container con = getContentPane();
        con.setLayout(null); // We don't want a layout as we'd rather arrange components ourselves

        toolPanel = new Panel(new GridLayout(1, 6));
        toolPanel.setBounds(0, 0, BOARD_WIDTH + HORZ_OFFSET + 10, 40);

        butt_NewGame = new JButton(new ImageIcon(toolbarImages[0]));
        butt_NewGame.addActionListener(this);
        toolPanel.add(butt_NewGame);

        butt_SaveGame = new JButton(new ImageIcon(toolbarImages[2]));
        butt_SaveGame.addActionListener(this);
        toolPanel.add(butt_SaveGame);

        butt_LoadGame = new JButton(new ImageIcon(toolbarImages[1]));
        butt_LoadGame.addActionListener(this);
        toolPanel.add(butt_LoadGame);

        butt_Takeback = new JButton(new ImageIcon(toolbarImages[3]));
        butt_Takeback.addActionListener(this);
        toolPanel.add(butt_Takeback);

        butt_SetupBoard = new JButton(new ImageIcon(toolbarImages[4]));
        butt_SetupBoard.addActionListener(this);
        toolPanel.add(butt_SetupBoard);

        /**
         * ADJUST THIS TO BE FLIP BOARD
         */
        butt_Flip = new JButton(new ImageIcon(toolbarImages[5]));
        butt_Flip.addActionListener(this);
        toolPanel.add(butt_Flip);

        con.add(toolPanel);

        // Init Menu Bar
        menuBar = new MenuBar();

        menu_Game = new Menu("Game");
        menuBar.add(menu_Game);

        
        // Insert a separator between New Game and Takeback
        menu_Game.addSeparator();

        menu_Game_Takeback = new MenuItem("Takeback Move");
        menu_Game.add(menu_Game_Takeback);
        menu_Game_Takeback.addActionListener(this);

        // Insert a separator between items and Exit
        menu_Game.addSeparator();

        // Init Load Game Menu
        menu_Game_Exit = new MenuItem("Exit");
        menu_Game.add(menu_Game_Exit);
        menu_Game_Exit.addActionListener(this);

        // Initialize Tabbed Pane, which contains game info
        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(420, 0, TAB_WIDTH, TAB_HEIGHT);
        tabbedPane.setDoubleBuffered(true);

        // Set up some buttons and other options for the user to press
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();//0,2,0,0);

        Panel gamePanel = new Panel(gridBag);//new GridLayout(0,1));
        gamePanel.setBackground(new Color(204, 204, 204));
        gamePanel.setBounds(0, 0, TAB_WIDTH, 400);

        JLabel label_Nodes = new JLabel("Nodes");
        JLabel label_NodesSecond = new JLabel("Nodes per second");
        JLabel label_Depth = new JLabel("Depth");
        JLabel label_Score = new JLabel("Score");
        JLabel label_Thinking = new JLabel("Thinking Lines");
        JLabel label_MoveTime = new JLabel("Move Time");

        gc.weighty = 1;

        // Add the node count label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.weightx = 1.0;
        gc.gridwidth = 1;
        gridBag.setConstraints(label_Nodes, gc);

        gamePanel.add(label_Nodes);

        // Add the node count field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 1.0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_Nodes, gc);

        gamePanel.add(field_Nodes);

        // Add the nodes per second label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridwidth = 1;
        gc.weightx = 1.0;
        gridBag.setConstraints(label_NodesSecond, gc);

        gamePanel.add(label_NodesSecond);

        // Add the nodes per second field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 1.0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_NodesSecond, gc);
        gamePanel.add(field_NodesSecond);

        // Add the depth label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridwidth = 1;
        gc.weightx = 1.0;
        gridBag.setConstraints(label_Depth, gc);

        gamePanel.add(label_Depth);

        // Add the depth field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 1.0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_Depth, gc);
        gamePanel.add(field_Depth);

        // Add the score label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridwidth = 1;
        gc.weightx = 1.0;
        gridBag.setConstraints(label_Score, gc);

        gamePanel.add(label_Score);

        // Add the score field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 1.0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_Score, gc);
        gamePanel.add(field_Score);

        // Add the thinking lines label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 3;
        gc.weightx = 2;
        gridBag.setConstraints(label_Thinking, gc);
        gamePanel.add(label_Thinking);

        // Add the thinking lines field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 0.25;
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_Thinking, gc);
        gamePanel.add(field_Thinking);

        // Add the move time label
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 3;
        gc.weightx = 2;
        gridBag.setConstraints(label_MoveTime, gc);
        gamePanel.add(label_MoveTime);

        // Add the move time field
        gc.anchor = GridBagConstraints.EAST;
        gc.weightx = 0.25;
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(field_MoveTime, gc);
        gamePanel.add(field_MoveTime);

        // Add the Visual Thinking checkbox
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(chk_VisualThinking, gc);
        gamePanel.add(chk_VisualThinking);
        chk_VisualThinking.addActionListener(this);

        // Add the Slow Redraws checkbox
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(chk_SlowRedraws, gc);
        gamePanel.add(chk_SlowRedraws);
        chk_SlowRedraws.addActionListener(this);

        // Add the Iterative Deepening checkbox
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(chk_IterativeDeep, gc);
        gamePanel.add(chk_IterativeDeep);
        chk_IterativeDeep.addActionListener(this);

        // Add the Play as White Radio button
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(radio_White, gc);
        gamePanel.add(radio_White);
        radioGroup.add(radio_White);
        radio_White.addActionListener(this);

        // Add the Play as Black Radio button
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(radio_Black, gc);
        gamePanel.add(radio_Black);
        radioGroup.add(radio_Black);
        radio_Black.addActionListener(this);

        // Add difficulty slider LABEL
        JLabel sliderLabel = new JLabel("AI Search Depth");

        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(sliderLabel, gc);

        gamePanel.add(sliderLabel);

        // Add the difficulty slider
        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(difficultySlider, gc);

        difficultySlider.setSnapToTicks(true);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintTrack(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.createStandardLabels(20);

        difficultySlider.addChangeListener(this);
        difficultySlider.repaint();

        gamePanel.add(difficultySlider);

        tabbedPane.add("Game", gamePanel);

        // Set-up and add a list of moves to hold players' moves to tabbed pane
        moveTable.getTableHeader().setReorderingAllowed(false);

        moveTable.getColumnModel().getColumn(0).setHeaderValue("#");
        moveTable.getColumnModel().getColumn(1).setHeaderValue("White");
        moveTable.getColumnModel().getColumn(2).setHeaderValue("Black");

        moveTable.getColumnModel().getColumn(0).setMaxWidth(20);

        moveTable_scrollPane.setBounds(0, 50, 200, TAB_HEIGHT);
        moveTable_scrollPane.createVerticalScrollBar();
        moveTable_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        tabbedPane.add("Move List", moveTable_scrollPane);

        // Set-up and add a graph of who's winning
        graphScroll.setViewportView(graph);

        graphScroll.createHorizontalScrollBar();
        graphScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        graphScroll.setBounds(0, 50, 200, TAB_HEIGHT);
        tabbedPane.add("Graph", graphScroll);

        tabbedPane.setSelectedIndex(2);

        // Add our tabbed pane to the content layout
        con.add(tabbedPane);

        tabbedPane.updateUI();

        chess = new Chess(this);
        aiCaller = new AICaller(chess);
        aiCaller.start();

        this.setMenuBar(menuBar);

        this.pack();

        // Resize the window to a very precise size that will fit on most displays
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Determine the user name to be displayed in the title bar
        this.setTitle(System.getProperty("user.name") + "vsComp");
        this.setVisible(true);

        repaint();
        tabbedPane.repaint();

        Thread mainThread;
        mainThread = new Thread(this);
        mainThread.start();

        timer.schedule(new TimerTask() {
            int nodesSecond = 0;

            public void run() {
                if (!Chess.bThinking || (bSlowRedraws && bVisualThinking)) {
                    bRedraw = true;
                }

                if (Chess.bThinking) {
                    ++moveTime;	// since this function is called every second, just increment the move timer

                    field_NodesSecond.setText((Chess.nodeCount - nodesSecond) + "");
                    nodesSecond = Chess.nodeCount;

                    field_Nodes.setText("" + Chess.nodeCount);
                    field_Depth.setText(Chess.maxDepth + "/" + Chess.reachedDepth);

                    tempString = "";
                    for (int i = 0; i < Chess.reachedDepth; i++) {
                        tempString += Chess.principalVariation[i].toString() + " ";
                    }

                    field_Thinking.setText(tempString);

                    field_MoveTime.setText(moveTime + " seconds");

                } else {
                    nodesSecond = 0;
                }
            }
        }, 0, 1000);

        NewGame();
    }

    /**
     * Over-rides the standard update function to enable double-buffering.
     * Images are first rendered to an off-screen buffered before posted on the
     * screen.
     *
     * @param g
     * @see offScreen
     */
    @Override
    public void update(Graphics g) {
        Graphics gr;
        // Will hold the graphics context from the offScreen.
        // We need to make sure we keep our offscreen buffer the same size
        // as the graphics context we're working with.
        if (offScreen == null
                || offScreen.getWidth(this) != this.getWidth()
                || offScreen.getHeight(this) != this.getHeight()) {
            offScreen = null;
            offScreen = this.createImage(BOARD_WIDTH + 2 * HORZ_OFFSET + 10, BOARD_HEIGHT + 2 * VERT_OFFSET + 30);
        }

        // We need to use our buffer Image as a Graphics object:
        gr = offScreen.getGraphics();

        paint(gr); // Passes our off-screen buffer to our paint method, which,
        // unsuspecting, paints on it just as it would on the Graphics
        // passed by the browser or applet viewer.
        g.drawImage(offScreen, HORZ_OFFSET, VERT_OFFSET, this);
        // And now we transfer the info in the buffer onto the
        // graphics context we got from the browser in one smooth motion.
    }

    /**
     * Draws the board and repaints the TabbedPane so it doesn't stutter.
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        drawPosition(g, BOARD_HORZ_OFFSET, BOARD_VERT_OFFSET);

        tabbedPane.repaint();
    }

    /**
     * Draws the actual chess board and the pieces on top of it. Also draws a
     * hovering piece for a picked up piece and a blue rectangle to indicate the
     * most recent move.
     */
    static Main thisMain;

    static Color colDarkSquare = new Color(70, 51, 43);
    static Color colLightSquare = new Color(255, 197, 120);
    static Color colDarkGreySquare = new Color(113, 118, 114);
    static Color colLightGreySquare = new Color(190, 198, 204);

    static Color colDarkRedSquare = new Color(107, 3, 3);
    static Color colLightRedSquare = new Color(240, 227, 212);

    static ChessPosition paintPos = Chess.pos;

    static boolean bBlueScreen = false; // is the visual thinking working

    public static void drawPosition(Graphics g, int xOffset, int yOffset) {
        // Draw the board and pieces
        boolean bIsBlack = false;

        int xPos = 0;
        int yPos = 0;

        int imgHover = 0; // image to draw for hovering piece

        Color darkColor = colDarkSquare;
        Color lightColor = colLightSquare;

        if (bSetPosition) {
            darkColor = colDarkRedSquare;
            lightColor = colLightRedSquare;
        }

        if (Chess.bThinking) {
            if (bVisualThinking) {
                bBlueScreen = true;
                darkColor = colDarkGreySquare;
                lightColor = colLightGreySquare;
                paintPos = Chess.workPos;

            } else {
                paintPos = Chess.pos;
                bBlueScreen = false;
            }
        } else {
            paintPos = Chess.pos;
            bBlueScreen = false;
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                // Draw the level tile

                xPos = TILE_WIDTH * x + xOffset;
                yPos = TILE_HEIGHT * y + yOffset;

                g.setColor(bIsBlack ? darkColor : lightColor);
                g.fillRect(xPos, yPos, TILE_WIDTH, TILE_HEIGHT);

                bIsBlack = !bIsBlack;

                // Draw Pieces
                // Determine piece value stored at board square, depending on whether the board is flipped
                int square = bFlipBoard ? (7 - y) * 10 + (7 - x) : y * 10 + x;
                int tileVal = paintPos.board[square]; //get(x,y);

                //did the last move take place on this square?
                if (bBlueScreen) // don't draw a last move if visual thinking enabled.
                {
                    if (Chess.principalVariation[1].from != 0 && Chess.principalVariation[1].to != 0) {
                        if (Chess.principalVariation[1].from == square) {
                            g.setColor(Color.green);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        } else if (Chess.principalVariation[1].to == square) {
                            g.setColor(Color.green);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        }
                    }
                    if (Chess.principalVariation[0].from != 0 && Chess.principalVariation[0].to != 0) {
                        if (Chess.principalVariation[0].from == square) {
                            g.setColor(Color.yellow);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        } else if (Chess.principalVariation[0].to == square) {
                            g.setColor(Color.yellow);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        }
                    }
                } else {
                    if (lastMove.to != 0 && lastMove.from != 0) {
                        if (lastMove.from == square) {
                            g.setColor(Color.blue);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        } else if (lastMove.to == square) {
                            g.setColor(Color.blue);
                            g.drawRect(xPos, yPos, TILE_WIDTH - 1, TILE_HEIGHT - 1);
                        }
                    }
                }

                // If the square is not blank, determine correct piece image and draw it.
                if (tileVal != ChessPosition.BLANK) {
                    int imgVal = tileVal;

                    if (imgVal < 0) {
                        imgVal = -imgVal + 6;
                    }

                    --imgVal;

                    // If this piece is the piece currently picked up, don't draw it (it is drawn later)
                    if (square + 1 == hoverPiece) // remember the + 1 for indexing
                    {
                        // Store the hovering piece's image for later drawing
                        imgHover = imgVal;
                    } else {
                        g.drawImage(images[imgVal], xPos, yPos, thisMain);
                    }
                }
            }

            bIsBlack = !bIsBlack; // Swap tile colours for correct colouring
        }

        // Draw hovering piece (player is holding it with the mouse
        if (hoverPiece > 0) {
            g.drawImage(images[imgHover],
                    mouse_x - TILE_WIDTH / 2, // x-position /  - HORZ_OFFSET
                    mouse_y - TILE_HEIGHT / 2, // y-position - VERT_OFFSET
                    thisMain);
        }

        // Now draw a stroke to the right of the board to indicate whose turn it is
        g.setColor(chess.bWhoseTurn ? Color.white : Color.black);
        g.fillRect(BOARD_WIDTH, yOffset, 10, BOARD_HEIGHT);

    }

    /**
     * The main function updates the playing area by calling repaint() when
     * bRedraw has been set and the board is "dirty". The thread sleeps for a
     * small time (25ms) and redraws the screen if necessary.
     */
    int paintCount = 0;

    @Override
    public void run() {

        while (!bQuit) {
            if (Chess.bThinking && bVisualThinking && !bSlowRedraws) {
                ++paintCount;

                if (paintCount >= 4) // every 100 ms
                {
                    paintCount = 0;
                    repaint();
                }

            } else if (bRedraw) {
                repaint();
                bRedraw = false;
            }
            try {
                Thread.currentThread().sleep(25);
            } catch (java.lang.InterruptedException e) {
                System.err.println("Error in main update thread.");
            }
        }

    }

    /**
     * Spawns a new Main class upon the application starting.
     *
     * @param args
     */
    public static void main(String[] args) {
        Main frame = new Main();
        System.out.println("Welcome to Chessmate v" + VERSION);
    }
}
