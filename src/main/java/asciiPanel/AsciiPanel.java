package asciiPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * This simulates a code page 437 ASCII terminal display.
 * 
 * @author Trystan Spangler
 */
public class AsciiPanel extends JPanel {
    private static final long serialVersionUID = -4167851861147593092L;

    /**
     * The color black (pure black).
     */
    public static Color black = new Color(0, 0, 0);

    /**
     * The color red.
     */
    public static Color red = new Color(128, 0, 0);

    /**
     * The color green.
     */
    public static Color green = new Color(0, 128, 0);

    /**
     * The color yellow.
     */
    public static Color yellow = new Color(128, 128, 0);

    /**
     * The color blue.
     */
    public static Color blue = new Color(0, 0, 128);

    /**
     * The color magenta.
     */
    public static Color magenta = new Color(128, 0, 128);

    /**
     * The color cyan.
     */
    public static Color cyan = new Color(0, 128, 128);

    /**
     * The color white (light gray).
     */
    public static Color white = new Color(192, 192, 192);

    /**
     * A brighter black (dark gray).
     */
    public static Color brightBlack = new Color(128, 128, 128);

    /**
     * A brighter red.
     */
    public static Color brightRed = new Color(255, 0, 0);

    /**
     * A brighter green.
     */
    public static Color brightGreen = new Color(0, 255, 0);

    /**
     * A brighter yellow.
     */
    public static Color brightYellow = new Color(255, 255, 0);

    /**
     * A brighter blue.
     */
    public static Color brightBlue = new Color(0, 0, 255);

    /**
     * A brighter magenta.
     */
    public static Color brightMagenta = new Color(255, 0, 255);

    /**
     * A brighter cyan.
     */
    public static Color brightCyan = new Color(0, 255, 255);

    /**
     * A brighter white (pure white).
     */
    public static Color brightWhite = new Color(255, 255, 255);

    private Image offscreenBuffer;
    private Graphics offscreenGraphics;
    private int widthInCharacters;
    private int heightInCharacters;
    private int charWidth;
    private int charHeight;
    private String terminalFontFile;
    private Color defaultBackgroundColor;
    private Color defaultForegroundColor;
    private int cursorX;
    private int cursorY;
    private BufferedImage glyphSprite;
    private BufferedImage[] charGlyphs;
    private String[][] imageUrls;
    private String[][] oldImageUrls;
    private Hashtable<String, BufferedImage> images;
    private AsciiFont asciiFont;

    /**
     * Gets the height, in pixels, of a character.
     * 
     * @return
     */
    public int getCharHeight() {
        return charHeight;
    }

    /**
     * Gets the width, in pixels, of a character.
     * 
     * @return
     */
    public int getCharWidth() {
        return charWidth;
    }

    /**
     * Gets the height in characters. A standard terminal is 24 characters high.
     * 
     * @return
     */
    public int getHeightInCharacters() {
        return heightInCharacters;
    }

    /**
     * Gets the width in characters. A standard terminal is 80 characters wide.
     * 
     * @return
     */
    public int getWidthInCharacters() {
        return widthInCharacters;
    }

    /**
     * Gets the distance from the left new text will be written to.
     * 
     * @return
     */
    public int getCursorX() {
        return cursorX;
    }

    /**
     * Sets the distance from the left new text will be written to. This should be
     * equal to or greater than 0 and less than the the width in characters.
     * 
     * @param cursorX the distance from the left new text should be written to
     */
    public void setCursorX(int cursorX) {
        if (cursorX < 0 || cursorX >= widthInCharacters)
            throw new IllegalArgumentException(
                    "cursorX " + cursorX + " must be within range [0," + widthInCharacters + ").");

        this.cursorX = cursorX;
    }

    /**
     * Gets the distance from the top new text will be written to.
     * 
     * @return
     */
    public int getCursorY() {
        return cursorY;
    }

    /**
     * Sets the distance from the top new text will be written to. This should be
     * equal to or greater than 0 and less than the the height in characters.
     * 
     * @param cursorY the distance from the top new text should be written to
     */
    public void setCursorY(int cursorY) {
        if (cursorY < 0 || cursorY >= heightInCharacters)
            throw new IllegalArgumentException(
                    "cursorY " + cursorY + " must be within range [0," + heightInCharacters + ").");

        this.cursorY = cursorY;
    }

