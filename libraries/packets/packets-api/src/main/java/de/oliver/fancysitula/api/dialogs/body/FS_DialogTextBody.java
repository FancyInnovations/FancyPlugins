package de.oliver.fancysitula.api.dialogs.body;

public class FS_DialogTextBody implements FS_DialogBody {

    private String text;
    private int width;

    public FS_DialogTextBody(String text, int width) {
        this.text = text;
        this.width = width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
