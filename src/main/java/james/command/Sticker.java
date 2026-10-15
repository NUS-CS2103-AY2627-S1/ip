package james.command;

/**
 * Defines the packaged stickers available for command replies.
 */
public enum Sticker {
    NANKORE("/images/nankore_pandorobou.png"),
    OTSU("/images/otsu_pandorobou.png"),
    NAISU("/images/naisu_pandorobou.png"),
    GOMEN("/images/gomen_pandorobou.png"),
    YATTA("/images/yatta_pandorobou.png");

    private final String resourcePath;

    Sticker(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
