package chai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests command keyword classification. */
public class CommandTypeTest {

    @Test
    public void fromKeyword_allKnownLowercaseKeywords_returnsMatchingType() {
        for (CommandType type : CommandType.values()) {
            if (type != CommandType.UNKNOWN) {
                String keyword = type.name().toLowerCase(Locale.ROOT);
                assertEquals(type, CommandType.fromKeyword(keyword));
            }
        }
    }

    @Test
    public void fromKeyword_mixedCaseKeyword_returnsMatchingType() {
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("DeAdLiNe"));
    }

    @Test
    public void fromKeyword_unrecognisedOrEmptyKeyword_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("remind"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword(""));
    }
}
