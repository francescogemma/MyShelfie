package it.polimi.ingsw.view.tui.terminal.drawable.symbol;

import java.util.*;

/**
 * Represents a {@link Symbol} which has some formatting like background color, foreground color, bold effect, ... .
 * DecoratedSymbols are obtained by adding such formatting to {@link PrimitiveSymbol}.
 * Since DecoratedSymbol inherits from Symbol, it must implement a singleton pattern.
 * DecoratedSymbols which have been crafted from the same PrimitiveSymbol with the same formatting are sotred at the
 * same memory location.
 *
 * @author Cristiano Migali
 */
public class DecoratedSymbol implements Symbol {
    /**
     * It is the underlying {@link PrimitiveSymbol} from which this DecoratedSymbol has been crafted by adding some
     * formatting.
     */
    private final PrimitiveSymbol primitiveSymbol;

    /**
     * It is a list of integers to be used to craft an ANSI escape sequence with the following syntax:
     * ESC[integer1;integer2;...m which can be printed beside the character of the underlying PrimitiveSymbol to
     * format it properly.
     */
    private final List<Integer> decorations;

    /**
     * It is a string (an ANSI escape sequence which surround the character of the underlying primitive symbol) which
     * can be printed to the terminal to show the represented DecoratedSymbol.
     */
    private final String symbol;

    /**
     * It is a string to be added to enclose the character of the underlying PrimitiveSymbol in an ANSI escape sequence,
     * without propagating the decoration to neighbor terminal cells.
     */
    private static final String RESET = "\033[0m";

    /**
     * @param decorations is a list of integers to be used to craft an ANSI escape sequence with the following syntax:
     *                    ESC[integer1;integer2;...m
     * @return an ANSI escape sequence with the following syntax: ESC[integer1;integer2;...m which can be printed beside
     * the character of the underlying PrimitiveSymbol to format it properly.
     */
    private static String parseDecorations(List<Integer> decorations) {
        StringBuilder parsedDecorations = new StringBuilder("\033[");

        for (int i = 0; i < decorations.size() - 1; i++) {
            parsedDecorations.append(decorations.get(i)).append(";");
        }
        parsedDecorations.append(decorations.get(decorations.size() - 1));

        return parsedDecorations.append("m").toString();
    }

    /**
     * Constructor of the class.
     * It initializes the symbol string that must be printed to the terminal to obtain the represented DecoratedSymbol.
     *
     * @param primitiveSymbol is the underlying PrimitiveSymbol from which the DecoratedSymbol will be crafted by
     *                        adding some formatting.
     * @param decorations is a list of integers to be used to craft an ANSI escape sequence with the following syntax:
     *                    ESC[integer1;integer2;...m which represents the formatting to be added to the PrimitiveSymbol.
     */
    private DecoratedSymbol(PrimitiveSymbol primitiveSymbol, List<Integer> decorations) {
        this.primitiveSymbol = primitiveSymbol;
        this.decorations = decorations;

        this.symbol = parseDecorations(decorations) + primitiveSymbol.asString() + RESET;
    }

    /**
     * Map of instances to implement a singleton pattern. The key of the map is the string (ANSI control sequence +
     * the character of the underlying PrimitiveSymbol) which can be printed to obtain the DecoratedSymbol at the
     * corresponding value.
     */
    private static final Map<String, DecoratedSymbol> INSTANCES = new HashMap<>();

    /**
     * This class implements a singleton pattern. This is the only way to retrieve an instance of a DecoratedSymbol.
     *
     * @param primitiveSymbol is the underlying PrimitiveSymbol from which the DecoratedSymbol instance will be crafted.
     * @param previousDecorations is a list of integers to be used to craft an ANSI escape sequence with the following
     *                            syntax ESC[integer1;integer2;...m which represents a decoration already used to
     *                            format a DecoratedSymbol.
     * @param newDecorations is a list of integers analogous to previousDecorations that must be used to
     *                       further decorate the DecoratedSymbol represented by primitiveSymbol + previousDecorations.
     * @return a DecoratedSymbol crafted by formatting primitiveSymbol with previousDecorations and newDecorations.
     */
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

    /**
     * This class implements a singleton pattern. This is the only way to retrieve an instance of a DecoratedSymbol.
     *
     * @param primitiveSymbol is the underlying PrimitiveSymbol from which the DecoratedSymbol instance will be
     *                        crafted.
     * @param newDecorations is a list of integers to be used to craft an ANSI escape sequence with the following
     *                       syntax ESC[integer1;integer2;...m which represents the formatting to be added to
     *                       the primitiveSymbol.
     * @return a DecoratedSymbol crafted by formatting primitiveSymbol with newDecorations.
     */
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

    @Override
    public DecoratedSymbol colorForeground(Color color) {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, color.colorForeground());
    }

    @Override
    public DecoratedSymbol colorBackground(Color color) {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, color.colorBackground());
    }

    @Override
    public DecoratedSymbol bold() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(1));
    }

    @Override
    public DecoratedSymbol italic() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(3));
    }

    @Override
    public DecoratedSymbol underline() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(4));
    }

    @Override
    public DecoratedSymbol strikethrough() {
        return DecoratedSymbol.getInstance(primitiveSymbol, decorations, List.of(9));
    }
}
