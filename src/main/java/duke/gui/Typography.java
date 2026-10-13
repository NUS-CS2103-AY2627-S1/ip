package duke.gui;

import java.util.List;

import javafx.scene.Parent;
import javafx.scene.text.Font;

/** Chooses an installed, readable UI font without downloading or bundling font files. */
final class Typography {
    private static final List<String> PREFERRED_FAMILIES = List.of(
            "Segoe UI", "Noto Sans", "Arial", "DejaVu Sans", "Liberation Sans");

    private Typography() {
    }

    /** Applies one available family; JavaFX CSS does not support browser-style font-family lists. */
    static void apply(Parent root) {
        root.setStyle("-fx-font-family: '" + selectFontFamily(Font.getFamilies()) + "';");
    }

    /** Selects the first installed preference, with JavaFX's system font as the safe fallback. */
    static String selectFontFamily(List<String> availableFamilies) {
        for (String family : PREFERRED_FAMILIES) {
            if (availableFamilies.contains(family)) {
                return family;
            }
        }
        return "System";
    }
}
