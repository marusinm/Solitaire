package ija.ija2016.gui;

import ija.ija2016.Solitaire;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;
import ija.ija2016.model.cards.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marek on 05/05/2017.
 */
public class GameManager {
    public int numberOfGames = 0;
    
    /*deck position*/
    public int[] deck_positions = new int[2];
    public int[] helper_card_positions = new int[2];

    /*target packs positions*/
    public int[] target_pack_1_positions = new int[2];
    public int[] target_pack_2_positions = new int[2];
    public int[] target_pack_3_positions = new int[2];
    public int[] target_pack_4_positions = new int[2];

    /*working packs positions*/
    public int[] working_pack_1_positions = new int[2];
    public int[] working_pack_2_positions = new int[2];
    public int[] working_pack_3_positions = new int[2];
    public int[] working_pack_4_positions = new int[2];
    public int[] working_pack_5_positions = new int[2];
    public int[] working_pack_6_positions = new int[2];
    public int[] working_pack_7_positions = new int[2];

    public Card helperCard = null;

    public CardBackgroundView target_label_1;// = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
    public CardBackgroundView target_label_2;// = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
    public CardBackgroundView target_label_3;// = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
    public CardBackgroundView target_label_4;// = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));


    public CardDeck mainCardDeck;
    public CardStack deck;
    ArrayList<ija.ija2016.cardpack.Card> helperDeck = new ArrayList<>(); // nahravaju sa tu vsetky popnute carty z docku, pre pripad cyklenia v docku

    public CardDeck targedPack1 = new CardDeck(13);
    public CardDeck targedPack2 = new CardDeck(13);
    public CardDeck targedPack3 = new CardDeck(13);
    public CardDeck targedPack4 = new CardDeck(13);

    public CardStack workingPack1,workingPack2,workingPack3,workingPack4,workingPack5,workingPack6,workingPack7;
    public CardStack[] workingPacksArray = {
            workingPack1,workingPack2,workingPack3,workingPack4,workingPack5,workingPack6,workingPack7
    };

    /**
     * Constructor, creates mixed deck and set global variables for one game
     */
    public GameManager() {
        //set params to target packs
        targedPack1.setIsTargetPack(true);
        targedPack1.setIdxOfTargetPack(1);
        targedPack1.setPackColor(Card.Color.CLUBS);

        targedPack2.setIsTargetPack(true);
        targedPack2.setIdxOfTargetPack(2);
        targedPack2.setPackColor(Card.Color.DIAMONDS);

        targedPack3.setIsTargetPack(true);
        targedPack3.setIdxOfTargetPack(3);
        targedPack3.setPackColor(Card.Color.HEARTS);

        targedPack4.setIsTargetPack(true);
        targedPack4.setIdxOfTargetPack(4);
        targedPack4.setPackColor(Card.Color.SPADES);

        //create mixed deck
        mainCardDeck = CardDeck.createMixedUpDeck();
        deck = new CardStack(24);
        for (int i = 0; i < 24; i++){
            deck.put(mainCardDeck.get(i));
        }

        //create working packs
        workingPack1 = new CardStack(52);
        workingPack1.put(mainCardDeck.get(24));
        workingPack1.setIsWorkingPack(true);
        workingPack1.setIdxOfWorkingPack(1);
        workingPacksArray[0] = workingPack1;

        workingPack2 = new CardStack(52);
        workingPack2.put(mainCardDeck.get(25));
        workingPack2.put(mainCardDeck.get(26));
        workingPack2.setIsWorkingPack(true);
        workingPack2.setIdxOfWorkingPack(2);
        workingPacksArray[1] = workingPack2;

        workingPack3 = new CardStack(52);
        workingPack3.put(mainCardDeck.get(27));
        workingPack3.put(mainCardDeck.get(28));
        workingPack3.put(mainCardDeck.get(29));
        workingPack3.setIsWorkingPack(true);
        workingPack3.setIdxOfWorkingPack(3);
        workingPacksArray[2] = workingPack3;

        workingPack4 = new CardStack(52);
        workingPack4.put(mainCardDeck.get(30));
        workingPack4.put(mainCardDeck.get(31));
        workingPack4.put(mainCardDeck.get(32));
        workingPack4.put(mainCardDeck.get(33));
        workingPack4.setIsWorkingPack(true);
        workingPack4.setIdxOfWorkingPack(4);
        workingPacksArray[3] = workingPack4;

        workingPack5 = new CardStack(52);
        workingPack5.put(mainCardDeck.get(34));
        workingPack5.put(mainCardDeck.get(35));
        workingPack5.put(mainCardDeck.get(36));
        workingPack5.put(mainCardDeck.get(37));
        workingPack5.put(mainCardDeck.get(38));
        workingPack5.setIsWorkingPack(true);
        workingPack5.setIdxOfWorkingPack(5);
        workingPacksArray[4] = workingPack5;

        workingPack6 = new CardStack(52);
        workingPack6.put(mainCardDeck.get(39));
        workingPack6.put(mainCardDeck.get(40));
        workingPack6.put(mainCardDeck.get(41));
        workingPack6.put(mainCardDeck.get(42));
        workingPack6.put(mainCardDeck.get(43));
        workingPack6.put(mainCardDeck.get(44));
        workingPack6.setIsWorkingPack(true);
        workingPack6.setIdxOfWorkingPack(6);
        workingPacksArray[5] = workingPack6;

        workingPack7 = new CardStack(52);
        workingPack7.put(mainCardDeck.get(45));
        workingPack7.put(mainCardDeck.get(46));
        workingPack7.put(mainCardDeck.get(47));
        workingPack7.put(mainCardDeck.get(48));
        workingPack7.put(mainCardDeck.get(49));
        workingPack7.put(mainCardDeck.get(50));
        workingPack7.put(mainCardDeck.get(51));
        workingPack7.setIsWorkingPack(true);
        workingPack7.setIdxOfWorkingPack(7);
        workingPacksArray[6] = workingPack7;
    }

    /**
     * resize ImageIcon (e.g. icon for card)
     * @param icon old icon
     * @return new ImageIcon
     */
    public static ImageIcon resizeIconIfNeed(ImageIcon icon){
        if (Solitaire.gameManager.numberOfGames
                != 1){
//            int width = (int)(icon.getIconWidth()/1.3);
//            int height = (int)(icon.getIconHeight()/1.3);
            int width = (int)(icon.getIconWidth()/1.6);
            int height = (int)(icon.getIconHeight()/1.6);
            icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        }
        return icon;
    }

}
