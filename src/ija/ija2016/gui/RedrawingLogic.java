package ija.ija2016.gui;

import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Implementatino of game logic.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class RedrawingLogic {

    JPanel panel;
    Game game;

    private boolean isLastDrawedCard = false;
    private boolean isDoubleClickFromHelpedCard = false;
    int packToRedraw = 2;

    private CardStack movingStack = null;
    private CardStack selectedStackSource = null;
    public CardStack helperStack = null; //always just one card here in this stack, reprezentuje je to stack len z jednou kartou ktora sa obracia z docku

    enum SourceStack{
        WORKING_PACK,
        HELPER_CARD
    }
    RedrawingLogic.SourceStack sourceStack = RedrawingLogic.SourceStack.WORKING_PACK;

    ArrayList<ArrayList<CardView>> cardViews = new ArrayList<>();
    CardView helperCardView = null;
    CardView targetCardView1 = null;
    CardView targetCardView2 = null;
    CardView targetCardView3 = null;
    CardView targetCardView4 = null;

    /**
     * Constructor.
     * @param panel panel where drawing methods will draw.
     * @param game Game instance.
     */
    public RedrawingLogic(JPanel panel, Game game){
        this.panel = panel;
        this.game = game;
        for (int i = 0 ; i < 7; i++){
            cardViews.add(null);
        }
    }

    /**
     * Method draw left top deck of cards and implements click listeners.
     * @param deck CardStack to be draw as top left deck.
     */
    public void drawDeck(CardStack deck){
        int card_x_pos = game.gameManager.deck_positions[0];
        int card_y_pos = game.gameManager.deck_positions[1];

//        Card card = deck.getCard(deck.size() - 1);
        Card card;
        if (deck.size() != 0) {
            card = deck.getCard(deck.size() - 1);
        }else{
            card = new Card();
        }
        card.turnFaceDown();

        CardView cardView = new CardView(card);
        cardView.setLocation(card_x_pos, card_y_pos);
        cardView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (deck.size()-1 == -1){
                    //try to refill deck from helperDock
                    for (int i = game.gameManager.helperDeck.size()-1; i >= 0; i--){
                        Card card1 = game.gameManager.helperDeck.get(i);
                        deck.put(card1);
                    }
                    game.gameManager.helperDeck.clear();

                    //still not filled
                    if (deck.size()-1 == -1){
                        JOptionPane.showMessageDialog(panel, "Deck is empty!");
                    }
                }

                if (deck.size()-1 > -1){
                    helperStack = null; //always reload old data
                    helperStack = deck.pop(deck.getCard(deck.size()-1));

                    game.gameManager.helperDeck.add(helperStack.getCard(helperStack.size()-1));

                    drawHelperCard();
                }
            }
        });
        panel.add(cardView,0);
        panel.repaint();
    }

    /**
     * Draw card taken from top left deck and implement click listener on this card.
     */
    public void drawHelperCard(){
        int card_x_pos = game.gameManager.helper_card_positions[0];
        int card_y_pos = game.gameManager.helper_card_positions[1];

        if (helperCardView != null){
            panel.remove(helperCardView);
            panel.updateUI();
        }

        if (helperStack.size()-1 > -1) {
            helperStack.get(helperStack.size() - 1).turnFaceUp();
            Card card = helperStack.getCard(helperStack.size() - 1);
            game.gameManager.helperCard = card;
            CardView cardView = new CardView(card);
            cardView.setLocation(card_x_pos, card_y_pos);
            cardView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() == 2) {
                        System.out.println("Double clicked on HelperCard: " + card);
                        isDoubleClickFromHelpedCard = true;

                        if(drawTargetPack(card)) {
                            System.out.println("new card added to target pack");
                            movingStack = null;
                            helperStack.pop(game.gameManager.helperDeck.get(game.gameManager.helperDeck.size() - 1));
                            game.gameManager.helperDeck.remove(game.gameManager.helperDeck.size() - 1);
                            drawHelperCard();
                        }else{
                            System.out.println("no card added to target pack on double click");
                        }

                    }else {
                        System.out.println("Clicked on HelperCard: " + card);

                        sourceStack = RedrawingLogic.SourceStack.HELPER_CARD;
                        movingStack = helperStack.pop(card);

                        //get card back into helperStack, cause it was poped in previos step
                        //we pop this card after it will be setted on right place
                        helperStack.put(game.gameManager.helperDeck.get(game.gameManager.helperDeck.size() - 1));
                    }
                }
            });
            panel.add(cardView, 1);
            panel.repaint();
            helperCardView = cardView;
        }

        //if rendering after undo clickced do not save last game
        if (!game.isUndoSelected) {
            game.saveGame(null);
        }else{
            game.isUndoSelected = false;
        }
    }

    /**
     * Draw pack to appropriate position
     * @param pack pack to draw
     */
    public void drawWorkingPack(CardStack pack) {
        int card_x_pos = 0;
        int card_y_pos = 0;
        switch (pack.getIdxOfWorkingPack()){
            case 1:
                card_x_pos = game.gameManager.working_pack_1_positions[0];
                card_y_pos = game.gameManager.working_pack_1_positions[1];
                break;
            case 2:
                card_x_pos = game.gameManager.working_pack_2_positions[0];
                card_y_pos = game.gameManager.working_pack_2_positions[1];
                break;
            case 3:
                card_x_pos = game.gameManager.working_pack_3_positions[0];
                card_y_pos = game.gameManager.working_pack_4_positions[1];
                break;
            case 4:
                card_x_pos = game.gameManager.working_pack_4_positions[0];
                card_y_pos = game.gameManager.working_pack_4_positions[1];
                break;
            case 5:
                card_x_pos = game.gameManager.working_pack_5_positions[0];
                card_y_pos = game.gameManager.working_pack_5_positions[1];
                break;
            case 6:
                card_x_pos = game.gameManager.working_pack_6_positions[0];
                card_y_pos = game.gameManager.working_pack_6_positions[1];
                break;
            case 7:
                card_x_pos = game.gameManager.working_pack_7_positions[0];
                card_y_pos = game.gameManager.working_pack_7_positions[1];
                break;
        }

        ArrayList<CardView> Views = new ArrayList<>();
        for (int i = 0; i < pack.size(); i++){
            Card card = pack.getCard(i);

            if (i == pack.size()-1){
                isLastDrawedCard = true;
            }

            CardView cardView = new CardView(card);
            cardView.setLocation(card_x_pos, card_y_pos);

            Views.add(cardView);

            if (card.isTurnedFaceUp()) {
                cardView.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (SwingUtilities.isLeftMouseButton(e)) { //left click
                            if (e.getClickCount() == 2) {
                                System.out.println("Double clicked on Card: " + card + " packNum: " + pack.getIdxOfWorkingPack());

                                //first return changes from previos one click
                                movingStack = null;

                                if(drawTargetPack(card)){
                                    System.out.println("new card added to target pack");
                                    pack.pop(card);
                                    if ((pack.size()-1) > -1)
                                        pack.get(pack.size()-1).turnFaceUp();
                                    redrawPack(pack);
                                    game.saveGame(null);
                                }else{
                                    System.out.println("no card added to target pack on double click");
                                }

                            } else {
                                System.out.println("One clicked on Card: " + card + " packNum: " + pack.getIdxOfWorkingPack());

                                sourceStack = RedrawingLogic.SourceStack.WORKING_PACK;
                                movingStack = pack.getFromCard(card);
                                selectedStackSource = pack;
                            }
                        }else if (SwingUtilities.isRightMouseButton(e)) {//right click
//                            System.out.println("right click ...");
                            if (movingStack != null) {
                                if(sourceStack == RedrawingLogic.SourceStack.WORKING_PACK) {
                                    if (pack.put(movingStack)) {
                                        selectedStackSource.pop(movingStack.getCard(0));
                                        movingStack = null;

                                        redrawPack(pack); //redraw destination working pack

                                        //turn up last card if exist
                                        if (selectedStackSource.size() - 1 > -1) {
                                            selectedStackSource.get(selectedStackSource.size() - 1).turnFaceUp();
                                        }
                                        redrawPack(selectedStackSource); //redraw source working pack

                                    } else {
                                        JOptionPane.showMessageDialog(panel, "Invalid move!\n Try agin!");
                                        movingStack = null;
                                    }

                                }else if(sourceStack == RedrawingLogic.SourceStack.HELPER_CARD) {
                                    if (pack.put(movingStack)) {
                                        movingStack = null;
                                        redrawPack(pack); //redraw destination working pack

                                        drawHelperCard();

                                        movingStack = null;
                                        helperStack.pop(game.gameManager.helperDeck.get(game.gameManager.helperDeck.size()-1));
                                        game.gameManager.helperDeck.remove(game.gameManager.helperDeck.size()-1);
                                        drawHelperCard();
                                        game.gameManager.helperCard = null;

                                    } else {
                                        JOptionPane.showMessageDialog(panel, "Invalid move!\n Try agin!");
                                        movingStack = null;
                                        game.gameManager.helperCard = null;
                                    }
                                }
                            }
                        }
                    }
                });
            }
            panel.add(cardView, pack.size() - 1 - i);
            panel.repaint();
