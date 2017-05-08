package ija.ija2016.cardpack;

/**
 * CardStack representaion
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class CardStack implements ija.ija2016.model.cards.CardStack {

    private Card[] stack;
    private int stackPointer = -1;
    private int realStackSize;
    private boolean isWorkingPack = false; // jedna sa o specialny pack na hranie, na spodok moze len kral a stiredavo ukladane farby
    private int idxOfWorkingPack = 0;

    /**
     * Constructor, initialize stack on added size.
     * @param size size of stack.
     */
    public CardStack(int size){
        this.realStackSize = size;
        this.stack = new Card[size];
        for (int i = 0; i < size; i++){
            stack[i] = null;
        }
    }

    /**
     * Put one card on top of stack.
     * @param card Card to put.
     * @return true if card was putted, false otherwise.
     */
    public boolean put(ija.ija2016.model.cards.Card card){
        Card c = (Card) card;

        // pokial sa nejedna o working pack tak mozeme pridat kartu normalnym sposobonm
        if(!getIsWorkingPack()) {
            stackPointer++; // increment pointer of current stack
            if (stackPointer < stack.length) {
                stack[stackPointer] = c;
                return true;
            } else {
                return false;
            }
        }else{
            //jedna sa o working pack, na spodok moze ist len kral a potom striedavo zoradene farby

            if (size() == 0){
                //ma spodok moze ist len kral
                if (c.value() == 13){
                    stackPointer++; // increment pointer of current stack
                    if (stackPointer < stack.length) {
                        stack[stackPointer] = c;
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                //kontrola ci pridavana hodnota je prave o jedna mensia ako predosla
                //a zaroven ci sa jedna o opacnu farbu ako ma posledne vlozena karta
                if (c.value()+1 == stack[stackPointer].value() &&
                    !c.similarColorTo(stack[stackPointer])){
                    stackPointer++; // increment pointer of current stack
                    if (stackPointer < stack.length) {
                        stack[stackPointer] = c;
                        return true;
                    } else {
                        return false;
                    }
                }else {
                    return false;
                }
            }
        }
    }

    /**
     * Replace card on position.
     * @param card Card to replace
     * @param position position of replacing.
     * @return true if card was replaced.
     */
    public boolean replace(ija.ija2016.model.cards.Card card, int position) {
        Card c = (Card) card;
        stack[position] = c;
        return true;
    }


    @Override
    public boolean put(ija.ija2016.model.cards.CardStack stack){
        CardStack s = (CardStack)stack;

        if (!isWorkingPack) { //nejedna sa o specialny pack
            if (s.size() + this.size() < this.stack.length) {
                for (int i = 0; i < s.size(); i++) {
                    stackPointer++; //increment pointer of current stack
                    this.stack[stackPointer] = s.getCard(i);
                }
                return true;
            } else {
                return false;
            }
        }else{
            //jedna sa o specialny pack, vlozit mozeme len krala ak je prazdny
            //alebo o pack zacinajuci o kartu mensiu ako je posledna vlozena
            //karta ktora ma zaroven rovnaku farbu
            if (s.isEmpty()){
                return false;
            }

            //pokial je this.stack prazdny, tak pridavajuca sekvencia musi zacinat kralom
            if (this.stackPointer == -1){
                if (s.get(0).value() == 13){
                    if (s.size() + this.size() < this.stack.length) {
                        for (int i = 0; i < s.size(); i++) {
                            stackPointer++; //increment pointer of current stack
                            this.stack[stackPointer] = s.getCard(i);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    return false;
                }
            }

            //kontrola ci pridavany stack zacina o jednu mensiu kartu inej farby farby
            if (s.get(0).value() +1 == this.stack[stackPointer].value() &&
                !s.get(0).similarColorTo(this.stack[stackPointer])){

                if (s.size() + this.size() < this.stack.length) {
                    for (int i = 0; i < s.size(); i++) {
                        stackPointer++; //increment pointer of current stack
                        this.stack[stackPointer] = s.getCard(i);
                    }
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    /**
     * Checks if stack is empty.
     * @return true is stack is empty, false otherwise.
     */
    public boolean isEmpty(){
        if (stackPointer == -1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ija.ija2016.model.cards.Card get() {
        return stack[stackPointer];
    }

    @Override
    public ija.ija2016.model.cards.Card get(int index) {
        return stack[index];
    }


    @Override
    public ija.ija2016.model.cards.Card pop() {
        if (isEmpty())
            return null;
        else
            return popOneCard();
    }

    /**
     * Get CardStack size.
     * @return size.
     */
    public int size(){
        return stackPointer+1;
    }

    @Override
    public CardStack pop(Card card){
        CardStack cardStack = new CardStack(realStackSize);
        boolean isCardFounded = false;

        //cycle remove cards(sets to null) from current stack and add it to stack for sequence of removing cards
        for (int i = 0; i < this.realStackSize; i++){

            if (this.stack[i] == null){
                break;
            }

            if (this.stack[i].equals(card)) {
                isCardFounded = true;
            }

            if(isCardFounded){
                //add last card and break
                cardStack.put(stack[i]);
                this.stack[i] = null;
                this.stackPointer--; //decrement pointer of current stack
            }
        }

        if (isCardFounded){
            //return sequence of removing cards
            return cardStack;
        }else{
            return  null;
        }
    }

    /**
     * Get stack like pop() function but without popping.
     * Get all cards from stack from added card to end of stack.
     * @param card From this card takes next card to the end of stack.
     * @return Stack of taken cards.
     */
    public CardStack getFromCard(Card card){
        CardStack cardStack = new CardStack(realStackSize);
        boolean isCardFounded = false;

        //cycle remove cards(sets to null) from current stack and add it to stack for sequence of removing cards
        for (int i = 0; i < this.realStackSize; i++){

            if (this.stack[i] == null){
                break;
            }

            if (this.stack[i].equals(card)) {
                isCardFounded = true;
            }

            if(isCardFounded){
                //add last card and break
                cardStack.put(stack[i]);
            }
        }

        if (isCardFounded){
            //return sequence of removing cards
            return cardStack;
        }else{
            return  null;
        }
    }

    /**
     * Delete card from top of stack.
     * @return card which is poped
     */
    public Card popOneCard(){
        Card card = this.stack[stackPointer]; //take card from top of stack
        this.stack[stackPointer] = null;
        stackPointer--; //decrement pointer of current stack
        return card;
    }

    /**
     * Return card from position.
     * @param position position of card.
     * @return Card object.
     */
    public Card getCard(int position){
        return this.stack[position];
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < stack.length; i++){
            if (stack[i] != null) str+= stack[i].toString()+"/";
            else str+="/null";

        }
        return str;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof CardStack)) {
            return false;
        }

        CardStack cardStack = (CardStack) o;

        for (int i = size()-1; i >= 0; i--){
            //return false if any of cards do not equal
            if(!cardStack.getCard(i).equals(getCard(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = size()-1; i >= 0; i--) {
            char character = getCard(i).color().toString().charAt(1);
            int ascii_char = (int) character;
            hash += getCard(i).value() + ascii_char;
        }
        return hash;
    }

    /**
     * make deep copy of stack
     * @return copied CardStack
     */
    public CardStack clone(){
        CardStack newCardStack = new CardStack(realStackSize);
        newCardStack.setIdxOfWorkingPack(getIdxOfWorkingPack());
//        newCardStack.setIsWorkingPack(getIsWorkingPack());
        for(int i = 0; i < size(); i++){
            if (stack[i] == null){
                break;
            }
            newCardStack.put(stack[i]);
        }
        return newCardStack;
    }

    /**
     * Set stack as working pack.
     * @param isWorkingPack boolean.
     */
    public void setIsWorkingPack(boolean isWorkingPack){
        this.isWorkingPack = isWorkingPack;
    }

    /**
     * Get info if stack is working pack.
     * @return true if stack is working pack, false otherwise.
     */
    public boolean getIsWorkingPack(){
        return this.isWorkingPack;
    }

    /**
     * Set index of stack as working pack.
     * @param idxOfWorkingPack int from 1 - 7.
     */
    public void setIdxOfWorkingPack(int idxOfWorkingPack){this.idxOfWorkingPack = idxOfWorkingPack;}

    /**
     * Get index of stack as working pack.
     * @return int from 1 - 7.
     */
    public int getIdxOfWorkingPack(){return  this.idxOfWorkingPack;}
}
