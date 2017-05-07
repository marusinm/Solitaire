package ija.ija2016.gui;

import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represent individual games on GUI level.
 * @author Marek Marušin, xmarus08
 * @author Marián Mrva, xmrvam01
 * @version 1.0
 * @since 2017-05-06
 */
public class Game extends JInternalFrame {

    private int xPosition, yPosition;
    private int width, height, gameIndex;
    JPanel panel = new JPanel();

    Game game;
    public GameManager gameManager = new GameManager();

    /**
     * New game constructor, sets params.
     * @param width - game width
     * @param height- game height
     * @param gameIndex - number of games
     * @param desktopWidth - application width
     * @param desktopHeight - application height
     */
    public Game(int width, int height, int gameIndex, int desktopWidth, int desktopHeight) {
        super("Game number "+ gameIndex,
                true,        // resizable
                true,        // closable
                true,     // maximizable
                true);      // iconifiable

        this.game = this;
        this.width = width;
        this.height = height;
        this.gameIndex = gameIndex;

        setSize(width, height);
        // Set the window's location.
        switch (gameIndex){
            case 1:
                xPosition = 0; yPosition = 0;
                break;
            case 2:
                xPosition = desktopWidth/2; yPosition = 0;
                break;
            case 3:
                xPosition = 0; yPosition = desktopHeight/2;
                break;
            case 4:
                xPosition = desktopWidth/2; yPosition = desktopHeight/2;
                break;
        }
        setLayout(null);
        setLocation(xPosition, yPosition);
//        setBackground(Color.green);
        setJMenuBar(createMenuBar());

        panel.setBackground(Color.green);
        panel.setSize(width, height);
        panel.setLayout(null);
        add(panel);

        prepareBoard();
        distributeMixedCardsToBoard();
    }

