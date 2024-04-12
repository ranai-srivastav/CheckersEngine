package edu.iastate.cs472.proj2;

/**
 * 
 * @author ranais
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch
{

    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves)
    {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        double maxEval = Integer.MIN_VALUE;
        int maxMoveIndex = -1;

        for(int i = 0; i < legalMoves.length; i++)
        {
            CheckersData tempBoard = this.board.clone();

            tempBoard.makeMove(legalMoves[i]);

            double moveOutcome = getMaxValue(CheckersData.BLACK, tempBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, 4);
            if(maxEval < moveOutcome)
            {
                maxMoveIndex = i;
                maxEval = moveOutcome;
            }
        }

//        this.board.makeMove(legalMoves[maxMoveIndex]);
        return legalMoves[maxMoveIndex];
    }

    /**
     * returns the utility of the current state of the board. If it is a terminal state, 1 is returned if black wins, -1 if red wins, and 0 if it is a draw.
     * Otherwise, the utility is calculated by the number of pieces each player has.
     * @param depth the recursive depth of the current node
     * @param tempBoard the board state to be evaluated
     * @return a double value representing the utility of the current game state
     */
    private double getUtility(int depth, CheckersData tempBoard)
    {
        if(depth <= 0)
        {
            double redVal = (2 * tempBoard.numRedPieces + tempBoard.numRedKings) / 12.0;
            double blackVal = (2 * tempBoard.numBlackPieces + 2 * tempBoard.numBlackKings) / 12.0;
            System.out.println("Current value:" + (blackVal - redVal));
            return blackVal - redVal;
        }

        System.out.println("Depth was " + depth);

        //this means one of the players has no moves remaining
        if(isTerminal(CheckersData.RED, tempBoard) && isTerminal(CheckersData.BLACK, tempBoard))
        {
//            System.out.printf("Utility was 0 for depth %d \n %s", depth, tempBoard.toString());
            return 0;
        }
        if(isTerminal(CheckersData.RED, tempBoard))
        {
//            System.out.printf("Utility was -1 for depth %d \n %s", depth, tempBoard.toString());
            return -1;
        }

        if(isTerminal(CheckersData.BLACK, tempBoard))
        {
//            System.out.printf("Utility was 1 for depth %d \n %s ", depth, tempBoard.toString());
            return 1;
        }

        throw new IllegalStateException("Somehow you missed every isTerminal call before utility function calls or the depth calulcation is fkd.");
    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK. For the purposes of this project, it will always be CheckersData.BLACK
     * @param alpha the alpha value used for Alpha Beta pruning
     * @param beta the ebta value used for Alpha-Beta pruning
     * @return the meximum value possible at t anode
     */
    private double getMaxValue(int player, CheckersData tempBoard, double alpha, double beta, int depth)
    {
        assert player == CheckersData.BLACK;
        if(isTerminal(player, tempBoard) || depth <= 0) return getUtility(depth, tempBoard);
        double value = Integer.MIN_VALUE;

        for(CheckersMove action: tempBoard.getLegalMoves(player))
        {
            tempBoard.makeMove(action);
            value = Math.max(value, getMinValue( (player == CheckersData.RED ? CheckersData.BLACK : CheckersData.RED), tempBoard, alpha, beta, depth-1));
            if(value >= beta) return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    /**
     *
     * @param player the player who is making the move. Must be either CheckersData.RED or CheckersData.BLACK. For the purposes of this project, it will always be CheckersData.BLACK
     * @param alpha the alpha value used for Alpha Beta pruning
     * @param beta the ebta value used for Alpha-Beta pruning
     * @return the meximum value possible at t anode
     */
    private double getMinValue(int player, CheckersData tempBoard, double alpha, double beta, int depth)
    {
        assert player == CheckersData.RED;
        if(isTerminal(player, tempBoard) || depth <= 0) return getUtility(depth, tempBoard);
        double value = Integer.MAX_VALUE;

        for(CheckersMove action: tempBoard.getLegalMoves(player))
        {
            tempBoard.makeMove(action);
            value = Math.min(value, getMaxValue((player == CheckersData.RED ? CheckersData.BLACK : CheckersData.RED), tempBoard, alpha, beta, depth-1));
            if(value <= alpha) return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    /**
     * Can any more moves be made at this state by the player who must now move?
     * @param player the player who must now move
     * @param tempBoard the board where the player has to make the move
     * @return is the game over for this player?
     */
    public static boolean isTerminal(int player, CheckersData tempBoard)
    {
        CheckersMove[] moves = tempBoard.getLegalMoves(player);
        return (moves == null || moves.length == 0);
    }
}
