package kenbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import kenbot.KenbotException;

/**
 * Tests {@link Tag}, which decides what counts as a tag and where one may
 * appear in a command.
 *
 * <p>Test names read as {@code method_input_expectedResult}.</p>
 */
public class TagTest {

    /** Returns the names of the tags split off the given argument, in order. */
    private static List<String> tagNamesIn(String argument) throws KenbotException {
        return Tag.splitTrailingTags(argument).tags().stream().map(Tag::getName).toList();
    }

    @Test
    public void of_plainName_keepsIt() throws KenbotException {
        assertEquals("fun", Tag.of("fun").getName());
    }

    @Test
    public void of_leadingHash_removesIt() throws KenbotException {
        assertEquals("fun", Tag.of("#fun").getName());
    }

    @Test
    public void of_secondHash_keepsIt() throws KenbotException {
        // Only one # is punctuation; a second one is part of the name.
        assertEquals("#fun", Tag.of("##fun").getName());
    }

    @Test
    public void toString_always_putsTheHashBack() throws KenbotException {
        assertEquals("#fun", Tag.of("fun").toString());
    }

    @Test
    public void of_punctuation_isAllowed() throws KenbotException {
        assertEquals("c++", Tag.of("c++").getName());
    }

    @Test
    public void of_blank_throws() {
        assertThrows(KenbotException.class, () -> Tag.of("   "));
    }

    @Test
    public void of_hashAlone_throws() {
        assertThrows(KenbotException.class, () -> Tag.of("#"));
    }

    @Test
    public void of_nameWithASpace_throws() {
        assertThrows(KenbotException.class, () -> Tag.of("school work"));
    }

    @Test
    public void of_nameWithABar_throwsBecauseItSeparatesSavedFields() {
        assertThrows(KenbotException.class, () -> Tag.of("a|b"));
    }

    @Test
    public void equals_sameName_areTheSameTag() throws KenbotException {
        assertEquals(Tag.of("fun"), Tag.of("#fun"));
    }

    @Test
    public void equals_differingOnlyInCase_areDifferentTags() throws KenbotException {
        assertFalse(Tag.of("Fun").equals(Tag.of("fun")));
    }

    @Test
    public void hashCode_equalTags_match() throws KenbotException {
        assertEquals(Tag.of("fun").hashCode(), Tag.of("fun").hashCode());
    }

    @Test
    public void splitTrailingTags_noTags_returnsTheWholeText() throws KenbotException {
        Tag.TaggedText split = Tag.splitTrailingTags("read book");
        assertEquals("read book", split.text());
        assertTrue(split.tags().isEmpty());
    }

    @Test
    public void splitTrailingTags_trailingTags_removesThemInOrder() throws KenbotException {
        Tag.TaggedText split = Tag.splitTrailingTags("read book #fun #cs2103");
        assertEquals("read book", split.text());
        assertEquals(List.of("fun", "cs2103"), tagNamesIn("read book #fun #cs2103"));
        assertEquals(2, split.tags().size());
    }

    @Test
    public void splitTrailingTags_hashInTheMiddle_staysInTheDescription() throws KenbotException {
        // The run of tags stops at the first word that is not one, so #1 here is
        // part of what the user is describing rather than a label on it.
        Tag.TaggedText split = Tag.splitTrailingTags("read #1 book");
        assertEquals("read #1 book", split.text());
        assertTrue(split.tags().isEmpty());
    }

    @Test
    public void splitTrailingTags_hashInTheMiddleAndAtTheEnd_splitsOnlyTheEnd()
            throws KenbotException {
        Tag.TaggedText split = Tag.splitTrailingTags("read #1 book #fun");
        assertEquals("read #1 book", split.text());
        assertEquals(List.of("fun"), tagNamesIn("read #1 book #fun"));
    }

    @Test
    public void splitTrailingTags_repeatedTag_keepsOneCopy() throws KenbotException {
        assertEquals(List.of("fun"), tagNamesIn("read book #fun #fun"));
    }

    @Test
    public void splitTrailingTags_onlyTags_leavesNoText() throws KenbotException {
        assertEquals("", Tag.splitTrailingTags("#fun").text());
    }

    @Test
    public void splitTrailingTags_unusableTrailingTag_throws() {
        assertThrows(KenbotException.class, () -> Tag.splitTrailingTags("read book #"));
    }
}
