package asciiPanel;

/**
 * This class holds provides all available Fonts for the AsciiPanel. Some
 * graphics are from the Dwarf Fortress Tileset Wiki Page
 * 
 * @author zn80
 *
 */
public class AsciiFont {

/*
    public static final AsciiFont CP437_8x8 = new AsciiFont("resources/font/cp437_8x8.png", 8, 8);
    public static final AsciiFont CP437_10x10 = new AsciiFont("resources/font/cp437_10x10.png", 10, 10);
    public static final AsciiFont CP437_12x12 = new AsciiFont("resources/font/cp437_12x12.png", 12, 12);
    public static final AsciiFont CP437_16x16 = new AsciiFont("resources/font/cp437_16x16.png", 16, 16);
    public static final AsciiFont CP437_9x16 = new AsciiFont("resources/font/cp437_9x16.png", 9, 16);
    public static final AsciiFont DRAKE_10x10 = new AsciiFont("resources/font/drake_10x10.png", 10, 10);
    public static final AsciiFont TAFFER_10x10 = new AsciiFont("resources/font/taffer_10x10.png", 10, 10);
    public static final AsciiFont QBICFEET_10x10 = new AsciiFont("resources/font/qbicfeet_10x10.png", 10, 10);
    public static final AsciiFont TALRYTH_15x15 = new AsciiFont("resources/font/talryth_square_15x15.png", 15, 15);
*/
    public static final AsciiFont CP437_32x32 = new AsciiFont("resources/font/cp437_32x32.png", 32, 32);

    private String fontFilename;

    public String getFontFilename() {
        return fontFilename;
    }

    private int width;

    public int getWidth() {
        return width;
    }

    private int height;

    public int getHeight() {
        return height;
    }

    public AsciiFont(String filename, int width, int height) {
        this.fontFilename = filename;
        this.width = width;
        this.height = height;
    }
}