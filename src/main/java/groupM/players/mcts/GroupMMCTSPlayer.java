package groupM.players.mcts;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.actions.AbstractAction;
import core.interfaces.IStateHeuristic;

import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * This is a simple version of MCTS that may be useful for newcomers to TAG and MCTS-like algorithms
 * It strips out some of the additional configuration of MCTSPlayer. It uses BasicTreeNode in place of
 * SingleTreeNode.
 */
public class GroupMMCTSPlayer extends AbstractPlayer {

    Random rnd;
    GroupMMCTSParams params;
    JSONObject rootDataJson;

    public GroupMMCTSPlayer() {
        this(System.currentTimeMillis());
    }

    public GroupMMCTSPlayer(long seed) {
        this.params = new GroupMMCTSParams(seed);
        rnd = new Random(seed);
        setName(this.params.name);

        // These parameters can be changed, and will impact the Basic MCTS algorithm
        this.params.K = Math.sqrt(2);
        this.params.rolloutLength = 10;
        this.params.maxTreeDepth = 5;
        this.params.epsilon = 1e-6;

    }

    public GroupMMCTSPlayer(GroupMMCTSParams params) {
        this.params = params;
        rnd = new Random(params.getRandomSeed());
        setName(this.params.name);

        // divide budget by number of roots being run
        this.params.budget /= this.params.nRoots;

    }

    @Override
    public AbstractAction _getAction(AbstractGameState gameState, List<AbstractAction> actions) {
        // create roots and run mcts for each
        TreeNode[] roots = new TreeNode[this.params.nRoots];
        for(int i = 0; i < this.params.nRoots; i++){
            roots[i] = params.treeNodeFactory.createNode(this, null, gameState.copy(), rnd);            
            roots[i].mctsSearch();
        }

        // merge all the roots 
        TreeNode masterRoot = roots[0];
        for(TreeNode root : roots){
            if(root != masterRoot){
                masterRoot.merge(root, 1);
            }
        }
        rootDataJson = new JSONObject(masterRoot.iterData);

        return masterRoot.bestAction();
    }

    public void setStateHeuristic(IStateHeuristic heuristic) {
        this.params.heuristic = heuristic;
    }


    @Override
    public String toString() {
        return this.params.name;
    }

    @Override
    public GroupMMCTSPlayer copy() {
        return this;
    }
}