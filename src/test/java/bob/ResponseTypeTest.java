package bob;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests conversion of response types into presentation style classes. */
class ResponseTypeTest {

    @Test
    void getStyleClass_allResponseTypes_returnsLowercaseCssClass() {
        assertAll(
                () -> assertEquals("response-add", ResponseType.ADD.getStyleClass()),
                () -> assertEquals("response-bye", ResponseType.BYE.getStyleClass()),
                () -> assertEquals("response-delete", ResponseType.DELETE.getStyleClass()),
                () -> assertEquals("response-error", ResponseType.ERROR.getStyleClass()),
                () -> assertEquals("response-find", ResponseType.FIND.getStyleClass()),
                () -> assertEquals("response-info", ResponseType.INFO.getStyleClass()),
                () -> assertEquals("response-list", ResponseType.LIST.getStyleClass()),
                () -> assertEquals("response-mark", ResponseType.MARK.getStyleClass()),
                () -> assertEquals("response-unmark", ResponseType.UNMARK.getStyleClass()));
    }
}
