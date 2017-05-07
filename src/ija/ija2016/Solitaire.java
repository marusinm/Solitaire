package ija.ija2016;

import ija.ija2016.gui.Game;
import ija.ija2016.gui.GameManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

//src: http://www.wideskills.com/java-tutorial/java-jinternalframe-class-example
public class Solitaire extends JFrame {

    public static GameManager gameManager = new GameManager();

    JDesktopPane jdpDesktop;
    int gameIndex = 0;
    Game game1 = null;
    Game game2 = null;
    Game game3 = null;
    Game game4 = null;
    Solitaire() {
        super("Solitare Demo");
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
        // A specialized layered pane to be used with JInternalFrames
        createNewGameFrame(); // Create first window
        setContentPane(jdpDesktop);
        setJMenuBar(createMainMenuBar());
        // Make dragging faster by setting drag mode to Outline
        jdpDesktop.putClientProperty("JDesktopPane.dragMode", "outline");
    }
    protected JMenuBar createMainMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_N);

        JMenuItem menuItem = new JMenuItem("new game");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewGameFrame();
            }
        });

        menu.add(menuItem);
        menuBar.add(menu);
        return menuBar;
    }

    protected void createNewGameFrame() {
        int newGameWidth;
        int newGameHeight;
        Game newGame;
        ++gameIndex;
        Solitaire.gameManager.numberOfGames++;

        if (gameIndex == 1){
            newGameWidth = getBounds().width;
            newGameHeight = getBounds().height;
        }else{

            Solitaire.gameManager.numberOfGames = gameIndex;

            newGameWidth = getBounds().width/2;
            newGameHeight = getBounds().height/2;

            if (gameIndex == 2){
                //TODO: 1, before delete full size game, save it
                //2, then delete full size window
                //3, get load saved full size game to smaller game

                //deleting full size game
                jdpDesktop.remove(game1);
                jdpDesktop.updateUI();

                //creating new size game
                newGame = new Game(newGameWidth, newGameHeight,1, getBounds().width, getBounds().height);
                newGame.setVisible(true);
                jdpDesktop.add(newGame);
                try {
                    newGame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                }
                game1 = newGame;
            }
        }

        newGame = new Game(newGameWidth, newGameHeight,gameIndex,getBounds().width, getBounds().height);
        newGame.setVisible(true);
        // Every JInternalFrame must be added to content pane using JDesktopPane
        jdpDesktop.add(newGame);
        try {
            newGame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        switch (gameIndex){
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

    public static void main(String[] args) {
        Solitaire frame = new Solitaire();
        frame.setVisible(true);
    }
}

