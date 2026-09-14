package stuart.ui;

import java.util.Scanner;

/**
 * Handles all interaction with the user: printing the banner and replies,
 * and reading commands from standard input.
 */
public class Ui {
    /** Indentation applied to every line of text Stuart prints. */
    public static final String TEXT_INDENT = "     ";

    /** Divider printed above and below every reply (without indentation). */
    private static final String HORIZONTAL_LINE = "_".repeat(60);

    /** Indentation applied to each divider line. */
    private static final String DIVIDER_INDENT = "    ";

    /** ANSI code that makes following text bold and cyan (used when not animating). */
    private static final String BANNER_COLOR = "[1;36m";

    /** ANSI code that resets text formatting back to the terminal default. */
    private static final String ANSI_RESET = "[0m";

    /** Filled block-letter "STUART" banner, one row per element. */
    private static final String[] BANNER_LINES = {
        "█████ █████ █   █  ███  ████  █████",
        "█       █   █   █ █   █ █   █   █  ",
        "█████   █   █   █ █████ ████    █  ",
        "    █   █   █   █ █   █ █  █    █  ",
        "█████   █   █████ █   █ █   █   █  ",
    };

    /** How many colour steps the banner animation cycles through. */
    private static final int BANNER_ANIMATION_FRAMES = 60;

    /** Milliseconds between each colour step of the banner animation. */
    private static final int BANNER_FRAME_DELAY_MS = 40;

    /** Phase offset between successive RGB channels in the colour cycle: 120 degrees. */
    private static final double CHANNEL_PHASE_OFFSET = 2 * Math.PI / 3;

    /** Center of the 0-255 RGB range that each channel's sine wave oscillates around. */
    private static final int RGB_MIDPOINT = 128;

    /** Amplitude of the sine wave driving each RGB channel. */
    private static final int RGB_AMPLITUDE = 127;

    private final Scanner scanner;

    /**
     * Creates a new {@code Ui} that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the STUART banner. Animates it through the RGB spectrum when
     * connected to a real interactive terminal; otherwise (e.g. piped input,
     * many IDE run consoles) prints one static coloured frame, so that
     * automated tests get deterministic output.
     */
    public void showBanner() {
        if (System.console() != null) {
            animateBanner();
        } else {
            printBannerFrame(BANNER_COLOR);
        }
    }

    /**
     * Prints the banner once, cycling its colour through the RGB spectrum
     * over {@link #BANNER_ANIMATION_FRAMES} steps, redrawing in place.
     */
    private void animateBanner() {
        for (int frame = 0; frame < BANNER_ANIMATION_FRAMES; frame++) {
            double angle = 2 * Math.PI * frame / BANNER_ANIMATION_FRAMES;
            int red = (int) (Math.sin(angle) * RGB_AMPLITUDE + RGB_MIDPOINT);
            int green = (int) (Math.sin(angle + CHANNEL_PHASE_OFFSET) * RGB_AMPLITUDE + RGB_MIDPOINT);
            int blue = (int) (Math.sin(angle + 2 * CHANNEL_PHASE_OFFSET) * RGB_AMPLITUDE + RGB_MIDPOINT);
            printBannerFrame("[1;38;2;" + red + ";" + green + ";" + blue + "m");
            try {
                Thread.sleep(BANNER_FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (frame < BANNER_ANIMATION_FRAMES - 1) {
                // Move the cursor back to the top-left of the banner to redraw over it.
                System.out.print("[" + BANNER_LINES.length + "F");
            }
        }
    }

    /**
     * Prints one frame of the banner in the given ANSI colour code.
     *
     * @param ansiColor the ANSI escape code to colour the banner with
     */
    private void printBannerFrame(String ansiColor) {
        for (String line : BANNER_LINES) {
            System.out.println(ansiColor + line + ANSI_RESET);
        }
    }

    /**
     * Checks whether there is another command to read.
     *
     * @return true if {@link #readCommand()} can be called again
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command the user typed.
     *
     * @return the raw command line, unmodified
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints one reply from Stuart: the given lines, indented and wrapped
     * between horizontal dividers, followed by a blank line.
     *
     * @param lines the lines of text to display
     */
    public void reply(String... lines) {
        System.out.println(DIVIDER_INDENT + HORIZONTAL_LINE);
        for (String line : lines) {
            System.out.println(TEXT_INDENT + line);
        }
        System.out.println(DIVIDER_INDENT + HORIZONTAL_LINE);
        System.out.println();
    }

    /**
     * Releases the resources held by this {@code Ui}, e.g. its input scanner.
     */
    public void close() {
        scanner.close();
    }
}
