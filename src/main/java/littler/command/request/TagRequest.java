package littler.command.request;

/**
 * Encapsulates a fully parsed tag/untag request: which task to affect, and the tag text.
 */
public final class TagRequest {
    private final int index;
    private final String tag;

    /**
     * Constructs a new TagRequest with the specified task index and tag text.
     *
     * @param index the 1-based index of the task to tag/untag
     * @param tag the tag text to add or remove
     */
    public TagRequest(int index, String tag) {
        this.index = index;
        this.tag = tag;
    }

    /**
     * Returns the 1-based index of the task to tag/untag.
     *
     * @return the task index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the tag text to add or remove.
     *
     * @return the tag text
     */
    public String getTag() {
        return tag;
    }
}
