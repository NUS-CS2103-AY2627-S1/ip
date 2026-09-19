package nori.ui;

import java.util.List;
import java.util.Set;

import javafx.scene.text.Font;

/**
 * Chooses the monospaced family the conversation is set in.
 *
 * JavaFX does not walk a font-family list the way a browser does: it reads the
 * first family named and, if that one is not installed, falls back to the
 * platform's proportional System face rather than trying the next name. A
 * stylesheet therefore cannot express "this font, or the next best monospaced
 * one", and a list written there would quietly lose its alignment on every
 * machine missing the first family. The choice is made here instead, against
 * the families the running machine actually has.
 */
final class ConversationFont {
    /**
     * The monospaced families worth having, best first.
     *
     * The list covers the usual Windows, macOS and Linux faces, so a
     * conversation is set in a face that belongs to its platform wherever one
     * is installed.
     */
    private static final List<String> PREFERRED_FAMILIES = List.of(
            "Cascadia Mono", "Consolas", "SF Mono", "Menlo", "DejaVu Sans Mono", "Ubuntu Mono");

    /**
     * The family used where none of the preferred ones is installed.
     *
     * JavaFX maps this logical name to a real monospaced face on every
     * platform, so the columns of a task list line up even on a machine with
     * no recognizable font at all.
     */
    private static final String FALLBACK_FAMILY = "Monospaced";

    /** The chosen family, looked up once because the installed fonts do not change. */
    private static final String FAMILY = findAvailableFamily();

    /** Prevents instantiation of this stateless chooser. */
    private ConversationFont() {
    }

    /**
     * Returns the inline style that sets a message in the chosen monospaced family.
     *
     * Only the family is set here. Size, spacing and colour stay in the
     * stylesheet, where the rest of the type scale lives.
     *
     * @return the JavaFX inline style naming the family.
     */
    static String getFamilyStyle() {
        return "-fx-font-family: \"" + FAMILY + "\";";
    }

    /**
     * Returns the best installed monospaced family, or the logical fallback.
     *
     * @return the family name to set messages in.
     */
    private static String findAvailableFamily() {
        Set<String> installedFamilies = Set.copyOf(Font.getFamilies());
        return PREFERRED_FAMILIES.stream()
                .filter(installedFamilies::contains)
                .findFirst()
                .orElse(FALLBACK_FAMILY);
    }
}
