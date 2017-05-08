package ija.ija2016.model.board;

import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;

/**
 * FactoryKlondike.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class FactoryKlondike extends AbstractFactorySolitaire {
    @Override
    public Card createCard(ija.ija2016.model.cards.Card.Color color, int value) {
        if (value == 0){
            return null;
        }
        return new Card(color, value);
    }

    @Override
    public CardDeck createCardDeck() {
        return CardDeck.createStandardDeck();
    }

    @Override
    public CardDeck createTargetPack(ija.ija2016.model.cards.Card.Color color) {
        return CardDeck.createColorDeck(color);
    }

    @Override
    public CardStack createWorkingPack() {
        CardStack cardStack = new CardStack(52);
        cardStack.setIsWorkingPack(true);
        return cardStack;
    }
}
