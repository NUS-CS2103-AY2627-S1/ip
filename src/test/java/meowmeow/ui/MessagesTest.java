package meowmeow.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Messages} - specifically that every category really does
 * hold several phrasings (not just one dressed up as an array), and that the
 * two categories carrying a {@code %s} placeholder for a date substitute it
 * correctly no matter which phrasing is picked.
 *
 * <p>{@link Messages} exposes no way to inspect its arrays directly, so
 * "several phrasings" is checked the same way a caller would ever notice it:
 * by drawing many picks from one {@link Random} - the same way {@link Ui}
 * uses it - and counting the distinct results. A fresh {@code Random} per
 * draw is deliberately avoided: {@code java.util.Random}'s output for the
 * very first call after sequential small seeds is known to correlate, which
 * would make this test flaky for exactly the reason it exists.
 */
public class MessagesTest {

    private static final int TRIALS = 50;
    private static final int MINIMUM_VARIANTS = 4;

    private int distinctResultCount(Function<Random, String> pick) {
        Random random = new Random(1);
        Set<String> seen = new HashSet<>();
        for (int trial = 0; trial < TRIALS; trial++) {
            seen.add(pick.apply(random));
        }
        return seen.size();
    }

    @Test
    public void added_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::added) >= MINIMUM_VARIANTS);
    }

    @Test
    public void removed_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::removed) >= MINIMUM_VARIANTS);
    }

    @Test
    public void markedDone_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::markedDone) >= MINIMUM_VARIANTS);
    }

    @Test
    public void markedNotDone_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::markedNotDone) >= MINIMUM_VARIANTS);
    }

    @Test
    public void priorityChanged_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::priorityChanged) >= MINIMUM_VARIANTS);
    }

    @Test
    public void listHeader_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::listHeader) >= MINIMUM_VARIANTS);
    }

    @Test
    public void dateHeader_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::dateHeader) >= MINIMUM_VARIANTS);
    }

    @Test
    public void emptyDate_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::emptyDate) >= MINIMUM_VARIANTS);
    }

    @Test
    public void findHeader_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::findHeader) >= MINIMUM_VARIANTS);
    }

    @Test
    public void emptyFind_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::emptyFind) >= MINIMUM_VARIANTS);
    }

    @Test
    public void errorLeadIn_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(Messages::errorLeadIn) >= MINIMUM_VARIANTS);
    }

    @Test
    public void welcomePlain_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(random -> Messages.welcome(MessageStyle.PLAIN, random)) >= MINIMUM_VARIANTS);
    }

    @Test
    public void welcomeRich_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(random -> Messages.welcome(MessageStyle.RICH, random)) >= MINIMUM_VARIANTS);
    }

    @Test
    public void farewellPlain_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(random -> Messages.farewell(MessageStyle.PLAIN, random)) >= MINIMUM_VARIANTS);
    }

    @Test
    public void farewellRich_hasAtLeastFourVariants() {
        assertTrue(distinctResultCount(random -> Messages.farewell(MessageStyle.RICH, random)) >= MINIMUM_VARIANTS);
    }

    @Test
    public void dateHeader_everyVariant_formatsCleanlyWithOnePlaceholder() {
        for (int seed = 0; seed < TRIALS; seed++) {
            String formatted = String.format(Messages.dateHeader(new Random(seed)), "Dec 2 2019");
            assertTrue(formatted.contains("Dec 2 2019"));
        }
    }

    @Test
    public void emptyDate_everyVariant_formatsCleanlyWithOnePlaceholder() {
        for (int seed = 0; seed < TRIALS; seed++) {
            String formatted = String.format(Messages.emptyDate(new Random(seed)), "Dec 2 2019");
            assertTrue(formatted.contains("Dec 2 2019"));
        }
    }
}