    /**
     * Create menu bar for one game.
     * @return JMenuBar created menu
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuSave = new JMenu("Save");
        menuSave.setMnemonic(KeyEvent.VK_N);
        menuSave.addMenuListener(new SaveMenuListener());

        JMenu menuUndo = new JMenu("Undo");
        menuUndo.setMnemonic(KeyEvent.VK_N);
        menuUndo.addMenuListener(new UndoMenuListener());

        JMenu menuHint = new JMenu("Hint");
        menuHint.setMnemonic(KeyEvent.VK_N);
        menuHint.addMenuListener(new HintMenuListener());

        menuBar.add(menuSave);
        menuBar.add(menuUndo);
        menuBar.add(menuHint);

        return menuBar;
    }

    /**
     * Listeners for "save game" menu button.
     */
    class SaveMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            System.out.println("save...");
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(game);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Save to path: " + file.getAbsoluteFile().toString());
                saveGame(file.getAbsoluteFile().toString());
                JOptionPane.showMessageDialog(panel, "Game saved!");
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
        @Override
        public void menuDeselected(MenuEvent e) {
//            System.out.println("menuDeselected");
        }
        @Override
        public void menuCanceled(MenuEvent e) {
//            System.out.println("menuCanceled");
        }
    }

    /**
     * Listener for "undo" menu button.
     */
    class UndoMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            System.out.println("undo...");
        }
        @Override
        public void menuDeselected(MenuEvent e) {
//            System.out.println("menuDeselected");
        }
        @Override
        public void menuCanceled(MenuEvent e) {
//            System.out.println("menuCanceled");
        }
    }

    /**
     * Listener for "hint" menu button.
     */
    class HintMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            System.out.println("hint...");

            CardDeck[] targetPacks = {
                    gameManager.targedPack1,
                    gameManager.targedPack2,
                    gameManager.targedPack3,
                    gameManager.targedPack4,
            };

            String hintText = "";

            for (CardStack workingPack_a : gameManager.workingPacksArray){
                //test if we can add card between working packs
                for (CardStack workingPack_b : gameManager.workingPacksArray){
                    CardStack testPutStack = workingPack_b.clone();
                    testPutStack.setIsWorkingPack(true);
                    for (int i = 0; i < workingPack_a.size(); i++){
                        Card card = workingPack_a.getCard(i);
                        if(card.isTurnedFaceUp()) {
                            if (testPutStack.put(card)) {
                                hintText += "You can add card " + card + " from pack " + workingPack_a.getIdxOfWorkingPack() + " to top of working pack " + workingPack_b.getIdxOfWorkingPack() + "\n";
                            }
                        }
                    }
                }

                //test if we can add card between working packs and target packs
                int target_index = 0;
                for(CardDeck deck : targetPacks){
                    target_index++;
                    CardDeck testPutDeck = deck.clone();
//                    System.out.print("cloned: ");
//                    for (int i = 0; i < testPutDeck.size(); i++){
//                        System.out.print("/"+testPutDeck.get(i));
//                    }
//                    System.out.print("\n");

                    for (int i = 0; i < workingPack_a.size(); i++){
                        Card card = workingPack_a.getCard(i);
                        if(card.isTurnedFaceUp()) {
                            if (testPutDeck.put(card)) {
                                hintText += "You can add card " + card + " from pack " + workingPack_a.getIdxOfWorkingPack() + " to target pack " + target_index + "\n";
                            }
                        }
                    }
                }
            }

            //test add helper card to one of working packs
            for (CardStack workingPack_a : gameManager.workingPacksArray){
                CardStack testPutStack = workingPack_a.clone();
                testPutStack.setIsWorkingPack(true);
                if(gameManager.helperCard!=null) {
                    if (testPutStack.put(gameManager.helperCard)) {
                        hintText += "You can add helper card " + gameManager.helperCard + " to top of working pack " + testPutStack.getIdxOfWorkingPack() + "\n";
                    }
                }
            }

            //test add helper card to one of target packs
            int target_index = 0;
            for(CardDeck deck : targetPacks) {
                target_index++;
                CardDeck testPutDeck = deck.clone();
//                    System.out.print("cloned: ");
//                    for (int i = 0; i < testPutDeck.size(); i++){
//                        System.out.print("/"+testPutDeck.get(i));
//                    }
//                    System.out.print("\n");
                if (gameManager.helperCard != null) {
                    if (testPutDeck.put(gameManager.helperCard)) {
                        hintText += "You can add helper card " + gameManager.helperCard + " to target pack " + target_index + "\n";

                    }
                }
            }

            hintText += "... think or just click on left deck!\n";
            JOptionPane.showMessageDialog(panel, hintText);
        }
        @Override
        public void menuDeselected(MenuEvent e) {
//            System.out.println("menuDeselected");
        }
        @Override
        public void menuCanceled(MenuEvent e) {
//            System.out.println("menuCanceled");
        }
    }

    /**
     * Draw objects on board (card backgrouds) and find dynamic positions which will be setted to gameManger.
     */
    private void prepareBoard(){
        //card deck bacground
        double card_x_pos = this.width * 0.05;
        double card_y_pos = this.height * 0.05;
        gameManager.deck_positions[0] = (int) card_x_pos;
        gameManager.deck_positions[1] = (int) card_y_pos;
//        CardBackgroundView deck_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        CardBackgroundView deck_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        deck_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(deck_label,0);

        //helper card backgroud
        card_x_pos = card_x_pos + deck_label.getPreferredSize().getHeight();
        gameManager.helper_card_positions[0] = (int) card_x_pos;
        gameManager.helper_card_positions[1] = (int) card_y_pos;
        CardBackgroundView helper_card_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        helper_card_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(helper_card_label,0);

        //target 4 pack backgroud
        card_x_pos = width - (width * 0.2);
        card_y_pos = height * 0.05;
        gameManager.target_pack_4_positions[0] = (int) card_x_pos;
        gameManager.target_pack_4_positions[1] = (int) card_y_pos;
//        CardBackgroundView target_label_4 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"), gameIndex);
//        target_label_4.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.setComponentZOrder(target_label_4,0);
//        panel.add(target_label_4,0);
        gameManager.target_label_4 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        gameManager.target_label_4.setLocation((int) card_x_pos, (int) card_y_pos);
        panel.add(gameManager.target_label_4,0);

        //target 3 pack backgroud
        card_x_pos = card_x_pos - deck_label.getPreferredSize().getHeight();
        gameManager.target_pack_3_positions[0] = (int) card_x_pos;
        gameManager.target_pack_3_positions[1] = (int) card_y_pos;
//        CardBackgroundView target_label_3 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"), gameIndex);
//        target_label_3.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.setComponentZOrder(target_label_3,0);
//        panel.add(target_label_3,0);
        gameManager.target_label_3 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        gameManager.target_label_3.setLocation((int) card_x_pos, (int) card_y_pos);
        panel.add(gameManager.target_label_3,0);

        //target 2 pack backgroud
        card_x_pos = card_x_pos - deck_label.getPreferredSize().getHeight();
        gameManager.target_pack_2_positions[0] = (int) card_x_pos;
        gameManager.target_pack_2_positions[1] = (int) card_y_pos;
//        CardBackgroundView target_label_2 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"), gameIndex);
//        target_label_2.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.setComponentZOrder(target_label_2,0);
//        panel.add(target_label_2,0);
        gameManager.target_label_2 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        gameManager.target_label_2.setLocation((int) card_x_pos, (int) card_y_pos);
        panel.add(gameManager.target_label_2,0);

        //target 1 pack backgroud
        card_x_pos = card_x_pos - deck_label.getPreferredSize().getHeight();
        gameManager.target_pack_1_positions[0] = (int) card_x_pos;
        gameManager.target_pack_1_positions[1] = (int) card_y_pos;
//        CardBackgroundView target_label_1 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"), gameIndex);
//        target_label_1.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.setComponentZOrder(target_label_1,0);
//        panel.add(target_label_1,0);
        gameManager.target_label_1 = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
        gameManager.target_label_1.setLocation((int) card_x_pos, (int) card_y_pos);
        panel.add(gameManager.target_label_1,0);

        //working pack 1
        card_x_pos = this.width * 0.05;
        card_y_pos = gameManager.deck_positions[1] + deck_label.getHeight() + (deck_label.getHeight()/4);
        gameManager.working_pack_1_positions[0] = (int) card_x_pos;
        gameManager.working_pack_1_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack1_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack1_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack1_label);

        //working pack 2
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_2_positions[0] = (int) card_x_pos;
        gameManager.working_pack_2_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack2_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack2_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack2_label);

        //working pack 3
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_3_positions[0] = (int) card_x_pos;
        gameManager.working_pack_3_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack3_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack3_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        this.add(working_pack3_label);

        //working pack 4
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_4_positions[0] = (int) card_x_pos;
        gameManager.working_pack_4_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack4_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack4_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack4_label);

        //working pack 5
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_5_positions[0] = (int) card_x_pos;
        gameManager.working_pack_5_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack5_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack5_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack5_label);

        //working pack 6
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_6_positions[0] = (int) card_x_pos;
        gameManager.working_pack_6_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack6_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack6_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack6_label);

        //working pack 7
        card_x_pos = card_x_pos + working_pack1_label.getPreferredSize().getHeight();
        gameManager.working_pack_7_positions[0] = (int) card_x_pos;
        gameManager.working_pack_7_positions[1] = (int) card_y_pos;
        CardBackgroundView working_pack7_label = new CardBackgroundView(new ImageIcon("lib/card_background.gif"));
