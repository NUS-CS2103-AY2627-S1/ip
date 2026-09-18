package com.fireinyu.themyth.ui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Formatter that converts response messages into styled JavaFX {@link Text} nodes.
 * Randomly applies vibrant font colors and font weights to parts of the text to give
 * TheMyth's responses dramatic, flamboyant flair.
 */
public class FlamboyantTextFormatter {

    /** Default probability that a non-whitespace word will be chosen for emphasis. */
    public static final double EMPHASIS_PROBABILITY = 0.4;

    /** Base font size used across dialog text nodes. */
    public static final double BASE_FONT_SIZE = 14.0;

    /** Base neutral color for unaccented or standard text (dark charcoal). */
    public static final String DEFAULT_TEXT_COLOR = "#212121";

    /**
     * Curated palette of flamboyant jewel and diva colors with high contrast on white backgrounds.
     */
    private static final String[] FLAMBOYANT_COLORS = {
        "#D81B60", // Vivid Fuchsia / Magenta
        "#8E24AA", // Royal Violet / Amethyst
        "#6A1B9A", // Deep Velvet Purple
        "#C2185B", // Ruby Diva Crimson
        "#B8860B", // Rhinestone Dark Gold
        "#E64A19", // Vivid Passion Coral
        "#00897B", // Jewel Turquoise
        "#303F9F", // Electric Sapphire
        "#9C27B0" // Vivid Orchid
    };

    /**
     * Font weights used for emphasized text segments.
     */
    private static final FontWeight[] FLAMBOYANT_WEIGHTS = {
        FontWeight.BOLD,
        FontWeight.EXTRA_BOLD
    };

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\\s+|\\S+)");
    private static final FlamboyantTextFormatter DEFAULT_FORMATTER = new FlamboyantTextFormatter();

    private final Random random;

    /**
     * Constructs a FlamboyantTextFormatter using a default pseudo-random number generator.
     */
    public FlamboyantTextFormatter() {
        this(new Random());
    }

    /**
     * Constructs a FlamboyantTextFormatter with a specified {@link Random} instance,
     * useful for deterministic testing.
     *
     * @param random the random number generator to use.
     */
    public FlamboyantTextFormatter(Random random) {
        this.random = random;
    }

    /**
     * Formats a response string into a list of styled {@link Text} nodes using the default formatter.
     *
     * @param text the message body to format.
     * @return a list of styled {@link Text} nodes.
     */
    public static List<Text> format(String text) {
        return DEFAULT_FORMATTER.formatResponse(text);
    }

    /**
     * Formats a user string into plain {@link Text} nodes with standard weight and neutral color.
     *
     * @param text the user input string.
     * @return a list containing a plain {@link Text} node.
     */
    public static List<Text> formatPlain(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return List.of(createPlainText(text));
    }

    /**
     * Formats a response string into a list of styled {@link Text} nodes, randomly emphasizing
     * parts with vibrant font colors and heavy font weights.
     *
     * @param text the message body to format.
     * @return a list of styled {@link Text} nodes.
     */
    public List<Text> formatResponse(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        Matcher matcher = TOKEN_PATTERN.matcher(text);
        List<String> tokens = new ArrayList<>();
        List<Integer> wordIndices = new ArrayList<>();

        while (matcher.find()) {
            String token = matcher.group();
            if (!Character.isWhitespace(token.charAt(0))) {
                wordIndices.add(tokens.size());
            }
            tokens.add(token);
        }

        boolean[] isEmphasized = selectEmphasis(tokens.size(), wordIndices);
        List<Text> result = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            result.add(isEmphasized[i] ? createEmphasizedText(token) : createPlainText(token));
        }
        return result;
    }

    /**
     * Selects words for emphasis, choosing at least one whenever the response contains words.
     */
    private boolean[] selectEmphasis(int tokenCount, List<Integer> wordIndices) {
        boolean[] isEmphasized = new boolean[tokenCount];
        boolean hasAnyEmphasis = false;

        for (int wordIndex : wordIndices) {
            if (random.nextDouble() < EMPHASIS_PROBABILITY) {
                isEmphasized[wordIndex] = true;
                hasAnyEmphasis = true;
            }
        }

        // Guarantee at least one emphasized word if words exist, so every response exhibits flair
        if (!hasAnyEmphasis && !wordIndices.isEmpty()) {
            int selectedWord = wordIndices.get(random.nextInt(wordIndices.size()));
            isEmphasized[selectedWord] = true;
        }

        return isEmphasized;
    }

    /**
     * Creates a text node using the standard dialog styling.
     */
    private static Text createPlainText(String text) {
        Text node = new Text(text);
        node.setFill(Color.web(DEFAULT_TEXT_COLOR));
        node.setFont(Font.font(null, FontWeight.NORMAL, BASE_FONT_SIZE));
        node.setStyle("-fx-fill: " + DEFAULT_TEXT_COLOR + "; -fx-font-weight: normal; -fx-font-size: "
                + (int) BASE_FONT_SIZE + "px;");
        return node;
    }

    /**
     * Creates an emphasized text node, choosing its color before its font weight.
     */
    private Text createEmphasizedText(String text) {
        Text node = new Text(text);
        String colorHex = FLAMBOYANT_COLORS[random.nextInt(FLAMBOYANT_COLORS.length)];
        FontWeight weight = FLAMBOYANT_WEIGHTS[random.nextInt(FLAMBOYANT_WEIGHTS.length)];
        String weightCss = (weight == FontWeight.EXTRA_BOLD) ? "800" : "bold";

        node.setFill(Color.web(colorHex));
        node.setFont(Font.font(null, weight, BASE_FONT_SIZE));
        node.setStyle("-fx-fill: " + colorHex + "; -fx-font-weight: " + weightCss
                + "; -fx-font-size: " + (int) BASE_FONT_SIZE + "px;");
        return node;
    }

    /**
     * Returns the array of available flamboyant colors.
     *
     * @return copy of the flamboyant color palette.
     */
    public static String[] getFlamboyantColors() {
        return FLAMBOYANT_COLORS.clone();
    }

    /**
     * Returns the array of available flamboyant font weights.
     *
     * @return copy of the flamboyant font weights.
     */
    public static FontWeight[] getFlamboyantWeights() {
        return FLAMBOYANT_WEIGHTS.clone();
    }
}
