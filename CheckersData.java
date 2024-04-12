package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.

    int numRedPieces, numRedKings;
    int numBlackPieces, numBlackKings;


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame()
    {
        numRedPieces = 12;
        numBlackPieces = 12;
        numRedKings = 0;
        numBlackKings = 0;

        for(int row = 0; row < 8; row++)
        {
            for(int col = row % 2; col < 8; col += 2)
            {
                board[row][col] = EMPTY;

                // Put black
                if(row <= 2)
                {
                    board[row][col] = BLACK;
                }

                //we put red
                if(row >=5)
                {
                    board[row][col] = RED;
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col)
    {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move)
    {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol)
    {

    	// Update the board for the given move. You need to take care of the following situations:
            // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
            // 2. if this move is a jump, remove the captured piece
            // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king

        int piece = pieceAt(fromRow, fromCol);

        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;

        // isJump?
        if(Math.abs(fromRow - toRow) == 2)
        {
            int capturedRow = (fromRow+toRow)/2;
            int capturedCol = (fromCol+toCol)/2;
            int capturedPiece = pieceAt(capturedRow, capturedCol);

            switch (capturedPiece)
            {
                case RED:
                    numRedPieces--;
                    break;
                case RED_KING:
                    numRedKings--;
                    numRedPieces--;
                    break;
                case BLACK:
                    numBlackPieces--;
                    break;
                case BLACK_KING:
                    numBlackKings--;
                    numBlackPieces--;
                    break;
            }

            board[(fromRow+toRow)/2][(fromCol+toCol)/2] = EMPTY;
            board[toRow][toCol] = piece;
        }

        if(piece == RED && toRow == 0)
        {
            board[fromRow][fromCol] = EMPTY;
            board[toRow][toCol] = RED_KING;
            numRedKings++;
        }

        if(piece == BLACK && toRow == 7)
        {
            board[fromRow][fromCol] = EMPTY;
            board[toRow][toCol] = BLACK_KING;
            numBlackKings++;
        }
    }


    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player)
    {
        if(player != RED && player !=BLACK) return null;


        ArrayList<CheckersMove> jumpMoves = new ArrayList<>();
        for(int row = 0; row < 8; row++)
        {
            for(int col = 0; col < 8; col++)
            {
                int piece = board[row][col];
                if (player == RED)
                {
                    if(piece == RED || piece == RED_KING)
                    {
                        CheckersMove[] jumps = getLegalJumpsFrom(RED, row, col);
                        if(jumps.length == 0)
                        {
                            if (isMoveLegal(player, row, col, row - 1, col + 1) && (pieceAt(row - 1, col + 1) == EMPTY))
                                jumpMoves.add(new CheckersMove(row, col, row - 1, col + 1));

                            if (isMoveLegal(player, row, col, row - 1, col - 1) && (pieceAt(row - 1, col - 1) == EMPTY))
                                jumpMoves.add(new CheckersMove(row, col, row - 1, col - 1));

                            if (piece == RED_KING)
                            {
                                if (isMoveLegal(player, row, col, row + 1, col + 1) && pieceAt(row + 1, col + 1) == EMPTY)
                                    jumpMoves.add(new CheckersMove(row, col, row + 1, col + 1));

                                if (isMoveLegal(player, row, col, row + 1, col - 1) && pieceAt(row + 1, col - 1) == EMPTY)
                                    jumpMoves.add(new CheckersMove(row, col, row + 1, col - 1));
                            }
                        }
                        else jumpMoves.addAll(Arrays.asList(jumps));
                    }
                }
                else
                {
                    if (piece == BLACK || piece == BLACK_KING)
                    {
                        CheckersMove[] jumps = getLegalJumpsFrom(BLACK, row, col);

                        if(jumps.length == 0)
                        {
                            if (isMoveLegal(player, row, col, row + 1, col + 1) && pieceAt(row + 1, col + 1) == EMPTY)
                                jumpMoves.add(new CheckersMove(row, col, row + 1, col + 1));

                            if (isMoveLegal(player, row, col, row + 1, col - 1) && pieceAt(row + 1, col - 1) == EMPTY)
                                jumpMoves.add(new CheckersMove(row, col, row + 1, col - 1));

                            if (piece == BLACK_KING)
                            {
                                if (isMoveLegal(player, row, col, row - 1, col + 1) && pieceAt(row - 1, col + 1) == EMPTY)
                                    jumpMoves.add(new CheckersMove(row, col, row - 1, col + 1));

                                if (isMoveLegal(player, row, col, row - 1, col - 1) && pieceAt(row - 1, col - 1) == EMPTY)
                                    jumpMoves.add(new CheckersMove(row, col, row - 1, col - 1));
                            }
                        }
                        else jumpMoves.addAll(Arrays.asList(jumps));
                    }
                }
            }
        }
        return jumpMoves.size() == 0 ? null : jumpMoves.toArray(new CheckersMove[]{});
    }

    /**
     * Makes a copy of the int[8][8] board array
     * @return a 2d array copy of the board
     */
    public int[][] cloneBoard()
    {
        int[][] cloned = new int[8][8];
        for(int i = 0; i < 8; i++)
        {
            System.arraycopy(this.board[i], 0, cloned[i], 0, 8);
        }
        return cloned;
    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK
     * @param fromRow the row index of the piece to be moved
     * @param fromCol the column index of the piece to be moved
     * @param possibilities the list of possible moves. Alos used for recursion book-keeping
     * @return the list of possible moves
     */
    ArrayList<CheckersMove> canJump(int player, int fromRow, int fromCol, ArrayList<CheckersMove> possibilities)
    {
        CheckersMove possibility;

        possibility = new CheckersMove();
        possibility.addMove(fromRow, fromCol);

        int[] rowDelta;
        int[] colDelta;

        if(player == RED && (pieceAt(fromRow, fromCol) == RED))
        {
            rowDelta = new int[]{-2, -2};
            colDelta = new int[]{2, -2};
        }
        else if(player == BLACK && pieceAt(fromRow, fromCol) == BLACK)
        {
            rowDelta = new int[]{ 2,  2};
            colDelta = new int[]{ 2, -2};
        }
        else if(player == RED && pieceAt(fromRow, fromCol) == RED_KING)
        {
            rowDelta = new int[]{ 2,  2, -2, -2};
            colDelta = new int[]{ 2, -2,  2, -2};
        }
        else if(player == BLACK && pieceAt(fromRow, fromCol) == BLACK_KING)
        {
            rowDelta = new int[]{ 2,  2, -2, -2};
            colDelta = new int[]{ 2, -2,  2, -2};
        }
        else
        {
            throw new IllegalStateException("How are you neither black nor red and not one of the legal pieces");
        }

        for (int i = 0; i < rowDelta.length; i++)
        {
            int toRow = fromRow + rowDelta[i];
            int toCol = fromCol + colDelta[i];

            if(isJumpLegal(player, fromRow, fromCol, toRow, toCol))
            {
                CheckersData tempBoard = this.clone();
                CheckersMove clonedMove = possibility.clone();
                clonedMove.addMove(toRow, toCol);
                ArrayList<CheckersMove> tempList = new ArrayList<>();
                tempList.add(clonedMove);

                int origPiece = tempBoard.pieceAt(fromRow, fromCol);
                tempBoard.makeMove(fromRow, fromCol, toRow, toCol);

                if(tempBoard.pieceAt(toRow, toCol) != origPiece + 1)
                {
                    // the move does not promote anything to a king, you can continue checking for jumps
                    ArrayList<CheckersMove> recursionMoves = tempBoard.canJump(player, toRow, toCol, tempList);
                    if (recursionMoves.size() > 1)
                    {
                        for (int j = 1; j < recursionMoves.size(); j++)
                        {
                            //for each possible jump found, just add it to the previous one
                            CheckersMove possibleJump = recursionMoves.get(j);
                            CheckersMove doubleClone = clonedMove.clone();

                            int previousJumpLength = clonedMove.rows.size();
                            int currentJumpLength = possibleJump.rows.size();

                            assert clonedMove.rows.get(previousJumpLength - 1).intValue() == possibleJump.rows.get(0).intValue();

                            int k = 0;
                            while (k < 2)
                            {
                                doubleClone.addMove(possibleJump.rows.get(k), possibleJump.cols.get(k));
                                k++;
                            }

                            possibilities.add(doubleClone);
                        }
                    }
                }
                possibilities.add(clonedMove);
                possibilities.remove(possibility);  // TODO CONTROVERSIAL
            }
        }
        return possibilities;

    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK
     * @param fromRow the row index of the piece to be moved
     * @param fromCol the column index of the piece to be moved
     * @param toRow the row index of the square to move to
     * @param toCol the column index of the square to move to
     * @return boolean value representing whether the move is legal or not
     */
    private boolean isMoveLegal(int player, int fromRow, int fromCol, int toRow, int toCol)
    {
        return ((fromRow >= 0 && fromRow <= 7 &&
                 fromCol >= 0 && fromCol <= 7 &&
                 toRow >= 0 && toRow <= 7 &&
                 toCol >= 0 && toCol <= 7) &&
                 board[toRow][toCol] == EMPTY) &&
                 isMoveColorLegal(player, fromRow, fromCol, toRow, toCol);
    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK
     * @param fromRow the row index of the piece to be moved
     * @param fromCol the column index of the piece to be moved
     * @param toRow  the row index of the square to move to
     * @param toCol the column index of the square to move to
     * @return  boolean value representing whether the jump is legal or not
     */
    private boolean isJumpLegal(int player, int fromRow, int fromCol, int toRow, int toCol)
    {
        if(player != RED && player != BLACK) throw new IllegalStateException("Player is" + player + "which is different from either RED or BLACK");
        if(!isMoveLegal(player, fromRow, fromCol, toRow, toCol)) return false;

        int jumpedRow = (fromRow + toRow)/2;
        int jumpedCol = (fromCol + toCol)/2;
        int capturingPiece = pieceAt(fromRow, fromCol);
        int capturedPiece = pieceAt(jumpedRow, jumpedCol);
        boolean x = isMoveLegal(player, fromRow, fromCol, toRow, toCol) &&
                    isMoveColorLegal(player, fromRow, fromCol, toRow, toCol) &&
                    (
                        ( (capturingPiece == RED || capturingPiece == RED_KING) && (capturedPiece == BLACK || capturedPiece == BLACK_KING) ) ||
                        ( (capturingPiece == BLACK || capturingPiece == BLACK_KING) && (capturedPiece == RED || capturedPiece == RED_KING) )
                    );
        return x;
    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK
     * @param fromRow the row index of the piece to be moved
     * @param fromCol the column index of the piece to be moved
     * @param toRow the row index of the square to move to
     * @param toCol the column index of the square to move to
     * @return boolean value representing whether the move is legal for aplayer of this color
     */
    private boolean isMoveColorLegal(int player, int fromRow, int fromCol, int toRow, int toCol)
    {
        if(player != RED && player != BLACK)
            throw new IllegalStateException("Player is" + player + "which is different from either RED or BLACK");
        if(pieceAt(fromRow, fromCol) == EMPTY)
            throw new IllegalStateException("You're checking if an empty piece can move in COLOR_LEGAL");

        if(player == RED && pieceAt(fromRow, fromCol) != RED_KING)
        {
            // row numbers decrease for red, where I am going has to be smaller, to is smaller than from
            return toRow < fromRow;
        }
        else if(player == BLACK && pieceAt(fromRow, fromCol) != BLACK_KING)
        {
            // row numbers increase for black, to is larger than from
            return toRow > fromRow;
        }

        //means that it has to be a KING piece
        return true;
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps.
     * Each move returned in the array represents a sequence of jumps
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col)
    {
         return canJump(player, row, col, new ArrayList<CheckersMove>()).toArray(new CheckersMove[]{});
    }

    /**
     * Returns a deep copy of the CheckersData object it is called on
     * @return the deep copy
     */
    public CheckersData clone()
    {
        CheckersData retval = new CheckersData();
        retval.board = cloneBoard();
        retval.numBlackKings = this.numBlackKings;
        retval.numBlackPieces = this.numBlackPieces;
        retval.numRedPieces = this.numRedPieces;
        retval.numRedKings = this.numRedKings;

        return retval;
    }

    /**
     * Returns true if the two objects are equal
     * @param o the object to compare to
     * @return true if the two objects are equal
     */
    public boolean equals(Object o)
    {
        if(o instanceof CheckersData)
        {
            CheckersData cd = (CheckersData) o;
            if(cd.numBlackPieces != this.numBlackPieces) return false;
            if(cd.numBlackKings != this.numBlackKings) return false;
            if(cd.numRedPieces != this.numRedPieces) return false;
            if(cd.numRedKings != this.numRedKings) return false;

            for (int i = 0; i < 8; i++)
            {
                for(int j = 0; j < 8; j++)
                {
                    if(this.board[i][j] != cd.board[i][j])
                        return false;
                }
            }

            return true;
        }
        return false;
    }

}
