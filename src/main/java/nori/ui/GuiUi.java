package nori.ui;

/**
 * Captures Nori responses so that a graphical interface can display them.
 */
public final class GuiUi extends Ui {
    /** Joins the lines of one response into the single block a bubble shows. */
    private static final String LINE_SEPARATOR = "\n";

    /** The most recent command response that has not been displayed. */
    private String pendingResponse = "";
    /** Whether the most recent response reported something Nori could not do. */
    private boolean isErrorResponse;

    /** Creates a user interface that holds each response until it is consumed. */
    public GuiUi() {
    }

    /** {@inheritDoc} */
    @Override
    public void showResponse(String... lines) {
        pendingResponse = String.join(LINE_SEPARATOR, lines);
        isErrorResponse = false;
    }

    /** {@inheritDoc} */
    @Override
    public void showError(String... lines) {
        pendingResponse = String.join(LINE_SEPARATOR, lines);
        isErrorResponse = true;
    }

    /**
     * Returns and clears the pending response.
     *
     * @return the response produced by the latest command.
     */
    public String consumeResponse() {
        String response = pendingResponse;
        pendingResponse = "";
        return response;
    }

    /**
     * Returns whether the most recent response reported something Nori could not do.
     *
     * This describes the response last shown, and consuming that response does
     * not change it, so a caller may read the text and the kind of response in
     * either order.
     *
     * @return {@code true} when the latest response was an error.
     */
    public boolean isErrorResponse() {
        return isErrorResponse;
    }
}
