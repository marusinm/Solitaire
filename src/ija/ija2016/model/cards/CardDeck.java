package ija.ija2016.model.cards;

/**
 * Interface represents one CardDeck.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public interface CardDeck {

    /**
     * Get card from top of deck.
     * @return Card object.
     */
    Card get();

    /**
     * Get card from deck on position.
     * @param index card position.
     * @return Card object.
     */
    Card get(int index);

    /**
     * Cheks if deck is empty.
     * @return true if deck is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Pop card from top of deck.
     * @return Card object.
     */
    Card pop();

    /**
     * Put card on top of deck.
     * @param card Card to be putted.
     * @return true if card was putted, false otherwise.
     */
    boolean put(Card card);

    /**
     * Get size of deck.
     * @return size of deck.
     */
    int size();
}
