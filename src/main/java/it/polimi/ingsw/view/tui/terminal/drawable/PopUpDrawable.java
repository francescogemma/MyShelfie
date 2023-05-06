package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

public class PopUpDrawable extends FixedLayoutDrawable<TwoLayersDrawable> {
    private final BlurrableDrawable blurrableBackground;
    private final TwoLayersDrawable twoLayers;
    private final TextBox popUpTextBox = new TextBox().unfocusable();

    public PopUpDrawable(Drawable background) {
        blurrableBackground = background.blurrable();

        twoLayers = new TwoLayersDrawable(
            blurrableBackground,
            popUpTextBox
                .center().crop()
                .fixSize(new DrawableSize(30, 50))
                .addBorderBox()
                .center().crop()
        );

        setLayout(twoLayers);
    }

    public void displayPopUp(String text) {
        blurrableBackground.blur(true);
        popUpTextBox.text(text);
        twoLayers.showForeground();
    }

    public void hidePopUp() {
        blurrableBackground.blur(false);
        twoLayers.hideForeground();
    }
}
