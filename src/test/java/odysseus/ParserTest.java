package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests parsing of every supported task and learning command shape. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_validCommands_createExpectedRenderedTasks() throws Exception {
        assertEquals("[T][ ] mend sail", parser.parseTask("todo mend sail").toString());
        assertEquals("[D][ ] return home (by: Dec 25 2026)",
                parser.parseTask("deadline return home /by 2026-12-25 18:00").toString());
        assertEquals("[E][ ] meet (from: dawn to: dusk)",
                parser.parseTask("event meet /from dawn /to dusk").toString());
    }

    @Test
    void parseTask_invalidCommands_reportRecoverableErrors() {
        assertThrows(OdysseusException.class, () -> parser.parseTask("todo"));
        assertThrows(OdysseusException.class, () -> parser.parseTask("deadline return"));
        assertThrows(OdysseusException.class, () -> parser.parseTask("deadline return /by yesterday"));
        assertThrows(OdysseusException.class, () -> parser.parseTask("event meet /to dusk /from dawn"));
        assertThrows(OdysseusException.class, () -> parser.parseTask("unknown"));
    }

    @Test
    void parseLearningCard_topicsAndAnswers_areValidated() throws Exception {
        LearningCard defaultTopic = parser.parseLearningCard("learn nostos /answer homecoming");
        LearningCard namedTopic = parser.parseLearningCard("learn bird /answer owl /topic Symbols");

        assertEquals("Odyssey", defaultTopic.getTopic());
        assertEquals("Symbols", namedTopic.getTopic());
        assertThrows(OdysseusException.class, () -> parser.parseLearningCard("learn prompt"));
        assertThrows(OdysseusException.class, () -> parser.parseLearningCard("learn /answer answer /topic "));
    }

    @Test
    void scalarParsers_trimValuesAndRejectMissingNumbers() throws Exception {
        assertEquals("answer", parser.parseAnswer("answer  answer  "));
        assertEquals("topic", parser.parseOptionalTopic("cards topic", "cards"));
        assertEquals(2, parser.parseCardNumber("forget 2"));
        assertEquals(3, parser.parseTaskNumber("mark 3", TaskAction.MARK));
        assertEquals("book", parser.parseFindKeyword("find book"));
        assertThrows(OdysseusException.class, () -> parser.parseAnswer("answer"));
        assertThrows(OdysseusException.class, () -> parser.parseCardNumber("forget two"));
        assertThrows(OdysseusException.class, () -> parser.parseTaskNumber("delete", TaskAction.DELETE));
        assertThrows(OdysseusException.class, () -> parser.parseFindKeyword("find"));
    }
}
