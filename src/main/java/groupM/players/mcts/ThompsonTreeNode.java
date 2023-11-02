package groupM.players.mcts;

import java.util.Comparator;
import java.util.Random;

import core.AbstractGameState;
import core.actions.AbstractAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ThompsonTreeNode extends TreeNode {
    protected NormalGammaDistribution dist; 
    
    protected ThompsonTreeNode(GroupMMCTSPlayer player, TreeNode parent, AbstractGameState state, Random rnd) {
        super(player, parent, state, rnd);
        this.dist = new NormalGammaDistribution();
    }

    @Override
    Comparator<TreeNode> getPruningComparator() {
        return Comparator.comparing(c->((ThompsonTreeNode)c).dist.getMean());
    }

    @Override
    double getChildValue(TreeNode child, boolean isExpanding) {
        ThompsonTreeNode thomsonChild = (ThompsonTreeNode) child;
        boolean iAmMoving = state.getCurrentPlayer() == player.getPlayerID();

        // selecting best action -> just return mean of all results
        if(!isExpanding){
            return iAmMoving ? thomsonChild.dist.getMean() : - thomsonChild.dist.getMean();
        }
        
        // tree policy -> thompson sampling
        double value =  thompsonSample(thomsonChild);
        return iAmMoving ? value: - value;
    }

    @Override
    void backUp(double result)   {
        ThompsonTreeNode node = this;
        while (node != null) {
            node.dist.observeSample(result);
            node = (ThompsonTreeNode) node.parent;
        }
    }

    @Override
    int getNVisits() {
        return this.dist.getNVisits();
    }

    @Override
    void mergeThisNode(TreeNode node){
        ThompsonTreeNode thompsonNode = (ThompsonTreeNode) node;

        // this will calculate the mean of the two means in the distributions
        // as this is the only value checked when selecting the best action
        this.dist.merge(thompsonNode.dist);
    }

    double thompsonSample(ThompsonTreeNode child){
        return child.dist.sample(this.player.rnd);
    }

    @Override
    JSONArray getChildrenJson(){
        JSONArray childrenJson = new JSONArray();
        int actionIndex = 0;
        for (AbstractAction action : this.children.keySet()){
            ThompsonTreeNode thompsonChild = (ThompsonTreeNode) this.children.get(action);
            if(thompsonChild == null){
                continue;
            }
            JSONObject childJson = thompsonChild.dist.toJSON();
            childJson.put("actionIndex", actionIndex);
            childJson.put("action", action.toString());
            actionIndex += 1;
            childrenJson.add(childJson);
        }
        return childrenJson;
    }
}