//            game.saveGame(null);
//            panel.add(cardView, i+1);
//            panel.setComponentZOrder(cardView, i);

            double shift = cardView.getPreferredSize().getHeight() * 0.25;
            card_y_pos += (int) shift;
        }

        //theres no cards in working pack
        if (pack.size() == 0){
            Card card = new Card();

            isLastDrawedCard = true;

            CardView cardBackgroud = new CardView(card);

            Views.add(cardBackgroud);

            cardBackgroud.setLocation(card_x_pos,card_y_pos);
            cardBackgroud.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {//right click
                        System.out.println("right on empty place...");
                        if (movingStack != null) {
                            if(sourceStack == RedrawingLogic.SourceStack.WORKING_PACK) {
                                if (pack.put(movingStack)) {
                                    selectedStackSource.pop(movingStack.getCard(0));
                                    movingStack = null;

                                    redrawPack(pack); //redraw destination working pack

                                    //turn up last card if exist
                                    if (selectedStackSource.size() - 1 > -1) {
                                        selectedStackSource.get(selectedStackSource.size() - 1).turnFaceUp();
                                    }
                                    redrawPack(selectedStackSource); //redraw source working pack

                                } else {
                                    JOptionPane.showMessageDialog(panel, "Invalid move!\n Try agin!");
                                    movingStack = null;
                                }
                            }else if(sourceStack == RedrawingLogic.SourceStack.HELPER_CARD) {
                                if (pack.put(movingStack)) {
                                    movingStack = null;
                                    redrawPack(pack); //redraw destination working pack

                                    drawHelperCard();

                                    movingStack = null;
                                    helperStack.pop(game.gameManager.helperDeck.get(game.gameManager.helperDeck.size()-1));
                                    game.gameManager.helperDeck.remove(game.gameManager.helperDeck.size()-1);
                                    drawHelperCard();
                                    game.gameManager.helperCard = null;

                                } else {
                                    JOptionPane.showMessageDialog(panel, "Invalid move!\n Try agin!");
                                    movingStack = null;
                                    game.gameManager.helperCard = null;
                                }
                            }
                        }
                    }
                }
            });
            panel.add(cardBackgroud, 0);
            panel.repaint();
        }

        if (game.canSaveWorkingPackToUndoHistory) {
            if (packToRedraw == 1){
                if (isLastDrawedCard) {
                    game.saveGame(null);
                    isLastDrawedCard = false;
                    packToRedraw = 3;
                }
            }
            packToRedraw -= 1;
        }


        cardViews.set(pack.getIdxOfWorkingPack()-1,Views);
    }

    /**
     * remove all one working packs from panel
     * @param packIdx working pack index
     */
    private void removePack(int packIdx){
        for (CardView cardView : this.cardViews.get(packIdx)) {
            panel.remove(cardView);
            panel.repaint();
        }

        panel.updateUI();
//        panel.repaint();
    }

    /**
     * redraw working pack with new cards
     * @param pack pack to redraw
     */
    private void redrawPack(CardStack pack){
        removePack(pack.getIdxOfWorkingPack()-1);
//        removePack(pack.getIdxOfWorkingPack());
        drawWorkingPack(pack);
    }

    /**
     * Draw one card from top of one of target packs. Draw it on appropriate position.
     * @param card Card to render.
     * @return true if card was rendered, false otherwise.
     */
    public boolean drawTargetPack(Card card){
        boolean wasCardAdded = false;

        int card_x_pos = 0;
        int card_y_pos = 0;
        switch (card.color()){
            case CLUBS:
                card_x_pos = game.gameManager.target_pack_1_positions[0];
                card_y_pos = game.gameManager.target_pack_1_positions[1];
                wasCardAdded = game.gameManager.targedPack1.put(card);
                break;
            case DIAMONDS:
                card_x_pos = game.gameManager.target_pack_2_positions[0];
                card_y_pos = game.gameManager.target_pack_2_positions[1];
                wasCardAdded = game.gameManager.targedPack2.put(card);
                break;
            case HEARTS:
                card_x_pos = game.gameManager.target_pack_3_positions[0];
                card_y_pos = game.gameManager.target_pack_3_positions[1];
                wasCardAdded = game.gameManager.targedPack3.put(card);
                break;
            case SPADES:
                card_x_pos = game.gameManager.target_pack_4_positions[0];
                card_y_pos = game.gameManager.target_pack_4_positions[1];
                wasCardAdded = game.gameManager.targedPack4.put(card);
                break;
        }

        if (wasCardAdded) {
            CardView cardView = new CardView(card);
            cardView.setLocation(card_x_pos, card_y_pos);

            switch (card.color()){
                case CLUBS:
                    if (targetCardView1 != null) {
                        panel.remove(targetCardView1);
                        panel.updateUI();
                    }else {
                        panel.remove(game.gameManager.target_label_1);
                        panel.updateUI();
                    }
                    targetCardView1 = cardView;
                    break;
                case DIAMONDS:
                    if (targetCardView2 != null) {
                        panel.remove(targetCardView2);
                        panel.updateUI();
                    }else {
                        panel.remove(game.gameManager.target_label_2);
                        panel.updateUI();
                    }
                    targetCardView2 = cardView;
                    break;
                case HEARTS:
                    if (targetCardView3 != null) {
                        panel.remove(targetCardView3);
                        panel.updateUI();
                    }else {
                        panel.remove(game.gameManager.target_label_3);
                        panel.updateUI();
                    }
                    targetCardView3 = cardView;
                    break;
                case SPADES:
                    if (targetCardView4 != null) {
                        panel.remove(targetCardView4);
                        panel.updateUI();
                    }else {
                        panel.remove(game.gameManager.target_label_4);
                        panel.updateUI();
                    }
                    targetCardView4 = cardView;
                    break;
            }

            panel.add(cardView,0);
            panel.repaint();
        }

        return wasCardAdded;
    }

    /**
     * Draw one card from top of one of target packs. Draw it on appropriate position.
     * @param cardDeck CardDeck to render
     */
    public void drawTargetPack(CardDeck cardDeck){
        Card card = cardDeck.get(cardDeck.size()-1);

        int card_x_pos = 0;
        int card_y_pos = 0;
        switch (card.color()){
            case CLUBS:
                card_x_pos = game.gameManager.target_pack_1_positions[0];
                card_y_pos = game.gameManager.target_pack_1_positions[1];
                break;
            case DIAMONDS:
                card_x_pos = game.gameManager.target_pack_2_positions[0];
                card_y_pos = game.gameManager.target_pack_2_positions[1];
                break;
            case HEARTS:
                card_x_pos = game.gameManager.target_pack_3_positions[0];
                card_y_pos = game.gameManager.target_pack_3_positions[1];
                break;
            case SPADES:
                card_x_pos = game.gameManager.target_pack_4_positions[0];
                card_y_pos = game.gameManager.target_pack_4_positions[1];
                break;
        }

        CardView cardView = new CardView(card);
        cardView.setLocation(card_x_pos, card_y_pos);

        switch (card.color()){
            case CLUBS:
                if (targetCardView1 != null) {
                    panel.remove(targetCardView1);
                    panel.updateUI();
                }else {
                    panel.remove(game.gameManager.target_label_1);
                    panel.updateUI();
                }
                targetCardView1 = cardView;
                break;
            case DIAMONDS:
                if (targetCardView2 != null) {
                    panel.remove(targetCardView2);
                    panel.updateUI();
                }else {
                    panel.remove(game.gameManager.target_label_2);
                    panel.updateUI();
                }
                targetCardView2 = cardView;
                break;
            case HEARTS:
                if (targetCardView3 != null) {
                    panel.remove(targetCardView3);
                    panel.updateUI();
                }else {
                    panel.remove(game.gameManager.target_label_3);
                    panel.updateUI();
                }
                targetCardView3 = cardView;
                break;
            case SPADES:
                if (targetCardView4 != null) {
                    panel.remove(targetCardView4);
                    panel.updateUI();
                }else {
                    panel.remove(game.gameManager.target_label_4);
                    panel.updateUI();
                }
                targetCardView4 = cardView;
                break;
        }

        panel.add(cardView,0);
        panel.repaint();
    }

    /**
     * Remove all components of JPanel.
     */
    public void removeGui(){
        panel.removeAll();
        panel.updateUI();
        panel.repaint();
    }
}
