package games.dicemonastery.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.dicemonastery.DiceMonasteryGameState;
import games.dicemonastery.Monk;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static games.dicemonastery.DiceMonasteryConstants.ActionArea;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class PlaceMonk extends AbstractAction implements IExtendedSequence {

    public final ActionArea destination;
    public final int playerId;
    boolean monkChosen = false;

    public PlaceMonk(int playerId, ActionArea area) {
        this.playerId = playerId;
        destination = area;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        DiceMonasteryGameState state = (DiceMonasteryGameState) gs;
        state.setActionInProgress(this);
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState gs) {
        // we can pick any of our monks...but we can ignore duplicate values
        DiceMonasteryGameState state = (DiceMonasteryGameState) gs;
        Set<Integer> pietyValues = availableMonks(state).stream().map(Monk::getPiety).collect(toSet());
        return pietyValues.stream().map(piety -> new ChooseMonk(piety, destination)).collect(toList());
    }

    private List<Monk> availableMonks(DiceMonasteryGameState state) {
        return state.monksIn(null, playerId).stream()
                .filter(m -> m.getPiety() >= destination.dieMinimum
                        && state.getMonkLocation(m.getComponentID()) == ActionArea.DORMITORY)
                .collect(toList());
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return playerId;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {
        if (action instanceof ChooseMonk) {
            monkChosen = true;
        }
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return monkChosen;
    }

    @Override
    public PlaceMonk copy() {
        PlaceMonk retValue = new PlaceMonk(playerId, destination);
        retValue.monkChosen = monkChosen;
        return retValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceMonk))
            return false;
        PlaceMonk other = (PlaceMonk) obj;
        return other.monkChosen == monkChosen && other.destination == destination && playerId == other.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(monkChosen, destination, playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public String toString() {
        return String.format("Move monk to %s", destination);
    }
}

