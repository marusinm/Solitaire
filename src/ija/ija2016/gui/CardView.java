package ija.ija2016.gui;
import ija.ija2016.cardpack.Card;

import javax.swing.*;

/**
 * GUI representation of card.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
class CardView extends JLabel {

    /**
     * Constructor, set params to JLabel and create card representation.
     * @param card Card witch will be draw on board.
     */
    public CardView(Card card) {

        ImageIcon icon;
        if (!card.isTurnedFaceUp()){
            icon = new ImageIcon("lib/back.gif");
        }else{
            icon = new ImageIcon("lib/"+card+".gif");
        }

        icon = GameManager.resizeIconIfNeed(icon);

        setIcon(icon);
        // setMargin(new Insets(0,0,0,0));
        setIconTextGap(0);
        // setBorderPainted(false);
        setBorder(null);
        setText(null);
        setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
    }
}

