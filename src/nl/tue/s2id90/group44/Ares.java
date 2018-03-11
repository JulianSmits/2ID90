package nl.tue.s2id90.group44;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 *
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class Ares extends DraughtsPlayer {

    private int bestValue = 0;
    int maxSearchDepth;
    int currentSearchDepth = 1;
    int[] evalArray = new int[]{8, 8, 8, 8, 8, 5, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4,
        4, 5, 4, 4, 3, 3, 4, 3, 3, 2, 3, 3, 3, 2, 1, 2, 2, 2, 1, 0, 1, 2, 1, 0,
        0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
    ArrayList<Integer> leftSide = new ArrayList<>(Arrays.asList(1, 2, 6, 7, 8,
            11, 12, 16, 17, 18, 21, 22, 26, 27, 28, 31, 32, 36, 37, 38, 41, 42, 46, 47,
            48));
    int[] diagonalArray = new int[]{1, 7, 12, 18, 23, 29, 34, 40, 45, 0,
        2, 8, 13, 19, 24, 30, 35, 0,
        3, 9, 14, 20, 25, 0,
        4, 10, 15, 0,
        6, 11, 17, 22, 28, 33, 39, 44, 50, 0,
        16, 21, 27, 32, 38, 43, 49, 0,
        26, 31, 37, 42, 48, 0,
        36, 41, 47, 0,
        5, 10, 14, 19, 23, 28, 32, 37, 41, 46, 0,
        4, 9, 13, 18, 22, 27, 31, 36, 0,
        3, 8, 12, 17, 21, 26, 0,
        2, 7, 11, 16, 0,
        15, 20, 24, 29, 33, 38, 42, 47, 0,
        25, 30, 34, 39, 43, 48, 0,
        35, 40, 44, 49};

    /**
     * boolean that indicates that the GUI asked the player to stop thinking.
     */
    private boolean stopped;

    public Ares(int maxSearchDepth) {
        super("ares.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
    }

    @Override
    public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s.clone());    // the root of the search tree  

        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
        } catch (AIStoppedException ex) {
            System.err.format("not enough time ");
        }

        bestMove = node.getBestMove();

        if (bestMove == null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n",
                    this.getClass().getSimpleName(), maxSearchDepth, bestMove, bestValue
            );
            return bestMove;
        }
    }

    /**
     * This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to
     * getMove(s).
     */
    @Override
    public Integer getValue() {
        return bestValue;
    }

    /**
     * Tries to make alphabeta search stop. Search should be implemented such
     * that it throws an AIStoppedException when boolean stopped is set to true;
     *
     */
    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * returns random valid move in state s, or null if no moves exist.
     */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Implementation of alphabeta that automatically chooses the white player
     * as maximizing player and the black player as minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     *
     */
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        int returnValue = 0;
        for (int i = 1; i < depth; i++) {
            currentSearchDepth = i;
            if (node.getState().isWhiteToMove()) {
                returnValue = alphaBetaMax(node, alpha, beta, i);
            } else {
                returnValue = alphaBetaMin(node, alpha, beta, i);
            }
            bestValue = returnValue;
        }
        return returnValue;
    }

    /**
     * Does an alphabeta computation with the given alpha and beta where the
     * player that is to move in node is the minimizing player.
     *
     * <p>
     * Typical pieces of code used in this method are:
     * <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     * <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     * <li><code>node.setBestMove(bestMove);</code></li>
     * <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     * </ul>
     * </p>
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been
     * set to true.
     */
    int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        List<Move> moves = state.getMoves(); // get all the moves
        int lastValue = beta; // initialize starting values
        int value = beta;
        Move bestMove = moves.get(0);

        for (Move move : moves) { //loop over all the moves
            state.doMove(move);
            if (moves.size() != 1) {
                if (depth > 0 && !state.isEndState()) {
                    value = alphaBetaMax(node, alpha, lastValue, depth - 1);
                } else {
                    value = evaluate(state);
                }
            } else {
                if (depth > 0 && !state.isEndState()) {
                    value = alphaBetaMax(node, alpha, lastValue, depth);
                } else {
                    value = evaluate(state);
                }
            }
            if (value < lastValue) { // set better move as best move if possible
                bestMove = move;
                lastValue = value;
            }

            state.undoMove(move);
        }

        if (currentSearchDepth == depth) {
            node.setBestMove(bestMove);
            System.err.println("depth= " + depth);
        }
        return lastValue;
    }

    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) {
            stopped = false;
            throw new AIStoppedException();
        }
        DraughtsState state = node.getState();

        List<Move> moves = state.getMoves(); // get all the moves
        int lastValue = alpha; // initialize starting values
        int value = alpha;
        Move bestMove = moves.get(0);

        for (Move move : moves) { //loop over all the moves
            state.doMove(move);
            if (moves.size() != 1) {
                if (depth > 0 && !state.isEndState()) {
                    value = alphaBetaMin(node, lastValue, beta, depth - 1);
                } else {
                    value = evaluate(state);
                }
            } else {
                value = alphaBetaMin(node, lastValue, beta, depth);
            }
            if (value > lastValue) { // set better move as best move if possible
                bestMove = move;
                lastValue = value;
            }

            state.undoMove(move);
        }

        if (currentSearchDepth == depth) {
            node.setBestMove(bestMove);
            System.err.println("depth= " + depth);
        }
        return lastValue;
    }

    /**
     * A method that evaluates the given state.
     */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        int result = 0;
        int[] pieces = state.getPieces();
        int whitePieces = 0;
        int blackPieces = 0;
        int whitePosition = 0;
        int blackPosition = 0;
        int whiteBalance = 0;
        int blackBalance = 0;
        int whiteDiagonal = 0;
        int blackDiagonal = 0;

        int black = 0;
        int white = 0;
        for (int i : diagonalArray) {
            if (i == 0) {
                black = 0;
                white = 0;
            } else {
                if (pieces[i] == 1) {
                    white++;
                    black = 0;
                } else if (pieces[i] == 2) {
                    black++;
                    white = 0;
                }
                if (white == 3) {
                    whiteDiagonal++;
                    white = 0;
                } else if (black == 3) {
                    blackDiagonal++;
                    black = 0;
                }
            }
        }

        for (int i = 1; i < pieces.length; i++) {
            switch (pieces[i]) {
            case 1:
                whitePieces++;
                whitePosition += evalArray[i - 1];
                if (leftSide.contains(i)) {
                    whiteBalance++;
                } else {
                    whiteBalance--;
                }
                break;
            case 2:
                blackPieces++;
                blackPosition += evalArray[50 - i];
                if (leftSide.contains(i)) {
                    blackBalance++;
                } else {
                    blackBalance--;
                }
                break;
            case 3:
                whitePieces += 3;
                break;
            case 4:
                blackPieces += 3;
                break;
            }

        }
        int materialDiff = whitePieces - blackPieces;
        int positionDiff = whitePosition - blackPosition;
        int balanceDiff = (10 - Math.abs(whiteBalance)) - (10 - Math.abs(blackBalance));
        int diagonalDiff = whiteDiagonal - blackDiagonal;

        int balance;
        int position;
        int material;
        int diagonal;
        if (state.isWhiteToMove()) {
            if (blackPieces == 0) {
                result += 1000;
            }
        } else {
            if (whitePieces == 0) {
                result += 1000;
            }
        }
        result += (20 * materialDiff + 3 * positionDiff + 1 * balanceDiff + 10 * diagonalDiff);

        return result;
    }

}
