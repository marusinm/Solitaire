package ija.ija2016.model.board;

import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;

/**
 * AbstractFactorySolitaire.
 * @author Marek Marušin, xmarus08
 * @author Marián Mrva, xmrvam01
 * @version 1.0
 * @since 2017-05-06
 */
public abstract class AbstractFactorySolitaire{

    /**
     * Create card representation.
     * @param color card color.
     * @param value card value.
     * @return Card object.
     */
    public abstract Card createCard(Card.Color color, int value);

    /**
     * Crete deck representation
     * @return CardDeck obejct.
     */
    public abstract CardDeck createCardDeck();

    /**
     * Create target pack representation.
     * @param color color of target pack.
     * @return CardDeck object.
     */
    public abstract CardDeck createTargetPack(Card.Color color);

    /**
     * Create workting pack representation.
     * @return CardStack object.
     */
    public abstract CardStack createWorkingPack();
}
