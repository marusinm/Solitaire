package ija.ija2016.gui;

import ija.ija2016.Solitaire;
import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

/**
 * Represent individual games on GUI level.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class Game extends JInternalFrame {

    private int xPosition, yPosition;
    private int width, height, gameIndex;
    JPanel panel = new JPanel();

    Game game;
    public GameManager gameManager;
    private RedrawingLogic redrawingLogic;
    public ArrayList<String> undoHistory = new ArrayList<>();
    public boolean isUndoSelected = false;
    public boolean canSaveWorkingPackToUndoHistory = true;

    /**
     * New game constructor, sets params.
     * @param width - game width
     * @param height- game height
     * @param gameIndex - number of games
     * @param desktopWidth - application width
     * @param desktopHeight - application height
     */
    public Game(int width, int height, int gameIndex, int desktopWidth, int desktopHeight, GameManager gameManager) {
        super("Game number "+ gameIndex,
                true,        // resizable
                true,        // closable
                true,     // maximizable
                true);      // iconifiable

        this.game = this;
        this.width = width;
        this.height = height;
        this.gameIndex = gameIndex;
//        this.gameIndex = Solitaire.numberOfGames;

        if (gameManager == null) {
            this.gameManager = new GameManager();
        }else{
            this.gameManager = gameManager;
        }

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

        //override close button
        addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                super.internalFrameClosing(e);
                Solitaire.numberOfGames--;
                System.out.println("end game: "+Solitaire.numberOfGames);
                if (Solitaire.numberOfGames == 1){
//                    Solitaire.solitaire.createNewGameFrame(gameManager);

                    GameManager gameManagerAdapter = Solitaire.solitaire.game1.gameManager;
                    try {
                        Solitaire.solitaire.game1.setClosed(true); //close first winwod
                    } catch (PropertyVetoException e1) {
                        e1.printStackTrace();
                    }
                    Solitaire.solitaire.createNewGameFrame(gameManagerAdapter); //redraw firs window to second
                }
            }
        });

