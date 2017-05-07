package ija.ija2016.cardpack;

/**
 * Card representaion
 * @author Marek Marušin, xmarus08
 * @author Marián Mrva, xmrvam01
 * @version 1.0
 * @since 2017-05-06
 */
public class Card implements ija.ija2016.model.cards.Card {

    //inherite this structure from Card interface
//    public static enum Color{
//        CLUBS ("C"),
//        DIAMONDS ("D"),
//        HEARTS ("H"),
//        SPADES ("S");
//
//        private String name;
//
//        Color(String s) {
//            name = s;
//        }
//
//        public String toString() {
//            return this.name;
//        }
//
//    }
    private Color c;
    private int value;
    boolean isFacedUp = false; //defaultne je karta obratena zadom
    private boolean isEmptyCard = false;

    /**
     * Constructor, creates card representation.
     * @param c card color
     * @param value card value
     */
    public Card(Color c, int value){
        this.c = c;
        this.value = value;
    }

    /**
     * Constructor, creates empty card, without value and color (test card, or background card).
     */
    public Card(){
        //this is empty card
        isEmptyCard = true;
        turnFaceUp();
    }

    /**
     * Returns card color.
     * @return Color
     */
    public Color color(){
        return this.c;
    }

    /**
     * Set color to card.
     * @param c Color
     */
    public void setColor(Color c){
        this.c = c;
    }

    /**
     * Checks current card color and and card color from parameter, if cards are red or black.
     * @param c Card
     * @return true if cards are similar
     */
    public boolean similarColorTo(ija.ija2016.model.cards.Card c) {
        if(c.color().equals(color())){
            return true;
        }else{

            switch (color()){
                case CLUBS:
                    if (c.color().equals(Color.SPADES))
                        return true;
                    else
                        return false;
                case SPADES:
                    if(c.color().equals(Color.CLUBS))
                        return true;
                    else
                        return false;
                case DIAMONDS:
                    if (c.color().equals(Color.HEARTS))
                        return true;
                    else
                        return false;
                case HEARTS:
                    if(c.color().equals(Color.DIAMONDS))
                        return true;
                    else
                        return false;
                default:
                    return false;
            }
        }
    }

    /**
     * Compare current card and card from parameters values.
     * @param c Card
     * @return 0 if cards have equal values, positive difference otherwise
     */
    public int compareValue(ija.ija2016.model.cards.Card c) {
        if(c.value() == value)
            return 0;
        else
            return Math.abs(c.value()-value);
    }

    //TODO: u vyctovych typov by tato metoda mala byt vygenerovana automaticky, preto je mozno zbytocna
    public int value(){
        return this.value;
    }

    /**
     * Check if card is turned with value to the user.
     * @return true if card is faced up
     */
    public boolean isTurnedFaceUp() {
        return this.isFacedUp;
    }

    /**
     * Face up the card.
     * @return true if card was faced.
     */
    public boolean turnFaceUp() {
        if (isTurnedFaceUp()){
            return false;
        }else{
            this.isFacedUp = true;
            return true;
        }
    }

    /**
     * Face down the card.
     * @return true if card was faced down.
     */
    public boolean turnFaceDown() {
        if (isTurnedFaceUp()){
            this.isFacedUp = false;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        int value = value();

//        switch (value) {
//            case 1:
//                return "A("+color().toString()+")";
//            case 11:
//                return "J("+color().toString()+")";
//            case 12:
//                return "Q("+color().toString()+")";
//            case 13:
//                return "K("+color().toString()+")";
//            default:
//                return value+"("+color().toString()+")";
//        }
        if (!isEmptyCard) {
            switch (value) {
                case 1:
                    return "a" + color().toString();
                case 11:
                    return "j" + color().toString();
                case 12:
                    return "q" + color().toString();
                case 13:
                    return "k" + color().toString();
                default:
                    return value + "" + color().toString();
            }
        }else{
            return "card_background";
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Card)) {
            return false;
        }

        Card card = (Card) o;

        return card.value == value && card.color().equals(color());
    }

    @Override
    public int hashCode() {
        char character = color().toString().charAt(0);
        int ascii_char = (int) character;
        int hash = value() + ascii_char;
        return hash;
    }
}
