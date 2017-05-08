package ija.ija2016.model.cards;

/**
 * Interface represents one card.
 * @version 1.0
 * @since 2017-05-06
 */
public interface Card {

    /**
     * Get value.
     * @return card value.
     */
    int value();

    /**
     * Get info if card is turned up with value to the user.
     * @return true if card is faced up, false otherwise.
     */
    boolean isTurnedFaceUp();

    /**
     * Turns card up with value.
     * @return true if card was turned.
     */
    boolean turnFaceUp();

    /**
     * Get card color.
     * @return Card.Color object.
     */
    Card.Color color();

    /**
     * Checks current card color and and card color from parameter, if cards are red or black.
     * @param c Card
     * @return true if cards are similar
     */
    boolean similarColorTo(Card c);

    /**
     * Compare current card and card from parameters values.
     * @param c Card
     * @return 0 if cards have equal values, positive difference otherwise
     */
    int compareValue(Card c);

//    public static enum Color{
//        CLUBS ("C"),
//        DIAMONDS ("D"),
//        HEARTS ("H"),
//        SPADES ("S");


    public static enum Color{
        CLUBS ("c"),
        DIAMONDS ("d"),
        HEARTS ("h"),
        SPADES ("s");

        private String name;

        Color(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }
    }
}
