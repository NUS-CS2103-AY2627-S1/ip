package panda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EnumTest {

    @Test
    void getIcon_eachTaskType_returnsExpectedIcon() {
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
    }

    @Test
    void getIcon_eachTaskStatus_returnsExpectedIcon() {
        assertEquals(" ", TaskStatus.NOT_DONE.getIcon());
        assertEquals("X", TaskStatus.DONE.getIcon());
    }

    @Test
    void getOpposite_eachSortDirection_returnsOppositeDirection() {
        assertEquals(SortDirection.DESCENDING, SortDirection.ASCENDING.getOpposite());
        assertEquals(SortDirection.ASCENDING, SortDirection.DESCENDING.getOpposite());
    }

    @Test
    void getDisplayName_eachSortDirection_returnsExpectedLabel() {
        assertEquals("Ascending", SortDirection.ASCENDING.getDisplayName());
        assertEquals("Descending", SortDirection.DESCENDING.getDisplayName());
    }
}
