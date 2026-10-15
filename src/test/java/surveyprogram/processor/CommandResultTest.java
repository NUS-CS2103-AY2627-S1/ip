package surveyprogram.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests command-result construction and reply categories. */
class CommandResultTest {
    @Test
    void twoArgumentConstructor_validArguments_usesDefaultReplyType() {
        CommandResult result = new CommandResult("goodbye", false);

        assertEquals("goodbye", result.response());
        assertFalse(result.shouldContinue());
        assertEquals(ReplyType.DEFAULT, result.replyType());
    }

    @Test
    void replyTypes_allCategories_exposeExpectedStyleClasses() {
        assertNull(ReplyType.DEFAULT.getStyleClass());
        assertEquals("task-created-label", ReplyType.TASK_CREATED.getStyleClass());
        assertEquals("list-label", ReplyType.LIST.getStyleClass());
        assertEquals("find-label", ReplyType.FIND.getStyleClass());
        assertEquals("mark-completed-label", ReplyType.MARK_COMPLETED.getStyleClass());
        assertEquals("unmarked-label", ReplyType.UNMARKED.getStyleClass());
        assertEquals("deleted-label", ReplyType.DELETED.getStyleClass());
        assertEquals("date-query-label", ReplyType.DATE_QUERY.getStyleClass());
        assertEquals("error-label", ReplyType.ERROR.getStyleClass());
        assertEquals("empty-label", ReplyType.EMPTY.getStyleClass());
    }
}
