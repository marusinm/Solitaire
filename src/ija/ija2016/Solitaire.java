package ija.ija2016;

import ija.ija2016.cardpack.Card;
import ija.ija2016.cardpack.CardDeck;
import ija.ija2016.cardpack.CardStack;
import ija.ija2016.gui.Game;
import ija.ija2016.gui.GameManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Main class.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class Solitaire extends JFrame {

    public static GameManager gameManager = new GameManager();
    public static Solitaire solitaire;
    public static int numberOfGames = 0;

    JDesktopPane jdpDesktop;
//    int gameIndex = 0;
    public Game game1 = null;
    public Game game2 = null;
    public Game game3 = null;
    public Game game4 = null;
    Solitaire() {
        super("Solitare");
        solitaire = this;

        // Make the main window positioned as 50 pixels from each edge of the
        // screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        // Add a Window Exit Listener
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // Create and Set up the GUI.
        jdpDesktop = new JDesktopPane();

        createNewGameFrame(null); // Create first window
//        loadGame("examples/hello_world_game");

        setContentPane(jdpDesktop);
        setJMenuBar(createMainMenuBar());
        // Make dragging faster by setting drag mode to Outline
        jdpDesktop.putClientProperty("JDesktopPane.dragMode", "outline");
    }
    protected JMenuBar createMainMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JButton buttonNewGame= new JButton("New Game");
        buttonNewGame.setOpaque(true);
        buttonNewGame.setContentAreaFilled(false);
        buttonNewGame.setBorderPainted(false);
        buttonNewGame.setFocusable(false);
        buttonNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (numberOfGames < 4) {
                    createNewGameFrame(null);
                }else {
                    JOptionPane.showMessageDialog(jdpDesktop, "You can create only 4 instances!");
                }
            }
        });

        JButton buttonLoad= new JButton("Load Game");
        buttonLoad.setOpaque(true);
        buttonLoad.setContentAreaFilled(false);
        buttonLoad.setBorderPainted(false);
        buttonLoad.setFocusable(false);
        buttonLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("load...");
                final JFileChooser fc = new JFileChooser("examples/");
                int returnVal = fc.showOpenDialog(jdpDesktop);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    System.out.println("Open from path: " + file.getAbsoluteFile().toString());
                    loadGame(file.getAbsoluteFile().toString());
                    JOptionPane.showMessageDialog(jdpDesktop, "Game loaded!");
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });


        menuBar.add(buttonNewGame);
        menuBar.add(buttonLoad);
        return menuBar;
    }

    /**
     * Function creates new instance of Game.
     * @param gameManager launch with gameManger if exist, null otherwise
     */
    public void createNewGameFrame(GameManager gameManager) {
        int newGameWidth;
        int newGameHeight;
        Game newGame;
//        ++gameIndex;
        Solitaire.numberOfGames++;
        System.out.println("games num: "+Solitaire.numberOfGames);

        if (numberOfGames == 1){
            newGameWidth = getBounds().width;
            newGameHeight = getBounds().height;
        }else{
            newGameWidth = getBounds().width/2;
            newGameHeight = getBounds().height/2;

            if (numberOfGames == 2){
                //TODO: 1, before delete full size game, save it
                //2, then delete full size window
                //3, get load saved full size game to smaller game

                ArrayList<String> undoHistoryFromGame1 = new ArrayList<>(game1.undoHistory);

                //deleting full size game
                jdpDesktop.remove(game1);
                jdpDesktop.updateUI();

//                for (String s : undoHistoryFromGame1)
//                    System.out.println("undo game: \n"+s+"\n\n");
                GameManager firstGameManager = game1.loadGame();

                //creating new size game
                newGame = new Game(newGameWidth, newGameHeight, 1, getBounds().width, getBounds().height, firstGameManager);
                newGame.setVisible(true);
                jdpDesktop.add(newGame);
                try {
                    newGame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                }
                game1 = newGame;
            }
        }

        newGame = new Game(newGameWidth, newGameHeight,Solitaire.numberOfGames,getBounds().width, getBounds().height, gameManager);
        newGame.setVisible(true);
        // Every JInternalFrame must be added to content pane using JDesktopPane
        jdpDesktop.add(newGame);
        try {
            newGame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        switch (numberOfGames){
            case 1:
                game1 = newGame;
                break;
            case 2:
                game2 = newGame;
                break;
            case 3:
                game3 = newGame;
                break;
            case 4:
                game4 = newGame;
                break;
        }
    }

    /**
     * Start new game from saved file.
     * Fill new game manager and launch new game.
     * @param file_string path to file.
     */
    public void loadGame(String file_string){
        //create new GameManager, clear/pop all data from it and load new data from saved file
        GameManager gameManager = new GameManager();
        gameManager.deck.pop(gameManager.deck.getCard(0)); //pop all cards
        gameManager.helperDeck = new ArrayList<>();
        gameManager.helperCard = null;
        gameManager.targedPack1 = new CardDeck(13);
        gameManager.targedPack2 = new CardDeck(13);
        gameManager.targedPack3 = new CardDeck(13);
        gameManager.targedPack4 = new CardDeck(13);
        for (CardStack workingPack : gameManager.workingPacksArray){
            workingPack.pop(workingPack.getCard(0)); //pop all cards
            workingPack.setIsWorkingPack(false);
        }

        String line;
        try (
//                InputStream fis = new FileInputStream("out.txt");
                InputStream fis = new FileInputStream(file_string);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                String[] splitArray = null;
                try {
                    splitArray = line.split("\\s+");
                } catch (PatternSyntaxException ex) {
                    System.out.println(ex);
                }

                if (splitArray[0].equals("WP1")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack1.put(card);
                    }
                    gameManager.workingPack1.setIsWorkingPack(true);
                    System.out.println("working pack1: "+gameManager.workingPack1);

                }else if (splitArray[0].equals("WP2")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack2.put(card);
                    }
                    gameManager.workingPack2.setIsWorkingPack(true);
                    System.out.println("working pack2: "+gameManager.workingPack2);

                }else if (splitArray[0].equals("WP3")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack3.put(card);
                    }
                    gameManager.workingPack3.setIsWorkingPack(true);
                    System.out.println("working pack3: "+gameManager.workingPack3);

                }else if (splitArray[0].equals("WP4")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack4.put(card);
                    }
                    gameManager.workingPack4.setIsWorkingPack(true);
                    System.out.println("working pack4: "+gameManager.workingPack4);

                }else if (splitArray[0].equals("WP5")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack5.put(card);
                    }
                    gameManager.workingPack5.setIsWorkingPack(true);
                    System.out.println("working pack5: "+gameManager.workingPack5);

                }else if (splitArray[0].equals("WP6")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack6.put(card);
                    }
                    gameManager.workingPack6.setIsWorkingPack(true);
                    System.out.println("working pack6: "+gameManager.workingPack6);

                }else if (splitArray[0].equals("WP7")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.workingPack7.put(card);
                    }
                    gameManager.workingPack7.setIsWorkingPack(true);
                    System.out.println("working pack7: "+gameManager.workingPack7);

                }else if (splitArray[0].equals("DECK")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.deck.put(card);
                    }
                    System.out.println("deck: "+gameManager.deck);

                }else if (splitArray[0].equals("HELPERDECK")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.helperDeck.add(card);
                    }
                    System.out.println("HELPERDECK: "+gameManager.helperDeck);

                }else if (splitArray[0].equals("TARPACK1")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.targedPack1.setIsTargetPack(true);
                        gameManager.targedPack1.setPackColor(ija.ija2016.model.cards.Card.Color.CLUBS);
                        gameManager.targedPack1.put(card);
                    }
                    System.out.println("TARPACK1: "+gameManager.targedPack1);

                }else if (splitArray[0].equals("TARPACK2")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.targedPack2.setIsTargetPack(true);
                        gameManager.targedPack2.setPackColor(ija.ija2016.model.cards.Card.Color.DIAMONDS);
                        gameManager.targedPack2.put(card);
                    }
                    System.out.println("TARPACK2: "+gameManager.targedPack2);

                }else if (splitArray[0].equals("TARPACK3")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.targedPack3.setIsTargetPack(true);
                        gameManager.targedPack3.setPackColor(ija.ija2016.model.cards.Card.Color.HEARTS);
                        gameManager.targedPack3.put(card);
                    }
                    System.out.println("TARPACK3: "+gameManager.targedPack3);

                }else if (splitArray[0].equals("TARPACK4")){
                    for (int i = 1; i < splitArray.length; i++){
                        Card card = parseCard(splitArray[i]);
                        gameManager.targedPack4.setIsTargetPack(true);
                        gameManager.targedPack4.setPackColor(ija.ija2016.model.cards.Card.Color.SPADES);
                        gameManager.targedPack4.put(card);
                    }
                    System.out.println("TARPACK4: "+gameManager.targedPack4);

                }else if (splitArray[0].equals("HELPERCARD")){
                    if (splitArray.length == 1){
                        gameManager.helperCard = null;
                    }else{
                        Card card = parseCard(splitArray[1]);
                        gameManager.helperCard = card;
                    }
                    System.out.println("HELPERCARD: "+gameManager.helperCard);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //launch new game
        createNewGameFrame(gameManager);
    }

    /**
     * Create Card object from text representation;
     * @param s card text representation.
     * @return Card object.
     */
    public static Card parseCard(String s) {
        char facedUp = s.charAt(0);
        char valueChar = s.charAt(1);

        //in case that value is 10
        char colorChar;
        if (s.length() == 3) {
            colorChar = s.charAt(2);
        }else{
            colorChar = s.charAt(3);
        }

        Card.Color color;
        if (colorChar == 'c')
            color = Card.Color.CLUBS;
        else if (colorChar == 'd')
            color = Card.Color.DIAMONDS;
        else if (colorChar == 's')
            color = Card.Color.SPADES;
        else
            color = Card.Color.HEARTS;

        int value;
        if (s.length() > 3)
            value = 10;
        else if (valueChar == 'a')
            value = 1;
        else if (valueChar == 'j')
            value = 11;
        else if (valueChar == 'q')
            value = 12;
        else if (valueChar == 'k')
            value = 13;
        else
            value = valueChar - 48; // - asci value

        boolean isFacedUp = false;
        if (facedUp == 't')
            isFacedUp = true;

        Card card = new Card(color, value);
        if (isFacedUp)
            card.turnFaceUp();

        return card;
    }

    public static void main(String[] args) {
        Solitaire frame = new Solitaire();
        frame.setVisible(true);
    }
}

