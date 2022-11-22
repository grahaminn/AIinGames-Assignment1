package games.dominion;

import core.AbstractGameState;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.interfaces.IGameMetric;
import evaluation.GameListener;
import core.interfaces.IStatisticLogger;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DominionListener extends GameListener {

    public DominionListener(IStatisticLogger logger) {
        super(logger, null);
    }

    @Override
    public void onGameEvent(CoreConstants.GameEvents type, Game game) {
        if (type == CoreConstants.GameEvents.GAME_OVER) {
            AbstractGameState state = game.getGameState();
            Map<String, Object> data = Arrays.stream(DominionGameAttributes.values())
                    .collect(Collectors.toMap(IGameMetric::name, attr -> attr.get(state, null)));
            logger.record(data);
        }
    }

    @Override
    public void onEvent(CoreConstants.GameEvents type, AbstractGameState state, AbstractAction action) {
        // nothing
    }
}
