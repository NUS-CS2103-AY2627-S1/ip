package jaku;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jaku.storage.Storage;

/** Verifies recurring commands through Jaku's UI-independent command service. */
class RecurringCommandTest {
    /** Verifies recurring todos advance on mark and reverse on unmark. */
    @Test
    void recurringTodo_markAndUnmark_movesOneOccurrence(@TempDir Path temporaryDirectory) {
        Jaku jaku = new Jaku(new Storage(temporaryDirectory.resolve("jaku.txt")));

        assertTrue(jaku.getResponse("repeat todo review notes /from 2026-09-10 /every daily")
                .contains("next: Sep 10 2026"));
        assertTrue(jaku.getResponse("mark 1").contains("next: Sep 11 2026"));
        assertTrue(jaku.getResponse("unmark 1").contains("next: Sep 10 2026"));
    }

    /** Verifies recurring event date-times and ranges are validated. */
    @Test
    void recurringEvent_invalidRange_showsSpecificGuidance(@TempDir Path temporaryDirectory) {
        Jaku jaku = new Jaku(new Storage(temporaryDirectory.resolve("jaku.txt")));

        assertTrue(jaku.getResponse("repeat event meeting /from 2026-09-10 15:00 /to 2026-09-10 14:00 /every weekly")
                .contains("must end after it starts"));
    }

    /** Verifies a saved recurring event is shown after Jaku restarts. */
    @Test
    void recurringEvent_savedAndReloaded_isListed(@TempDir Path temporaryDirectory) {
        Path dataFile = temporaryDirectory.resolve("jaku.txt");
        Jaku firstSession = new Jaku(new Storage(dataFile));
        firstSession.getResponse("repeat event meeting /from 2026-09-10 14:00 /to 2026-09-10 15:00 /every weekly");

        Jaku secondSession = new Jaku(new Storage(dataFile));
        assertTrue(secondSession.getResponse("list").contains("every: weekly"));
    }
}
