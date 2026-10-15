package littler.command.request;

import java.util.List;

/**
 * Encapsulates a parsed mass-operation request: one or more target task indices,
 * plus a single trailing value (e.g. a tag or priority level) applied to all of them.
 */
public final class IndicesAndValue {
    private final List<Integer> indices;
    private final String value;

    /**
     * Constructs a new IndicesAndValue instance with the specified indices and value.
     *
     * @param indices the list of task indices to be modified
     * @param value the value to be applied to the specified tasks
     */
    public IndicesAndValue(List<Integer> indices, String value) {
        this.indices = indices;
        this.value = value;
    }

    /**
     * Returns the list of task indices to be modified.
     *
     * @return the list of task indices
     */
    public List<Integer> getIndices() {
        return indices;
    }

    /**
     * Returns the value to be applied to the specified tasks.
     *
     * @return the value to be applied
     */
    public String getValue() {
        return value;
    }
}
