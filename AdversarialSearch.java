package edu.iastate.cs472.proj2;

/**
 * 
 * @author ranais
 *
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is to be extended by the classes AlphaBetaSearch and MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch
{
    protected CheckersData board;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board)
    {
        this.board = board;
    }
    
    /** 
     * 
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves()
    {
        ArrayList<CheckersMove> legalMoves = new ArrayList<>(Arrays.asList(board.getLegalMoves(CheckersData.RED)));
    	legalMoves.addAll(Arrays.asList(board.getLegalMoves(CheckersData.BLACK)));

        return legalMoves.toArray(new CheckersMove[]{});
    }
	
    /**
     * Return a move returned from either the alpha-beta search or the Monte Carlo tree search.
     * 
     * @param legalMoves the moves that can be taken at the current state
     * @return CheckersMove, the move that is the best given the current state of the game
     */
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
