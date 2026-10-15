package littler.command.sort;

/**
 * Encapsulates a fully parsed sort request: which criteria to sort by, and in which order.
 */
public final class SortRequest {

    public static final String BY_DELIMITER = "/by";
    public static final String ORDER_DELIMITER = "/order";

    private final SortCriteria criteria;
    private final SortOrder order;

    /**
     * Constructs a SortRequest with the specified criteria and order.
     *
     * @param criteria the criteria by which to sort tasks
     * @param order the order in which to sort tasks
     */
    public SortRequest(SortCriteria criteria, SortOrder order) {
        this.criteria = criteria;
        this.order = order;
    }

    /**
     * Returns the criteria by which to sort tasks.
     *
     * @return the sort criteria
     */
    public SortCriteria getCriteria() {
        return criteria;
    }

    /**
     * Returns the order in which to sort tasks.
     *
     * @return the sort order
     */
    public SortOrder getOrder() {
        return order;
    }
}
