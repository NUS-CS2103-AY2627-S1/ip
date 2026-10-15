package cheecken;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Runs the real console loop at a fixed Monday afternoon for repeatable acceptance tests.
 */
public class FixedClockCli {
    /**
     * Starts an isolated console session using the working directory's task file.
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        Clock clock = Clock.fixed(Instant.parse("2026-09-07T15:30:00Z"), ZoneOffset.UTC);
        new Cheecken("data/cheecken.txt", clock).run();
    }
}
