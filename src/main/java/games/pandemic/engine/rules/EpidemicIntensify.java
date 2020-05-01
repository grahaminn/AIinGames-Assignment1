package games.pandemic.engine.rules;

import core.AbstractGameState;
import core.actions.AddCardToDeck;
import core.components.Card;
import core.components.Deck;
import games.pandemic.PandemicGameState;

import java.util.Random;

import static games.pandemic.Constants.infectionDiscardHash;
import static games.pandemic.Constants.infectionHash;

@SuppressWarnings("unchecked")
public class EpidemicIntensify extends RuleNode {
    Random rnd;

    public EpidemicIntensify(Random rnd) {
        super();
        this.rnd = rnd;
    }

    @Override
    protected boolean run(AbstractGameState gs) {
        PandemicGameState pgs = (PandemicGameState)gs;
        Deck<Card> infectionDiscard = (Deck<Card>) pgs.getComponent(infectionDiscardHash);
        Deck<Card> infectionDeck = (Deck<Card>) pgs.getComponent(infectionHash);
        // 3. shuffle infection discard deck, add back on top of infection deck
        infectionDiscard.shuffle(rnd);
        for (Card card: infectionDiscard.getCards()) {
            new AddCardToDeck(card, infectionDeck).execute(gs);
        }
        return true;
    }
}
