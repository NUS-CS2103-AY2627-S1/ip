package luckynoslacky.luckyresponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests non-task-specific user-visible quips. */
class LuckyNoQuipsTest {
    /** Verifies the dedicated decimal calendar duration message. */
    @Test
    void decimalCalendarDurationMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Paiseh bro... i cannot settle decimal values for years and months yet...",
                LuckyNoQuips.decimalCalendarDurationMessage());
    }

    /** Verifies invalid duration messages quote the supplied input. */
    @Test
    void invalidDurationMessage_durationText_returnsQuotedMessage() {
        assertEquals(
                "Eh can you be more specific anot, what do you mean by \"1h30min\" sia?",
                LuckyNoQuips.invalidDurationMessage("1h30min"));
    }

    /** Verifies the configured snooze-duration overflow message. */
    @Test
    void snoozeOverflowMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Siao ah delay so long, by that time your great grandson also die already la! "
                        + "Can be more reasonable anot!",
                LuckyNoQuips.snoozeOverflowMessage());
    }

    /** Verifies that the banner retains its multiline layout. */
    @Test
    void banner_multipleLines_preservesExpectedLayout() {
        assertEquals(
                "     .--\"\"\"\"\"--.\n"
                        + "   /  /^\\   /^\\  \\\n"
                        + "  |  .---------.  |\n"
                        + "  |  | | | | | |  |\n"
                        + "   \\ '---------' /\n"
                        + "     '-._____.-'\n"
                        + "    [NO SLACKING]\n"
                        + "  LuckyNoSlacky is here to help!",
                LuckyNoQuips.banner());
    }

    /** Verifies the configured loading error message. */
    @Test
    void loadErrorMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Eh you so free ah, no tasks were loaded! "
                        + "If you think this is salah, check your task data file.",
                LuckyNoQuips.loadErrorMessage());
    }

    /** Verifies the configured saving error message. */
    @Test
    void saveErrorMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Honggan la your system abit rabs ah, I cannot save your task",
                LuckyNoQuips.saveErrorMessage());
    }

    /** Verifies the configured task-capacity error message. */
    @Test
    void taskLimitMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Eh your task list too full already lah! Finish or delete "
                        + "some tasks before adding more.",
                LuckyNoQuips.taskLimitMessage());
    }

    /** Verifies the configured invalid-configuration error message. */
    @Test
    void configurationErrorMessage_noArguments_returnsConfiguredMessage() {
        assertEquals(
                "Eh your configured clock not making sense lah! Remove or fix "
                        + "`luckynoslacky.fixedNow` and try again.",
                LuckyNoQuips.configurationErrorMessage());
    }
}
