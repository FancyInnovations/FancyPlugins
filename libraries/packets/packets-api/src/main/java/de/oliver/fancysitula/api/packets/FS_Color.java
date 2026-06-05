package de.oliver.fancysitula.api.packets;

public enum FS_Color {
    BLACK(0, '0', "black"),
    DARK_BLUE(1, '1', "dark_blue"),
    DARK_GREEN(2, '2', "dark_green"),
    DARK_AQUA(3, '3', "dark_aqua"),
    DARK_RED(4, '4', "dark_red"),
    DARK_PURPLE(5, '5', "dark_purple"),
    GOLD(6, '6', "gold"),
    GRAY(7, '7', "gray"),
    DARK_GRAY(8, '8', "dark_gray"),
    BLUE(9, '9', "blue"),
    GREEN(10, 'a', "green"),
    AQUA(11, 'b', "aqua"),
    RED(12, 'c', "red"),
    LIGHT_PURPLE(13, 'd', "light_purple"),
    YELLOW(14, 'e', "yellow"),
    WHITE(15, 'f', "white");


    private final int id;
    private final char code;
    private final String name;

    FS_Color(int id, char code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public char getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
