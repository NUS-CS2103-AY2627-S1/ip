package bambolino;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import bambolino.parser.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Checks command normalization without changing argument text. */
class ParserTest {
    @TestFactory
    Stream<DynamicTest> parse_aliases_resolveFullNames() {
        List<String> aliases = List.of("t", "d", "e", "l", "f", "del", "m", "um", "q");
        List<String> names = List.of("todo", "deadline", "event", "list", "find",
                "delete", "mark", "unmark", "bye");
        return aliases.stream().map(alias -> DynamicTest.dynamicTest(alias, () -> {
            Parser.Command parsed = new Parser().parse("  " + alias.toUpperCase(Locale.ROOT) + "\t Book  title  ");
            assertEquals(names.get(aliases.indexOf(alias)), parsed.name());
            assertEquals("Book  title", parsed.arguments());
        }));
    }

    @Test
    void parse_emptyAndUnknown_preservesMeaning() {
        assertEquals(new Parser.Command("", ""), new Parser().parse(" \t "));
        assertEquals(new Parser.Command("unknown", "Hello"), new Parser().parse("UNKNOWN Hello"));
    }

    @Test
    void parse_turkishLocale_normalizesCommand() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("find", new Parser().parse("FIND book").name());
        } finally {
            Locale.setDefault(original);
        }
    }
}
