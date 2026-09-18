package cookie.task;

/** Identifies a task property that can be used to create a sorted view. */
public enum SortCriterion {
    /** Sorts tasks alphabetically by description. */
    DESCRIPTION,
    /** Sorts deadlines by due value and events by start value. */
    DATE
}
