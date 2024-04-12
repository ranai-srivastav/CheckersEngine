package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode
{
    /**
     * Constructor for the MCNode class.
     * @param currBoardState The current board state
     * @param parent The parent MCNode
     * @param children An array list containing the visited children nodes
     * @param totalNumGames The total number of games played where that contained this state
     * @param sumOfScores The sum of all the wins
     * @param player The player whose move it is now
     */
    public MCNode(CheckersData currBoardState, MCNode parent, ArrayList<MCNode> children, int totalNumGames, double sumOfScores, int player)
    {
        this.currBoardState = currBoardState;
        this.parent = parent;
        this.exploredChildren = children;
        this.totalNumGames = totalNumGames;
        this.sumOfScores = sumOfScores;
        this.player = player;
    }

    /**
     * Current board state
     */
    CheckersData currBoardState;

    /**
     * The parent MCNode
     */
    MCNode parent;

    /**
     * The children nodes that have been visited
     */
    ArrayList<MCNode> exploredChildren = new ArrayList<MCNode>();

    /**
     * The total number of games played where that contained this state
     */
    int totalNumGames;

    /**
     * The sum of all the wins
     */
    double sumOfScores;

    /**
     * The player whose move it is now
     */
    int player;

    /**
     * Whether or not this node is terminal (can any more moves be taken by this player?)
     */
    boolean isTerminal = false;

    /**
     * Whether or not this node is a leaf (has no children)
     * @return
     */
    boolean isLeaf()
    {
        return exploredChildren.isEmpty();
    }

    /**
     * If all children have been explored at least once
     * @return a boolean value representing whether or not all children have been explored at least once
     */
    boolean isFullyExpanded()
    {
        for(MCNode child: exploredChildren)
        {
            if(child.totalNumGames == 0)
                return false;
        }

        if(exploredChildren.size() == 0) return false;

        return true;
    }

    /**
     * Add a child to the list of explored children
     * @param child the child to add to the lsit of visited Children
     */
    public void addChild(MCNode child)
    {
        exploredChildren.add(child);
    }

    /**
     * Cannot make any more moves
     * @return a boolean value representing if any more moves can be taken
     */
    public boolean isTerminal()
    {
        this.isTerminal = AlphaBetaSearch.isTerminal(this.player, this.currBoardState);
    	return isTerminal;
    }

    /**
     * A deep copy of the MCNode object
     * @return MCNode object
     */
    public MCNode clone()
    {
        return new MCNode(this.currBoardState.clone(), this.parent, (ArrayList<MCNode>)this.exploredChildren.clone(), this.totalNumGames, this.sumOfScores, this.player);
    }


}