    /**
     * Sets the x and y position of where new text will be written to. The origin
     * (0,0) is the upper left corner. The x should be equal to or greater than 0
     * and less than the the width in characters. The y should be equal to or
     * greater than 0 and less than the the height in characters.
     * 
     * @param x the distance from the left new text should be written to
     * @param y the distance from the top new text should be written to
     */
    public void setCursorPosition(int x, int y) {
        setCursorX(x);
        setCursorY(y);
    }

    /**
     * Gets the default background color that is used when writing new text.
     * 
     * @return
     */
    public Color getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    /**
     * Sets the default background color that is used when writing new text.
     * 
     * @param defaultBackgroundColor
     */
    public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
        if (defaultBackgroundColor == null)
            throw new NullPointerException("defaultBackgroundColor must not be null.");

        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    /**
     * Gets the default foreground color that is used when writing new text.
     * 
     * @return
     */
    public Color getDefaultForegroundColor() {
        return defaultForegroundColor;
    }

    /**
     * Sets the default foreground color that is used when writing new text.
     * 
     * @param defaultForegroundColor
     */
    public void setDefaultForegroundColor(Color defaultForegroundColor) {
        if (defaultForegroundColor == null)
            throw new NullPointerException("defaultForegroundColor must not be null.");

        this.defaultForegroundColor = defaultForegroundColor;
    }

    /**
     * Gets the currently selected font
     * 
     * @return
     */
    public AsciiFont getAsciiFont() {
        return asciiFont;
    }

    /**
     * Sets the used font. It is advisable to make sure the parent component is
     * properly sized after setting the font as the panel dimensions will most
     * likely change
     * 
     * @param font
     */
    public void setAsciiFont(AsciiFont font) {
        if (this.asciiFont == font) {
            return;
        }
        this.asciiFont = font;

        this.charHeight = font.getHeight();
        this.charWidth = font.getWidth();
        this.terminalFontFile = font.getFontFilename();

        Dimension panelSize = new Dimension(charWidth * widthInCharacters, charHeight * heightInCharacters);
        setPreferredSize(panelSize);

        charGlyphs = new BufferedImage[256];

        offscreenBuffer = new BufferedImage(panelSize.width, panelSize.height, BufferedImage.TYPE_INT_RGB);
        offscreenGraphics = offscreenBuffer.getGraphics();

        loadGlyphs();
    }

    private void loadImages() {
        loadStartImg();
        loadImage("resources/wall/wall0.png");
        loadImage("resources/wall/wall1.png");
        loadImage("resources/wall/wall2.png");
        loadImage("resources/wall/wall3.png");
        loadImage("resources/floor.png");
        loadImage("resources/bean.png");
        loadImage("resources/life_potion.png");
        loadImage("resources/invincible_potion.png");
        loadImage("resources/ghost/direction0.png");
        loadImage("resources/ghost/direction1.png");
        loadImage("resources/ghost/direction2.png");
        loadImage("resources/ghost/direction3.png");
        loadImage("resources/player/0_stand.png");
        loadImage("resources/player/0_walk0.png");
        loadImage("resources/player/0_walk1.png");
        loadImage("resources/player/1_stand.png");
        loadImage("resources/player/1_walk0.png");
        loadImage("resources/player/1_walk1.png");
        loadImage("resources/player/2_stand.png");
        loadImage("resources/player/2_walk0.png");
        loadImage("resources/player/2_walk1.png");
        loadImage("resources/player/3_stand.png");
        loadImage("resources/player/3_walk0.png");
        loadImage("resources/player/3_walk1.png");
        loadImage("resources/arrow/0.png");
        loadImage("resources/arrow/1.png");
        loadImage("resources/arrow/2.png");
        loadImage("resources/arrow/3.png");
    }

    /**
     * Class constructor. Default size is 80x24.
     */
    public AsciiPanel() {
        this(80, 24);
    }

    /**
     * Class constructor specifying the width and height in characters.
     * 
     * @param width
     * @param height
     */
    public AsciiPanel(int width, int height) {
        this(width, height, null);
    }

