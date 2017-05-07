package ija.ija2016.model.cards;

/**
 * Interface represents one CardStack.
 * @author Marek Marušin, xmarus08
 * @author Marián Mrva, xmrvam01
 * @version 1.0
 * @since 2017-05-06
 */
public interface CardStack extends CardDeck {

    /**
     * Get all cards from stack from added card to end of stack.
     * @param card From this card takes next card to the end of stack.
     * @return Stack of taken cards.
     */
    ija.ija2016.cardpack.CardStack pop(ija.ija2016.cardpack.Card card);

    /**
     * Try to put CardStack to end of current stack.
     * @param stack CardStack to be added.
     * @return true if stack was added, false otherwise.
     */
    boolean put(CardStack stack);
}
