package test.games.sirius;

import core.AbstractPlayer;
import core.Game;
import games.GameType;
import games.sirius.*;
import games.sirius.actions.MoveToMoon;
import games.sirius.actions.TakeCard;
import org.junit.Before;
import org.junit.Test;
import players.simple.RandomPlayer;

import java.util.*;

import static games.sirius.SiriusConstants.MoonType.*;
import static org.junit.Assert.*;

public class TestTurnOrder {

    Game game;
    SiriusGameState state;
    SiriusForwardModel fm = new SiriusForwardModel();
    SiriusTurnOrder sto;
    SiriusParameters params;
    List<AbstractPlayer> players = new ArrayList<>();

    @Before
    public void setup() {
        players = Arrays.asList(new RandomPlayer(),
                new RandomPlayer(),
                new RandomPlayer());
        game = GameType.Sirius.createGameInstance(3, 34, new SiriusParameters());
        game.reset(players);
        state = (SiriusGameState) game.getGameState();
        params = (SiriusParameters) state.getGameParameters();
        sto = (SiriusTurnOrder) state.getTurnOrder();
    }

    @Test
    public void testCardsOnMiningMoons() {
        int base = params.cardsPerEmptyMoon;
        int increment = params.cardsPerNonEmptyMoon;
        // N at start
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == MINING)
                assertEquals(base, moon.getDeckSize());
        }
        // at endRound we expect more to be added
        sto.endRound(state);
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == MINING)
                assertEquals(base + increment, moon.getDeckSize());
        }
    }
    @Test
    public void testCardsOnEmptyMiningMoons() {
        int base = params.cardsPerEmptyMoon;
        // N at start
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == MINING) {
                assertEquals(base, moon.getDeckSize());
                for (int i = 0; i < base; i++)
                    moon.drawCard();
            }
        }
        // at endRound we expect more to be added
        sto.endRound(state);
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == MINING)
                assertEquals(base, moon.getDeckSize());
        }
    }

    @Test
    public void testCardsOnProcessingMoons() {
        int base = params.cardsPerEmptyMoon;
        int increment = params.cardsPerNonEmptyMoon;
        // N at start
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == PROCESSING)
                assertEquals(base, moon.getDeckSize());
        }
        // at endRound we expect more to be added
        sto.endRound(state);
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == PROCESSING)
                assertEquals(base + increment, moon.getDeckSize());
        }
    }
    @Test
    public void testCardsOnEmptyProcessingMoons() {
        int base = params.cardsPerEmptyMoon;
        // N at start
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == PROCESSING) {
                assertEquals(base, moon.getDeckSize());
                for (int i = 0; i < base; i++)
                    moon.drawCard();
            }
        }
        // at endRound we expect more to be added
        sto.endRound(state);
        for (Moon moon : state.getAllMoons()) {
            if (moon.getMoonType() == PROCESSING)
                assertEquals(base, moon.getDeckSize());
        }
    }

    @Test
    public void testNextPlayer() {
        assertEquals(0, sto.getRoundCounter());
        assertEquals(1, sto.nextPlayer(state));
        fm.next(state, new MoveToMoon(1));
        assertEquals(2, sto.nextPlayer(state));
        fm.next(state, new MoveToMoon(1));
        assertEquals(0, sto.nextPlayer(state));
        assertEquals(SiriusConstants.SiriusPhase.Move, state.getGamePhase());
        fm.next(state, new MoveToMoon(1));

        assertEquals(SiriusConstants.SiriusPhase.Draw, state.getGamePhase());
        assertEquals(1, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(2, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(0, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(SiriusConstants.SiriusPhase.Move, state.getGamePhase());
        assertEquals(1, sto.getRoundCounter());

        // Round 2
        assertEquals(0, sto.getTurnOwner());
        assertEquals(1, sto.nextPlayer(state));
        fm.next(state, new MoveToMoon(0));
        assertEquals(1, sto.getTurnOwner());
        assertEquals(2, sto.nextPlayer(state));
        fm.next(state, new MoveToMoon(0));
        assertEquals(2, sto.getTurnOwner());
        assertEquals(1, sto.nextPlayer(state));
        assertEquals(SiriusConstants.SiriusPhase.Move, state.getGamePhase());
        fm.next(state, new MoveToMoon(2));

        assertEquals(SiriusConstants.SiriusPhase.Draw, state.getGamePhase());
        assertEquals(1, sto.getTurnOwner());
        assertEquals(2, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(2, sto.getTurnOwner());
        assertEquals(0, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(0, sto.getTurnOwner());
        assertEquals(0, sto.nextPlayer(state));
        fm.next(state, fm.computeAvailableActions(state).get(0));
        assertEquals(SiriusConstants.SiriusPhase.Move, state.getGamePhase());
        assertEquals(2, sto.getRoundCounter());

    }

    @Test
    public void testRankCalculation() {
        assertEquals(1, sto.getRank(0));
        assertEquals(2, sto.getRank(1));
        assertEquals(3, sto.getRank(2));
        assertEquals(0, sto.getPlayerAtRank(1));
        assertEquals(1, sto.getPlayerAtRank(2));
        assertEquals(2, sto.getPlayerAtRank(3));

        sto.endRound(state);
        assertEquals(3, sto.getRank(0));
        assertEquals(1, sto.getRank(1));
        assertEquals(2, sto.getRank(2));
        assertEquals(1, sto.getPlayerAtRank(1));
        assertEquals(2, sto.getPlayerAtRank(2));
        assertEquals(0, sto.getPlayerAtRank(3));
    }

}