    /**
     * Class constructor specifying the width and height in characters and the
     * AsciiFont
     * 
     * @param width
     * @param height
     * @param font   if passing null, standard font CP437_9x16 will be used
     */
    public AsciiPanel(int width, int height, AsciiFont font) {
        super();

        if (width < 1) {
            throw new IllegalArgumentException("width " + width + " must be greater than 0.");
        }

        if (height < 1) {
            throw new IllegalArgumentException("height " + height + " must be greater than 0.");
        }

        widthInCharacters = width;
        heightInCharacters = height;

        defaultBackgroundColor = black;
        defaultForegroundColor = white;

        this.imageUrls = new String[widthInCharacters][heightInCharacters];
        this.oldImageUrls = new String[widthInCharacters][heightInCharacters];
        this.images = new Hashtable<>();

        if (font == null) {
            font = AsciiFont.CP437_32x32;
        }
        setAsciiFont(font);
        loadImages();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (g == null)
            throw new NullPointerException();

        for (int x = 0; x < widthInCharacters; x++) {
            for (int y = 0; y < heightInCharacters; y++) {
                if (oldImageUrls[x][y] == imageUrls[x][y])
                    continue;
                
                String url = imageUrls[x][y];
                if (!images.containsKey(url)) {
                    loadImage(url);
                }
                BufferedImage img = images.get(url);
                if (img == null) {
                    System.out.println("img is null:" + url);
                }
                offscreenGraphics.drawImage(img, x * charWidth, y * charHeight, null);
                oldImageUrls[x][y] = imageUrls[x][y];
            }
        }

        g.drawImage(offscreenBuffer, 0, 0, this);
    }

    private void loadGlyphs() {
        try {
            glyphSprite = ImageIO.read(AsciiPanel.class.getClassLoader().getResource(terminalFontFile));
        } catch (IOException e) {
            System.err.println("loadGlyphs(): " + e.getMessage());
        }

        for (int i = 0; i < 256; i++) {
            int sx = (i % 16) * charWidth;
            int sy = (i / 16) * charHeight;

            charGlyphs[i] = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
            charGlyphs[i].getGraphics().drawImage(glyphSprite, 0, 0, charWidth, charHeight, sx, sy, sx + charWidth,
                    sy + charHeight, null);
        }
    }

    private void loadStartImg() {
        try {
            glyphSprite = ImageIO.read(AsciiPanel.class.getClassLoader().getResource("resources/start.png"));
        } catch (IOException e) {
            System.err.println("loadGlyphs(): " + e.getMessage());
        }

        for (int i = 0; i < 20; i++) {
            int sy = i * charHeight;

            for (int j = 0; j < 24; j++) {
                int sx = j * charWidth;                                

                BufferedImage img = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
                img.getGraphics().drawImage(glyphSprite, 0, 0, charWidth, charHeight, sx, sy, sx + charWidth,
                sy + charHeight, null);
                images.put("(" + j + "," + i + ")", img);
            }
        }
    }

    /**
     * Create a <code>LookupOp</code> object (lookup table) mapping the original
     * pixels to the background and foreground colors, respectively.
     * 
     * @param bgColor the background color
     * @param fgColor the foreground color
     * @return the <code>LookupOp</code> object (lookup table)
     */
    private LookupOp setColors(Color bgColor, Color fgColor) {
        short[] a = new short[256];
        short[] r = new short[256];
        short[] g = new short[256];
        short[] b = new short[256];

        byte bga = (byte) (bgColor.getAlpha());
        byte bgr = (byte) (bgColor.getRed());
        byte bgg = (byte) (bgColor.getGreen());
        byte bgb = (byte) (bgColor.getBlue());

        byte fga = (byte) (fgColor.getAlpha());
        byte fgr = (byte) (fgColor.getRed());
        byte fgg = (byte) (fgColor.getGreen());
        byte fgb = (byte) (fgColor.getBlue());

        for (int i = 0; i < 256; i++) {
            if (i == 0) {
                a[i] = bga;
                r[i] = bgr;
                g[i] = bgg;
                b[i] = bgb;
            } else {
                a[i] = fga;
                r[i] = fgr;
                g[i] = fgg;
                b[i] = fgb;
            }
        }

        short[][] table = { r, g, b, a };
        return new LookupOp(new ShortLookupTable(0, table), null);
    }