//        panel.setBackground(Color.green);
        panel.setBackground(new Color(1, 128, 1));
        panel.setSize(width, height);
        panel.setLayout(null);
        add(panel);

        prepareBoard();
        distributeMixedCardsToBoard();
        game.saveGame(null);
    }

    /**
     * Create menu bar for one game.
     * @return JMenuBar created menu
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JButton buttonSave= new JButton("Save");
        buttonSave.setOpaque(true);
        buttonSave.setContentAreaFilled(false);
        buttonSave.setBorderPainted(false);
        buttonSave.setFocusable(false);
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("save...");
                final JFileChooser fc = new JFileChooser("examples/");
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
        });

        JButton buttonUndo= new JButton("Undo");
        buttonUndo.setOpaque(true);
        buttonUndo.setContentAreaFilled(false);
        buttonUndo.setBorderPainted(false);
        buttonUndo.setFocusable(false);
        buttonUndo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("undo...");
                gameManager = loadGame();
                if (undoHistory.size() < 1){
                    JOptionPane.showMessageDialog(panel, "No more \"undo\" history created!");
                }

                if (gameManager != null) {
                    redrawingLogic.removeGui();
                    prepareBoard();
                    distributeMixedCardsToBoard();
                }
            }
        });

        JButton buttonHint= new JButton("Hint");
        buttonHint.setOpaque(true);
        buttonHint.setContentAreaFilled(false);
        buttonHint.setBorderPainted(false);
        buttonHint.setFocusable(false);
        buttonHint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
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
        });


        menuBar.add(buttonSave);
        menuBar.add(buttonUndo);
        menuBar.add(buttonHint);

        return menuBar;
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
        canSaveWorkingPackToUndoHistory = false;

        redrawingLogic = new RedrawingLogic(panel, game);
        redrawingLogic.drawDeck(gameManager.deck);
        for (int i = 0; i < gameManager.workingPacksArray.length; i++){
            CardStack pack = gameManager.workingPacksArray[i];
            if ((pack.size()-1) >= 0)
                pack.get(pack.size()-1).turnFaceUp(); //turn up last card

            redrawingLogic.drawWorkingPack(pack);
        }

        //maybe game is loaded from file or undo, there can be other objects to draw
        if (gameManager.helperCard != null){
            redrawingLogic.helperStack = new CardStack(1);
            redrawingLogic.helperStack.put(gameManager.helperCard);
            redrawingLogic.drawHelperCard();
        }
        CardDeck[] targetPacks = {
                gameManager.targedPack1,
                gameManager.targedPack2,
                gameManager.targedPack3,
                gameManager.targedPack4,
        };

        for (CardDeck targetPack : targetPacks) {
            if (targetPack.size() > 0) {
//                redrawingLogic.drawTargetPack(targetPack.get(targetPack.size() - 1));
                redrawingLogic.drawTargetPack(targetPack);
            }
        }

        canSaveWorkingPackToUndoHistory = true;
    }

    /**
     * Save data from GameManager instance in text file.
     * @param file_path path where to save data. If param is NULL save to helper array for undo function.
     */
    public void saveGame(String file_path){
        String final_output =  "";

        String shortcut;
        boolean isFacedUp;

            /*working packs*/
        for (int i = 0; i < this.gameManager.workingPacksArray.length; i++) {
            final_output +=  "WP" + (i+1)+" ";
            // for each card in pack
            for (int j = 0; j < this.gameManager.workingPacksArray[i].size(); j++){
                isFacedUp = this.gameManager.workingPacksArray[i].getCard(j).isTurnedFaceUp();

                if (isFacedUp){
                    shortcut = "t";
                }else shortcut = "f";

                final_output +=  shortcut;
                final_output +=  this.gameManager.workingPacksArray[i].getCard(j).toString();
                final_output +=  " ";
            }

            final_output +=  "\n";
        }

            /*deck*/
        final_output +=  "DECK ";
        // store deck
        for(int i = 0; i < (this.gameManager.deck.size()); i++){
            isFacedUp = this.gameManager.deck.getCard(i).isTurnedFaceUp();

            shortcut = "f";
            if (isFacedUp)
                shortcut = "t";

            final_output +=  shortcut;
            final_output +=  this.gameManager.deck.getCard(i).toString();
            final_output +=  " ";
        }
        final_output +=  "\n";

            /*helper deck*/
        final_output +=  "HELPERDECK ";
        for (int i = 0; i < (this.gameManager.helperDeck.size()); i++){
            isFacedUp = this.gameManager.helperDeck.get(i).isTurnedFaceUp();

            shortcut = "f";
            if (isFacedUp)
                shortcut = "t";

            final_output +=  shortcut;
            final_output +=  this.gameManager.helperDeck.get(i).toString();
            final_output +=  " ";
        }
        final_output +=  "\n";

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
            final_output +=  "TARPACK"+target_pack+" ";
            for (int i = 0; i < targetPack.size(); i++){
//                System.out.println("Target pack idx: "+(target_pack)+ " card"+i+": "+targetPack.get(i));
                isFacedUp = targetPack.get(i).isTurnedFaceUp();

                shortcut = "f";
                if (isFacedUp)
                    shortcut = "t";

                final_output +=  shortcut;
                final_output +=  targetPack.get(i).toString();
                final_output +=  " ";
            }
            final_output +=  "\n";
        }

            /*helper card*/
        final_output +=  "HELPERCARD ";
        if (gameManager.helperCard != null){
            final_output +=  "t";
            final_output +=  this.gameManager.helperCard.toString();
            final_output +=  "\n";
        }

        if (file_path != null) { //save to file
            BufferedWriter out = null;
            try {
            FileWriter fstream = new FileWriter(file_path, false); //true tells to append data.
//                FileWriter fstream = new FileWriter("out.txt", false); //true tells to append data.
                out = new BufferedWriter(fstream);
                out.write(final_output);

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            boolean isEqualAsPrevios = false;
            if (undoHistory.size() > 0){
                if(undoHistory.get(undoHistory.size()-1).equals(final_output)){
                    isEqualAsPrevios = true;
                }
            }

            if (!isEqualAsPrevios) {
                if (undoHistory.size() < 5) {
                    undoHistory.add(final_output);
                } else {
                    //rotate items to left and save new item on last position
                    for (int i = 0; i < 4; i++) {
                        undoHistory.set(i, undoHistory.get(i + 1));
                    }
                    undoHistory.set(4, final_output);
                }
//                int i = 0;
//                for (String s : undoHistory) {
//                    i++;
//                    System.out.println(i + "/////////////////////////////////////////////////////////" + i);
//                    System.out.println(s);
//                }
            }
        }

        //check game end
        if (game.gameManager.targedPack4.size() == 13 &&
                game.gameManager.targedPack3.size() == 13 &&
                game.gameManager.targedPack2.size() == 13 &&
                game.gameManager.targedPack1.size() == 13) {
            if (game.gameManager.targedPack4.get(12).equals(new Card(Card.Color.SPADES, 13)) &&
                    game.gameManager.targedPack3.get(12).equals(new Card(Card.Color.HEARTS, 13)) &&
                    game.gameManager.targedPack2.get(12).equals(new Card(Card.Color.DIAMONDS, 13)) &&
                    game.gameManager.targedPack1.get(12).equals(new Card(Card.Color.CLUBS, 13))) {

                JOptionPane.showMessageDialog(panel, "You WIN!");
            }
        }
    }


    /**
     * Load games from undoHistory ArrayList
     */
    public GameManager loadGame() {
        isUndoSelected = true;
        if (undoHistory.size() > 1) {
            //create new GameManager, clear/pop all data from it and load new data from saved file
            GameManager gameManager = new GameManager();
            gameManager.deck.pop(gameManager.deck.getCard(0)); //pop all cards
            gameManager.helperDeck = new ArrayList<>();
            gameManager.helperCard = null;
            gameManager.targedPack1 = new CardDeck(13);
            gameManager.targedPack2 = new CardDeck(13);
            gameManager.targedPack3 = new CardDeck(13);
            gameManager.targedPack4 = new CardDeck(13);
            for (CardStack workingPack : gameManager.workingPacksArray) {
                workingPack.pop(workingPack.getCard(0)); //pop all cards
                workingPack.setIsWorkingPack(false);
            }

            undoHistory.remove(undoHistory.size() - 1);
            String undo = undoHistory.get(undoHistory.size() - 1);
//            int j = 0;
//            for (String s : undoHistory){
//                j++;
//                System.out.println(j+"/////////////////undo history///////////////////////////"+j);
//                System.out.println(s);
//            }

            String[] lines = undo.split("\n");
            for (String line : lines) {
                String[] splitArray = null;
                try {
                    splitArray = line.split("\\s+");
                } catch (PatternSyntaxException ex) {
                    System.out.println(ex);
                }

                if (splitArray[0].equals("WP1")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack1.put(card);
                    }
                    gameManager.workingPack1.setIsWorkingPack(true);
                    System.out.println("working pack1: " + gameManager.workingPack1);

                } else if (splitArray[0].equals("WP2")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack2.put(card);
                    }
                    gameManager.workingPack2.setIsWorkingPack(true);
                    System.out.println("working pack2: " + gameManager.workingPack2);

                } else if (splitArray[0].equals("WP3")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack3.put(card);
                    }
                    gameManager.workingPack3.setIsWorkingPack(true);
                    System.out.println("working pack3: " + gameManager.workingPack3);

                } else if (splitArray[0].equals("WP4")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack4.put(card);
                    }
                    gameManager.workingPack4.setIsWorkingPack(true);
                    System.out.println("working pack4: " + gameManager.workingPack4);

                } else if (splitArray[0].equals("WP5")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack5.put(card);
                    }
                    gameManager.workingPack5.setIsWorkingPack(true);
                    System.out.println("working pack5: " + gameManager.workingPack5);

                } else if (splitArray[0].equals("WP6")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack6.put(card);
                    }
                    gameManager.workingPack6.setIsWorkingPack(true);
                    System.out.println("working pack6: " + gameManager.workingPack6);

                } else if (splitArray[0].equals("WP7")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.workingPack7.put(card);
                    }
                    gameManager.workingPack7.setIsWorkingPack(true);
                    System.out.println("working pack7: " + gameManager.workingPack7);

                } else if (splitArray[0].equals("DECK")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.deck.put(card);
                    }
                    System.out.println("deck: " + gameManager.deck);

                } else if (splitArray[0].equals("HELPERDECK")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.helperDeck.add(card);
                    }
                    System.out.println("HELPERDECK: " + gameManager.helperDeck);

                } else if (splitArray[0].equals("TARPACK1")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.targedPack1.put(card);
                    }
                    System.out.println("TARPACK1: " + gameManager.targedPack1);

                } else if (splitArray[0].equals("TARPACK2")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.targedPack2.put(card);
                    }
                    System.out.println("TARPACK2: " + gameManager.targedPack2);

                } else if (splitArray[0].equals("TARPACK3")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.targedPack3.put(card);
                    }
                    System.out.println("TARPACK3: " + gameManager.targedPack3);

                } else if (splitArray[0].equals("TARPACK4")) {
                    for (int i = 1; i < splitArray.length; i++) {
                        Card card = Solitaire.parseCard(splitArray[i]);
                        gameManager.targedPack4.put(card);
                    }
                    System.out.println("TARPACK4: " + gameManager.targedPack4);

                } else if (splitArray[0].equals("HELPERCARD")) {
                    if (splitArray.length == 1) {
                        gameManager.helperCard = null;
                    } else {
                        Card card = Solitaire.parseCard(splitArray[1]);
                        gameManager.helperCard = card;
                    }
                    System.out.println("HELPERCARD: " + gameManager.helperCard);
                }

            }

            return gameManager;
        }else{
            return this.gameManager;
        }
    }
}