//        working_pack7_label.setLocation((int) card_x_pos, (int) card_y_pos);
//        panel.add(working_pack7_label);
    }

    /**
     * Start rendering game logic. Call class end methods which contains game logic implementation.
     */
    private void distributeMixedCardsToBoard(){
        RedrawingLogic redrawingLogic = new RedrawingLogic(panel, game);
        redrawingLogic.drawDeck(gameManager.deck);
        for (int i = 0; i < gameManager.workingPacksArray.length; i++){
            CardStack pack = gameManager.workingPacksArray[i];
            pack.get(pack.size()-1).turnFaceUp(); //turn up last card

            redrawingLogic.drawWorkingPack(pack);
        }
    }

    public void saveGame(String file_path){
        BufferedWriter out = null;
        try
        {
            FileWriter fstream = new FileWriter(file_path, false); //true tells to append data.
//            FileWriter fstream = new FileWriter("out.txt", false); //true tells to append data.
            out = new BufferedWriter(fstream);

            /*
             *   parse here state of decks&packs
             *   write to file
             */
            String shortcut;
            boolean isFacedUp;

            /*working packs*/
            for (int i = 0; i < this.gameManager.workingPacksArray.length; i++) {
                out.write("WP" + (i+1)+"\n");
                // for each card in pack
                for (int j = 0; j < this.gameManager.workingPacksArray[i].size(); j++){
                    isFacedUp = this.gameManager.workingPacksArray[i].getCard(j).isTurnedFaceUp();

                    if (isFacedUp){
                        shortcut = "t";
                    }else shortcut = "f";

                    out.write(shortcut);
                    out.write(this.gameManager.workingPacksArray[i].getCard(j).toString());
                    out.write(" ");
                }

                out.write("\n");
            }

            /*deck*/
            out.write("DECK\n");
            // store deck
            for(int i = 0; i < (this.gameManager.deck.size()); i++){
                isFacedUp = this.gameManager.deck.getCard(i).isTurnedFaceUp();

                shortcut = "f";
                if (isFacedUp)
                    shortcut = "t";

                out.write(shortcut);
                out.write(this.gameManager.deck.getCard(i).toString());
                out.write(" ");
            }
            out.write("\n");

            /*helper deck*/
            out.write("HELPERDECK\n");
            for (int i = 0; i < (this.gameManager.helperDeck.size()); i++){
                isFacedUp = this.gameManager.helperDeck.get(i).isTurnedFaceUp();

                shortcut = "f";
                if (isFacedUp)
                    shortcut = "t";

                out.write(shortcut);
                out.write(this.gameManager.helperDeck.get(i).toString());
                out.write(" ");
            }
            out.write("\n");

            /*target packs*/
            CardDeck[] targetPacks = {
                    gameManager.targedPack1,
                    gameManager.targedPack2,
                    gameManager.targedPack3,
                    gameManager.targedPack4,
            };
            int target_pack = 0;
            for (CardDeck targetPack : targetPacks){
                target_pack++;
                out.write("TARPACK"+target_pack+"\n");
                for (int i = 0; i < targetPack.size(); i++){
                    isFacedUp = targetPack.get(i).isTurnedFaceUp();

                    shortcut = "f";
                    if (isFacedUp)
                        shortcut = "t";

                    out.write(shortcut);
                    out.write(targetPack.get(i).toString());
                    out.write(" ");
                }
                out.write("\n");
            }

            /*helper card*/
            out.write("HELPERCARD\n");
            if (gameManager.helperCard != null){
                out.write("t");
                out.write(this.gameManager.helperCard.toString());
                out.write("\n");
            }
        }
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        finally
        {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}