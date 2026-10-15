package wwaffle.gui;

/** Preserves the original Post Malone card definition for existing callers. */
public class GoodbyeMusicCard extends MusicCard {
    /** Creates the original artwork, attribution, and goodbye excerpt. */
    public GoodbyeMusicCard() {
        super("/images/post-malone-goodbyes.png", "POST MALONE · GOODBYES (FEAT. YOUNG THUG)",
                "Goodbye, goodbye, goodbye. I'm no good at goodbyes.");
    }
}