    /**
     * Clear the entire screen to whatever the default background color is.
     * 
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear() {
        return clear(' ', 0, 0, widthInCharacters, heightInCharacters, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Clear the entire screen with the specified character and whatever the default
     * foreground and background colors are.
     * 
     * @param character the character to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        return clear(character, 0, 0, widthInCharacters, heightInCharacters, defaultForegroundColor,
                defaultBackgroundColor);
    }

    /**
     * Clear the entire screen with the specified character and whatever the
     * specified foreground and background colors are.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, Color foreground, Color background) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        return clear(character, 0, 0, widthInCharacters, heightInCharacters, foreground, background);
    }

    /**
     * Clear the section of the screen with the specified character and whatever the
     * default foreground and background colors are. The cursor position will not be
     * modified.
     * 
     * @param character the character to write
     * @param x         the distance from the left to begin writing from
     * @param y         the distance from the top to begin writing from
     * @param width     the height of the section to clear
     * @param height    the width of the section to clear
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, int x, int y, int width, int height) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ").");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ").");

        if (width < 1)
            throw new IllegalArgumentException("width " + width + " must be greater than 0.");

        if (height < 1)
            throw new IllegalArgumentException("height " + height + " must be greater than 0.");

        if (x + width > widthInCharacters)
            throw new IllegalArgumentException(
                    "x + width " + (x + width) + " must be less than " + (widthInCharacters + 1) + ".");

        if (y + height > heightInCharacters)
            throw new IllegalArgumentException(
                    "y + height " + (y + height) + " must be less than " + (heightInCharacters + 1) + ".");

        return clear(character, x, y, width, height, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Clear the section of the screen with the specified character and whatever the
     * specified foreground and background colors are.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param width      the height of the section to clear
     * @param height     the width of the section to clear
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, int x, int y, int width, int height, Color foreground, Color background) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        if (width < 1)
            throw new IllegalArgumentException("width " + width + " must be greater than 0.");

        if (height < 1)
            throw new IllegalArgumentException("height " + height + " must be greater than 0.");

        if (x + width > widthInCharacters)
            throw new IllegalArgumentException(
                    "x + width " + (x + width) + " must be less than " + (widthInCharacters + 1) + ".");

        if (y + height > heightInCharacters)
            throw new IllegalArgumentException(
                    "y + height " + (y + height) + " must be less than " + (heightInCharacters + 1) + ".");

        int originalCursorX = cursorX;
        int originalCursorY = cursorY;
        for (int xo = x; xo < x + width; xo++) {
            for (int yo = y; yo < y + height; yo++) {
                write(character, xo, yo, foreground, background);
            }
        }
        cursorX = originalCursorX;
        cursorY = originalCursorY;

        return this;
    }

    /**
     * Write a character to the cursor's position. This updates the cursor's
     * position.
     * 
     * @param character the character to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character) {
        if (character < 0 || character > charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        return write(character, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a character to the cursor's position with the specified foreground
     * color. This updates the cursor's position but not the default foreground
     * color.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, Color foreground) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        return write(character, cursorX, cursorY, foreground, defaultBackgroundColor);
    }

    /**
     * Write a character to the cursor's position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, Color foreground, Color background) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        return write(character, cursorX, cursorY, foreground, background);
    }

    /**
     * Write a character to the specified position. This updates the cursor's
     * position.
     * 
     * @param character the character to write
     * @param x         the distance from the left to begin writing from
     * @param y         the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        return write(character, x, y, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a character to the specified position with the specified foreground
     * color. This updates the cursor's position but not the default foreground
     * color.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y, Color foreground) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        return write(character, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a character to the specified position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y, Color foreground, Color background) {
        if (character < 0 || character >= charGlyphs.length)
            throw new IllegalArgumentException(
                    "character " + character + " must be within range [0," + charGlyphs.length + "].");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        if (foreground == null)
            foreground = defaultForegroundColor;
        if (background == null)
            background = defaultBackgroundColor;

        String url = String.valueOf(character);
        imageUrls[x][y] = url;
        if (!images.containsKey(url)) {
            LookupOp op = setColors(background, foreground);
            BufferedImage img = op.filter(charGlyphs[character], null);
            images.put(url, img);
        }        
        cursorX = x + 1;
        cursorY = y;
        return this;
    }

    private void loadImage(String url) {
        try {
            BufferedImage img = ImageIO.read(AsciiPanel.class.getClassLoader().getResource(url));
            images.put(url, img);            
        }
        catch (Exception e) {            
            System.out.println("glypy load error: " + e.getMessage());            
        }             
    }

    public AsciiPanel write(String url, int x, int y) {
        if (x < 0 || x >= widthInCharacters) {
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");
        }
            
        if (y < 0 || y >= heightInCharacters) {
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");
        }
            
        imageUrls[x][y] = url;     
        cursorX = x + 1;
        cursorY = y;
        return this;
    }

    /**
     * Write a string to the cursor's position. This updates the cursor's position.
     * 
     * @param string the string to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (cursorX + string.length() > widthInCharacters)
            throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length())
                    + " must be less than " + widthInCharacters + ".");

        return write(string, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a string to the cursor's position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * 
     * @param string     the string to write
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, Color foreground) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (cursorX + string.length() > widthInCharacters)
            throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length())
                    + " must be less than " + widthInCharacters + ".");

        return write(string, cursorX, cursorY, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the cursor's position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param string     the string to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, Color foreground, Color background) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (cursorX + string.length() > widthInCharacters)
            throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + string.length())
                    + " must be less than " + widthInCharacters + ".");

        return write(string, cursorX, cursorY, foreground, background);
    }

    /**
     * Write a string to the specified position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * 
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, int x, int y, Color foreground) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (x + string.length() > widthInCharacters)
            throw new IllegalArgumentException(
                    "x + string.length() " + (x + string.length()) + " must be less than " + widthInCharacters + ".");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ")");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        return write(string, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the specified position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, int x, int y, Color foreground, Color background) {
        if (string == null)
            throw new NullPointerException("string must not be null.");

        if (x + string.length() > widthInCharacters)
            throw new IllegalArgumentException(
                    "x + string.length() " + (x + string.length()) + " must be less than " + widthInCharacters + ".");

        if (x < 0 || x >= widthInCharacters)
            throw new IllegalArgumentException("x " + x + " must be within range [0," + widthInCharacters + ").");

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ").");

        if (foreground == null)
            foreground = defaultForegroundColor;

        if (background == null)
            background = defaultBackgroundColor;

        for (int i = 0; i < string.length(); i++) {
            write(string.charAt(i), x + i, y, foreground, background);
        }
        return this;
    }

    /**
     * Write a string to the center of the panel at the specified y position. This
     * updates the cursor's position.
     * 
     * @param string the string to write
     * @param y      the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (string.length() > widthInCharacters)
            throw new IllegalArgumentException(
                    "string.length() " + string.length() + " must be less than " + widthInCharacters + ".");

        int x = (widthInCharacters - string.length()) / 2;

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        return write(string, x, y, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a string to the center of the panel at the specified y position with
     * the specified foreground color. This updates the cursor's position but not
     * the default foreground color.
     * 
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y, Color foreground) {
        if (string == null)
            throw new NullPointerException("string must not be null");

        if (string.length() > widthInCharacters)
            throw new IllegalArgumentException(
                    "string.length() " + string.length() + " must be less than " + widthInCharacters + ".");

        int x = (widthInCharacters - string.length()) / 2;

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ")");

        return write(string, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the center of the panel at the specified y position with
     * the specified foreground and background colors. This updates the cursor's
     * position but not the default foreground or background colors.
     * 
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y, Color foreground, Color background) {
        if (string == null)
            throw new NullPointerException("string must not be null.");

        if (string.length() > widthInCharacters)
            throw new IllegalArgumentException(
                    "string.length() " + string.length() + " must be less than " + widthInCharacters + ".");

        int x = (widthInCharacters - string.length()) / 2;

        if (y < 0 || y >= heightInCharacters)
            throw new IllegalArgumentException("y " + y + " must be within range [0," + heightInCharacters + ").");

        if (foreground == null)
            foreground = defaultForegroundColor;

        if (background == null)
            background = defaultBackgroundColor;

        for (int i = 0; i < string.length(); i++) {
            write(string.charAt(i), x + i, y, foreground, background);
        }
        return this;
    }

/*
    public void withEachTile(TileTransformer transformer) {
        withEachTile(0, 0, widthInCharacters, heightInCharacters, transformer);
    }

    public void withEachTile(int left, int top, int width, int height, TileTransformer transformer) {
        AsciiCharacterData data = new AsciiCharacterData();

        for (int x0 = 0; x0 < width; x0++)
            for (int y0 = 0; y0 < height; y0++) {
                int x = left + x0;
                int y = top + y0;

                if (x < 0 || y < 0 || x >= widthInCharacters || y >= heightInCharacters)
                    continue;

                data.character = chars[x][y];
                data.foregroundColor = foregroundColors[x][y];
                data.backgroundColor = backgroundColors[x][y];

                transformer.transformTile(x, y, data);

                chars[x][y] = data.character;
                foregroundColors[x][y] = data.foregroundColor;
                backgroundColors[x][y] = data.backgroundColor;
            }
    }
*/    
}
