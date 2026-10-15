package peter.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class CommandTest {
    @Test
    public void fromString_nullOrBlank_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromString(null));
        assertEquals(Command.UNKNOWN, Command.fromString(""));
        assertEquals(Command.UNKNOWN, Command.fromString(" \t\n "));
    }

    @Test
    public void fromString_knownCommandWithMixedCaseAndSpacing_returnsCommand() {
        assertEquals(Command.DEADLINE, Command.fromString("  DeAdLiNe   submit report  "));
        assertEquals(Command.BYE, Command.fromString("BYE"));
    }

    @Test
    public void fromString_unknownAndPrefixOnly_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromString("dance now"));
        assertEquals(Command.UNKNOWN, Command.fromString("listing"));
    }

    @Test
    public void fromString_turkishDefaultLocale_stillRecognizesFind() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertEquals(Command.FIND, Command.fromString("find book"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
