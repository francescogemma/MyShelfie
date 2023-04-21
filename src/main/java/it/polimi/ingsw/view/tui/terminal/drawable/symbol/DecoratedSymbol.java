package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.*;

public class DecoratedSymbol implements Symbol {
    private final PrimitiveSymbol primitiveSymbol;
    private final List<Integer> decorations;

    private final String symbol;

    private static final String RESET = "\033[0m";

    private static String parseDecorations(List<Integer> decorations) {
        StringBuilder parsedDecorations = new StringBuilder("\033[");

        for (int i = 0; i < decorations.size() - 1; i++) {
            parsedDecorations.append(decorations.get(i)).append(";");
        }
        parsedDecorations.append(decorations.get(decorations.size() - 1));

        return parsedDecorations.append("m").toString();
    }

    private DecoratedSymbol(PrimitiveSymbol primitiveSymbol, List<Integer> decorations) {
        this.primitiveSymbol = primitiveSymbol;
        this.decorations = decorations;

        this.symbol = parseDecorations(decorations) + primitiveSymbol.asString() + RESET;
    }

    private static final Map<String, DecoratedSymbol> INSTANCES = new HashMap<>();

    static DecoratedSymbol getInstance(PrimitiveSymbol primitiveSymbol, List<Integer> previousDecorations,
                                              List<Integer> newDecorations) {
        ArrayList<Integer> decorations = new ArrayList<>(previousDecorations);
        decorations.addAll(newDecorations);

        if (INSTANCES.get(parseDecorations(decorations) + primitiveSymbol.asString()) == null) {
            INSTANCES.put(parseDecorations(decorations) + primitiveSymbol.asString(), new DecoratedSymbol(primitiveSymbol,
                decorations));
        }

        return INSTANCES.get(parseDecorations(decorations) + primitiveSymbol.asString());
    }

    static DecoratedSymbol getInstance(PrimitiveSymbol primitiveSymbol, List<Integer> newDecorations) {
        return getInstance(primitiveSymbol, new ArrayList<>(), newDecorations);
    }

    @Override
    public String asString() {
        return symbol;
    }

    @Override
    public PrimitiveSymbol getPrimitiveSymbol() {
        return primitiveSymbol;
    }

    public DecoratedSymbol colorForeground(Color color) {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, color.colorForeground());
    }

    public DecoratedSymbol colorBackground(Color color) {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, color.colorBackground());
    }

    public DecoratedSymbol bold() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(1));
    }

    public DecoratedSymbol italic() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(3));
    }

    public DecoratedSymbol underline() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(4));
    }

    public DecoratedSymbol strikethrough() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(9));
    }
}
