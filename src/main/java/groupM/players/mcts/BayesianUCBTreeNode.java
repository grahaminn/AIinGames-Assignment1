package groupM.players.mcts;

import core.AbstractGameState;

import java.util.Comparator;
import java.util.Random;

import org.apache.commons.math3.distribution.BetaDistribution;

public class BayesianUCBTreeNode extends UCB1TreeNode {

    private double alpha;
    private double beta;

    protected BayesianUCBTreeNode(GroupMMCTSPlayer player, TreeNode parent, AbstractGameState state, Random rnd) {
        super(player, parent, state, rnd);
        this.alpha = 1.0;
        this.beta = 1.0;
    }

    @Override
    Comparator<TreeNode> getPruningComparator() {
        return Comparator.comparing(c->(bayesUCB(c)));
    }

    @Override
    double getChildValue(TreeNode child, boolean isExpanding) {
        BayesianUCBTreeNode bucbChild = (BayesianUCBTreeNode)child;
        boolean iAmMoving = state.getCurrentPlayer() == player.getPlayerID();

        if(!isExpanding){
            return iAmMoving ? bucbChild.mean.getResult() : -bucbChild.mean.getResult();
        }

        return iAmMoving ? bayesUCB(child) : -bayesUCB(child);
    }

    @Override
    void backUp(double result)   {
        BayesianUCBTreeNode node = this;

        while (node != null) {
            if(this.state.getOrdinalPosition(this.player.getPlayerID())==1) {
                node.alpha+=1;
            }
            else {
                node.beta+=1;
            }
            node.nVisits +=1;
            node.mean.increment(result);
            node = (BayesianUCBTreeNode) node.parent;
        }
    }

    double bayesUCB(TreeNode child) {
        return new BetaDistribution(this.alpha, this.beta).sample();
    }
}
