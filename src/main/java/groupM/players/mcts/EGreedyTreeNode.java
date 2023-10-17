package groupM.players.mcts;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import core.AbstractGameState;
import core.actions.AbstractAction;

public class EGreedyTreeNode extends TreeNode{
    private double totValue;

    protected EGreedyTreeNode(MCTSPlayer player, TreeNode parent, AbstractGameState state, Random rnd) {
		super(player, parent, state, rnd);
        this.totValue = 0.0;
	}
    
    @Override
    protected AbstractAction selectAction(){
        // random
        if(player.rnd.nextFloat() < player.params.epsilon){
            List<AbstractAction> actions = children.keySet().stream().collect(Collectors.toList());
            int rndIndex = player.rnd.nextInt(actions.size());
            return actions.get(rndIndex);
        }

        // greedy
        return super.selectAction();
    }

	@Override
	double getChildValue(TreeNode child, boolean isExpanding) {
        EGreedyTreeNode eGreedyChild = (EGreedyTreeNode) child;
        boolean iAmMoving = state.getCurrentPlayer() == player.getPlayerID();
        
        return eGreedyChild.totValue * (iAmMoving ? 1 : -1); 
	}

	@Override
	void backUp(double result) {
        EGreedyTreeNode n = this;
        while (n != null) {
            n.totValue += result;
            n = (EGreedyTreeNode) n.parent;
        }
	}
}