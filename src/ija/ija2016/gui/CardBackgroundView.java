package ija.ija2016.gui;

import javax.swing.*;

/**
 * GUI representation of card background.
 * @author Marek Marušin, xmarus08
 * @version 1.0
 * @since 2017-05-06
 */
public class CardBackgroundView extends JLabel {

    /**
     * Constructor, set params to JLabel and create background representation.
     * @param icon ImageIcon to set as background.
     */
    public CardBackgroundView(ImageIcon icon) {
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

