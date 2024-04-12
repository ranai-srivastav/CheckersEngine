package edu.iastate.cs472.proj2;

/**
 * 
 * @author ranais
 *
 */

import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch
{

    private static final int NUM_ITER = 10000;
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
        // which is an 2D array of the following integers defined below:
    	// 
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king

        MCTree tree = new MCTree(new MCNode(board, null, new ArrayList<MCNode>(), 0, 0, CheckersData.BLACK));
//        MCNode currNode = tree.root;
//
//        for(CheckersMove move: legalMoves)
//        {
//            CheckersData temp = this.board.clone();
//            temp.makeMove(move);
//            currNode.addChild(new MCNode(temp, currNode, new ArrayList<MCNode>(), 0, 0, CheckersData.RED));
//        }

        for (int i = 0; i < NUM_ITER; i++)
        {
            // while this current node is not an end board state and there is no child that has not been fully expanded,
            // consider the biggest UCB child as the next node, otherwise, expand this current node
            MCNode leaf = selection(tree); // Finds the first node where all children are not explored and is the best combination of exploration and expansion
            MCNode child = expansion(leaf); // Expands the node by adding all children
            int result = simulation(child);
            backPropagation(child, result);
        }
        return getBestChild(tree.root, legalMoves);
    }

    /*
        Leaf: a node with no explored children
        Terminal: a game that has ended
        Root: is start of the tree
        Fully Expanded: A node where all of the children have been added
     */

    /**
     * Given a tree, selects a node to expand based on the biggest UCB value
     * @param tree starts at at the root of the tree and ensures all children have been traversed before expanding a new node
     * @return
     */
    public MCNode selection(MCTree tree)
    {
        MCNode node = tree.root;
        while (!node.isTerminal() && node.isFullyExpanded())
        {
            node = getChildWithMaxUCB(node.exploredChildren);
        }

        return node;
    }

    /**
     * Given a list of children, return the child with the highest UCB value
     * @param children The list of children
     * @return the Node with the max UCB
     */
    public MCNode getChildWithMaxUCB(ArrayList<MCNode> children)
    {
        double maxUCB = -1;
        int maxNodeIndex = -1;

    	// calculate UCB1 for each child
        for (int i = 0; i < children.size(); i++)
        {
            MCNode child = children.get(i);
            double nodeUCB = calculateUCB(child);
            if (nodeUCB > maxUCB)
            {
                maxUCB = nodeUCB;
                maxNodeIndex = i;
            }
        }


        // select the child with the highest UCB1
        return children.get(maxNodeIndex);
    }

    /**
     * Caluclates the UCB value for a given node
     * @param node The node to calculate it for
     * @return The UCB value
     */
    private double calculateUCB(MCNode node)
    {
    	double val = ((double) node.sumOfScores / (double) node.totalNumGames) + Math.sqrt(2 * Math.log(node.parent.totalNumGames) / node.totalNumGames);
        if(Double.isNaN(val) || val == Double.NEGATIVE_INFINITY || val == Double.POSITIVE_INFINITY)
            return Double.MAX_VALUE;
        return val;
    }

    /**
     * Given a node, expand the node by adding all it's children node, assuming that they don't already exist,
     * because they obviously don't.
     * @param node children of this node will be added
     */
    public MCNode expansion(MCNode node)
    {
        if(node.isTerminal()) return node;

        ArrayList<MCNode> unvisitedChildren = new ArrayList<>();

        outerloop:
        for(CheckersMove move : node.currBoardState.getLegalMoves(node.player))
        {
            MCNode tempNode = node.clone();
            tempNode.currBoardState.makeMove(move);
            for(MCNode child: node.exploredChildren)
            {
                if(child.currBoardState.equals(tempNode.currBoardState))
                {
                    continue outerloop;
                }
            }
            unvisitedChildren.add(tempNode);
        }

        int randomNumber = new Random().nextInt(unvisitedChildren.size());
        CheckersData board = unvisitedChildren.get(randomNumber).currBoardState;
        return new MCNode(board, node, new ArrayList<>(), 0, 0, node.player == CheckersData.RED ? CheckersData.BLACK : CheckersData.RED);
    }

    /**
     * Given a node, simulate a game from this node to the end, at each step, making a uniformly-random move.
     * Return +1 if player making move wins, 0 if draw, -1 if player making move loses.
     * @param node the node to simulate from
     * @return one of +1, 0, -1, depending on whether the player making a move from the given node wins, draws, or loses
     */
    public int simulation(MCNode node)
    {
        MCNode simulatedNode = node.clone();
        Random rand = new Random();

        CheckersMove[] allPossibleMoves = node.currBoardState.getLegalMoves(node.player);

        while(allPossibleMoves != null && allPossibleMoves.length > 0)
        {
            CheckersMove move = allPossibleMoves[rand.nextInt(allPossibleMoves.length)];
            simulatedNode.currBoardState.makeMove(move);
            simulatedNode.player = (simulatedNode.player == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
            allPossibleMoves = simulatedNode.currBoardState.getLegalMoves(simulatedNode.player);
        }

        // This means that simulated node has no more moves possible
        // Thus, if the player who made the last the move was black, then black has lost, and vice versa
        return simulatedNode.player == CheckersData.BLACK ? -1 : 1;
    }

    /**
     * Starting at the "leaf" node, update all the nodes on the path to the root node with the result of the simulation
     * Add 1 to sumOfScores if the player making the move wins, add 1 to totalNumGames regardless
     * @param node the node to start back-propagation from
     * @param score the score to back-propagate. One of +1, 0, -1.
     */
    public void backPropagation(MCNode node, int score)
    {
        while(node.parent != null)
        {
            if(score == 1) // black won
            {
                node.sumOfScores++;
            }
            node.totalNumGames++;
            node = node.parent;
        }
    }


    /**
     * Given a root node, return the child with the best chances of success
     * @param root the root node
     * @param legalMoves the original list of legal moves given to the algorithm
     * @return the CheckersMove object that represents the best move
     */
    public CheckersMove getBestChild(MCNode root, CheckersMove[] legalMoves)
    {
        ArrayList<CheckersMove> bestChildren = new ArrayList<>();
        int max_playouts = Integer.MIN_VALUE;

        for (CheckersMove move : legalMoves)
        {
            MCNode child = root.clone();
            child.currBoardState.makeMove(move);
            int playouts = child.totalNumGames;
            if (playouts > max_playouts)
            {
                max_playouts = playouts;
                bestChildren = new ArrayList<>();
                bestChildren.add(move);
            } else if (playouts == max_playouts) bestChildren.add(move);
        }
        return bestChildren.get(new Random().nextInt(bestChildren.size()));
    }

}
