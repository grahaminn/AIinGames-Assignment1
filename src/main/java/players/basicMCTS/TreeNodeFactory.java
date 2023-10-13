package players.basicMCTS;

import java.util.Random;

import core.AbstractGameState;

public class TreeNodeFactory {
    BasicMCTSEnums.ExporationStrategy exporationStrategy;
    public TreeNodeFactory(BasicMCTSEnums.ExporationStrategy exporationStrategy){
        this.exporationStrategy= exporationStrategy;
    }

    public BasicTreeNode createNode(BasicMCTSPlayer player, BasicTreeNode parent, AbstractGameState state, Random rnd){
        if(exporationStrategy == BasicMCTSEnums.ExporationStrategy.UCB1){
            return new UCB1TreeNode(player, parent, state, rnd);
        }
        return new UCB1TreeNode(player, parent, state, rnd);
    }
}

