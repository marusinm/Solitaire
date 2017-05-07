package ija.ija2016.cardpack;


import java.util.Random;

/**
 * Deck representaion
 * @author Marek Marušin, xmarus08
 * @author Marián Mrva, xmrvam01
 * @version 1.0
 * @since 2017-05-06
 */
public class CardDeck implements ija.ija2016.model.cards.CardDeck {

    private int deckSize;
    private CardStack cardStack;
    private boolean isTargetPack = false; //set if we can set only 13 sorted card of one color
    private Card.Color packColor = null;
    private int idxOfTargetPack = 0;

    /**
     * Constructor, initialize deck on added size.
     * @param size size of deck
     */
    public CardDeck(int size){
        deckSize = size;
        this.cardStack = new CardStack(size);
    }

    /**
     * Creates deck of 52 cards in every value and color.
     * @return created deck
     */
    public static CardDeck createStandardDeck(){
        CardDeck cardDeck = new CardDeck(52);

        Card.Color colors[] = {Card.Color.CLUBS, Card.Color.DIAMONDS, Card.Color.HEARTS, Card.Color.SPADES};
        for (int i = 0; i < 4; i++){
            for (int j = 1; j <= 13; j++){
                Card newCard = new Card(colors[i], j);
                cardDeck.put(newCard);
            }
        }
        return cardDeck;
    }

    /**
     * Created standard deck and mixed it up by swaping.
     * @return mixed CardDeck
     */
    public static CardDeck createMixedUpDeck(){
        CardDeck mixedCardDeck = createStandardDeck();

        Random randomGenerator = new Random();
        for (int i = 0; i < 250; i++){
            int randomInt1 = randomGenerator.nextInt(52);
            int randomInt2 = randomGenerator.nextInt(52);

            //swap two cards in deck
            Card cardHelper = mixedCardDeck.get(randomInt1);
            mixedCardDeck.replace(mixedCardDeck.get(randomInt2), randomInt1);
            mixedCardDeck.replace(cardHelper, randomInt2);
        }
        return  mixedCardDeck;
    }

    /**
     * Create dock of 13 cards in one color.
     * @param color deck color
     * @return created deck
     */
    public static CardDeck createColorDeck(Card.Color color){
        CardDeck cardDeck = new CardDeck(13);
        cardDeck.setIsTargetPack(true);
        cardDeck.setPackColor(color);

        return cardDeck;
    }

    @Override
    public boolean put(ija.ija2016.model.cards.Card card){
        //pretypovanie interfacu na dany objekt
        Card c = (Card)card;

        // kontrola ci sa jedna o specialny target pack, kde sa da ukladat len zoradene hodnoty jednej farby
        if (getIsTargetPack()) {
            //kontrola ci sa nahodou nejedna o 0 kartu - ta musi byt eso vo farbe packu
            if (cardStack.size() == 0 ) {
                if (c.value() == 1 && c.color() == getPackColor())
                    return cardStack.put(c);
                else
                    return false;
            }

            //kontrola ci je vkladana karta do zasobniku prave o 1 vyssia ako predosla karta
            if(cardStack.getCard(cardStack.size() - 1).value() + 1 == c.value() &&
               cardStack.getCard(cardStack.size() - 1).color() == c.color())
                return cardStack.put(c);
            else
                return false;
        }else{
            return cardStack.put(c);
        }
    }

    @Override
    public Card get() {
        if (cardStack.isEmpty())
            return null;

        return cardStack.getCard(cardStack.size()-1);
    }

    @Override
    public Card get(int index) {
        if (cardStack.isEmpty())
            return null;

        return cardStack.getCard(index);
    }

    @Override
    public boolean isEmpty() {
        if (cardStack.isEmpty())
            return true;

        return false;
    }

    /**
     * Make deep copy of deck.
     * @return copied CardDeck
     */
    public CardDeck clone(){
        CardDeck newCardDeck = new CardDeck(deckSize);
        newCardDeck.setIsTargetPack(getIsTargetPack());
        newCardDeck.setPackColor(getPackColor());
        for(int i = 0; i < cardStack.size(); i++){
            newCardDeck.put(cardStack.getCard(i));
        }
        return newCardDeck;
    }

    /**
     * Replace card in CardDeck.
     * @param card card to replace
     * @param position position of replacing
     */
    public void replace(ija.ija2016.model.cards.Card card, int position){
        Card c = (Card)card;
        cardStack.replace(c, position);
    }

    /**
     * Pop one card from deck.
     * @return popped card.
     */
    public Card pop(){
        return cardStack.popOneCard();
    }

    /**
     * Get deck size
     * @return size.
     */
    public int size(){
        return  cardStack.size();
    }

    /**
     * Set deck as target pack .
     * @param isTargetPack boolean.
     */
    public void setIsTargetPack(boolean isTargetPack){
        this.isTargetPack = isTargetPack;
    }

    /**
     * Get info if deck is target pack.
     * @return true if deck is target pack, false otherwise.
     */
    public boolean getIsTargetPack(){
        return this.isTargetPack;
    }

    /**
     * Set color to deck.
     * @param color Card.Color
     */
    public void setPackColor(Card.Color color){
        this.packColor = color;
    }

    /**
     * Get deck color.
     * @return Card.Color.
     */
    public Card.Color getPackColor(){
        return this.packColor;
    }

    /**
     * Set index of deck as target pack.
     * @param idxOfWorkingPack int from 1 - 4.
     */
    public void setIdxOfTargetPack(int idxOfWorkingPack){this.idxOfTargetPack = idxOfTargetPack;}

    /**
     * Get index of deck as target pack.
     * @return int from 1 - 4
     */
    public int getIdxOfTargetPack(){return  this.idxOfTargetPack;}
}
