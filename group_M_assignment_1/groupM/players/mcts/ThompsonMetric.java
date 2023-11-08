package groupM.players.mcts;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.AbstractPlayer;
import core.interfaces.IGameEvent;
import evaluation.listeners.MetricsGameListener;
import evaluation.metrics.AbstractMetric;
import evaluation.metrics.Event;
import evaluation.metrics.IMetricsCollection;

public class ThompsonMetric implements IMetricsCollection{
    
    public static class ThompsonStats extends AbstractMetric{

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            AbstractPlayer player = listener.getGame().getPlayers().get(e.state.getCurrentPlayer());
            if (player instanceof GroupMMCTSPlayer) {
                GroupMMCTSPlayer mctsPlayer = (GroupMMCTSPlayer) player;
                
                records.put("PlayerName", mctsPlayer.params.name);
                records.put("PlayerID", e.state.getCurrentPlayer());
                records.put("DataJson", mctsPlayer.rootDataJson.toJSONString());
                
                return true;
            }
            return false;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return new HashSet<>(Collections.singletonList(Event.GameEvent.ACTION_CHOSEN));
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> cols = new HashMap<>();
            cols.put("PlayerName", String.class);
            cols.put("PlayerID", Integer.class);
            
            // action values for each iteration
            cols.put("DataJson", String.class);            
            return cols;
        }

    }
}
