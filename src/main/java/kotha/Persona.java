package kotha;

import java.util.function.BooleanSupplier;

/** Represents one of Kotha's two contrasting personalities. */
public enum Persona {
    ROYAL("royal"),
    RUDE("rude");

    private final String style;

    Persona(String style) {
        this.style = style;
    }

    /** Chooses a persona using a source that returns true for the royal personality. */
    static Persona choose(BooleanSupplier isRoyalSelector) {
        assert isRoyalSelector != null : "A persona selector must be provided";
        return isRoyalSelector.getAsBoolean() ? ROYAL : RUDE;
    }

    /** Returns the phrase belonging to this persona. */
    String choosePhrase(String royalPhrase, String rudePhrase) {
        return this == ROYAL ? royalPhrase : rudePhrase;
    }

    /** Returns the CSS style category belonging to this persona. */
    public String getStyle() {
        return style;
    }
}
