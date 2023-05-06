package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

import java.util.ArrayList;
import java.util.List;

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
                .fixSize(new DrawableSize(8, 20))
                .addBorderBox()
                .center().crop()
        );

        setLayout(twoLayers);
    }

    public void displayPopUp(String text) {
        blurrableBackground.blur(true);

        StringBuilder justifiedText = new StringBuilder();

        List<String> line = new ArrayList<>();
        int lineLength = 0;

        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            line.add(words[i]);
            // The first word doesn't need space.
            lineLength += (lineLength == 0 ? 0 : 1) + words[i].length();

            // +1 is needed to account for the space.
            if (i == words.length - 1 || lineLength + 1 + words[i + 1].length() > 20) {
                // We want at least line.size() - 1 spaces.
                int remainingSpaces = 20 - line.stream().mapToInt(String::length).sum()
                    - (line.size() - 1);

                for (int j = 0; j < line.size() - 1; j++) {
                    int numOfSpaces;
                    if (j == line.size() - 2) {
                        numOfSpaces = 1 + remainingSpaces;
                        remainingSpaces = 0;
                    } else {
                        numOfSpaces = 1 + remainingSpaces / (line.size() - 1 - j);
                        remainingSpaces -= numOfSpaces;
                    }

                    justifiedText.append(line.get(j)).append(" ".repeat(numOfSpaces));
                }
                justifiedText.append(line.get(line.size() - 1)).append("\n");

                line.clear();
                lineLength = 0;
            }
        }

        popUpTextBox.text(justifiedText.toString());
        twoLayers.showForeground();
    }

    public void hidePopUp() {
        blurrableBackground.blur(false);
        twoLayers.hideForeground();
    }
}
