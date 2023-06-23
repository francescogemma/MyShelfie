package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * It is a {@link Drawable} which allows to display a pop-up on top of a background Drawable which is blurred.
 * The pop-up is displayed as a text-box surrounded by a border box.
 *
 * @author Cristiano Migali
 */
public class PopUpDrawable extends FixedLayoutDrawable<TwoLayersDrawable> {
    /**
     * {@link BlurrableDrawable} which correspond to the background on top of which the pop-up will be displayed.
     */
    private final BlurrableDrawable blurrableBackground;

    /**
     * It is a {@link TwoLayersDrawable} on whose background layer there is the {@link PopUpDrawable#blurrableBackground},
     * on the foreground there is the pop-up instead.
     */
    private final TwoLayersDrawable twoLayers;

    /**
     * It is the {@link TextBox} used to display the pop-up message.
     */
    private final TextBox popUpTextBox = new TextBox().unfocusable();

    /**
     * Constructor of the class.
     * It initializes the layout of the Drawable.
     *
     * @param background is the background Drawable on top of which we want to display the pop-up.
     */
    public PopUpDrawable(Drawable background) {
        blurrableBackground = background.blurrable();

        twoLayers = new TwoLayersDrawable(
            blurrableBackground,
            popUpTextBox
                .center().crop()
                .fixSize(new DrawableSize(8, 30))
                .addBorderBox()
                .center().crop()
        );

        setLayout(twoLayers);
    }

    /**
     * Displays a pop-up with the given text message on the foreground layer and blurs the background.
     *
     * @param text is the text message of the pop-up that will be displayed.
     */
    public void displayPopUp(String text) {
        blurrableBackground.blur(true);

        StringBuilder adaptedText = new StringBuilder();

        List<String> line = new ArrayList<>();
        int lineLength = 0;

        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            line.add(words[i]);
            // The first word doesn't need space.
            lineLength += (lineLength == 0 ? 0 : 1) + words[i].length();

            // +1 is needed to account for the space.
            if (i == words.length - 1 || lineLength + 1 + words[i + 1].length() > 20) {
                for (int j = 0; j < line.size() - 1; j++) {
                    adaptedText.append(line.get(j)).append(" ");
                }

                adaptedText.append(line.get(line.size() - 1))
                    .append(" ".repeat(20 -
                        line.stream().mapToInt(String::length).sum() - (line.size() - 1))).append("\n");

                line.clear();
                lineLength = 0;
            }
        }

        popUpTextBox.text(adaptedText.toString());
        twoLayers.showForeground();
    }

    /**
     * Hides the pop-up which is currently being displayed on the foreground and unblurs the background.
     */
    public void hidePopUp() {
        blurrableBackground.blur(false);
        twoLayers.hideForeground();
    }
}
