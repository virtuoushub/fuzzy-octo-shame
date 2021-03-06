package org.newdawn.slick;

import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A sprite sheet packed and defined by the Pacific Software Image Packer available
 * from:
 * 
 * http://homepage.ntlworld.com/config/imagepacker/
 *
 * @author kevin
 */
class PackedSpriteSheet {
    /** The image loaded for the sheet */
    private Image image;
    /** The base path where the image is expected to be found based on the original definition file */
    private final String basePath;
    /** The section definitions */
    @Nonnull
    private final Map<String, Section> sections = new HashMap<>();
    /** The filter used when loading the image */
    private int filter = Image.FILTER_NEAREST;

    /**
     * Create a new packed sprite sheet based on a ImagePacker definition file
     *
     * @param def The location of the definition file to read
     * @throws SlickException Indicates a failure to read the definition file
     */
    public PackedSpriteSheet(String def) throws SlickException {
        this(def, null);
    }

    /**
     * Create a new packed sprite sheet based on a ImagePacker definition file
     *
     * @param def The location of the definition file to read
     * @param trans The color to be treated as transparent
     * @throws SlickException Indicates a failure to read the definition file
     */
    private PackedSpriteSheet(String def, @Nullable Color trans) throws SlickException {
        def = def.replace('\\', '/');
        basePath = def.substring(0,def.lastIndexOf("/")+1);

        loadDefinition(def, trans);
    }

    /**
     * Create a new packed sprite sheet based on a ImagePacker definition file
     *
     * @param def The location of the definition file to read
     * @param filter The image filter to use when loading the packed sprite image
     * @throws SlickException Indicates a failure to read the definition file
     */
    public PackedSpriteSheet(String def, int filter) throws SlickException {
        this(def, filter, null);
    }

    /**
     * Create a new packed sprite sheet based on a ImagePacker definition file
     *
     * @param def The location of the definition file to read
     * @param filter The image filter to use when loading the packed sprite image
     * @param trans The color to be treated as transparent
     * @throws SlickException Indicates a failure to read the definition file
     */
    private PackedSpriteSheet(String def, int filter, @Nullable Color trans) throws SlickException {
        this.filter = filter;

        def = def.replace('\\', '/');
        basePath = def.substring(0,def.lastIndexOf("/")+1);

        loadDefinition(def, trans);
    }

    /**
     * Get the full image contaning all the sprites/sections
     *
     * @return The full image containing all the sprites/sections
     */
    public Image getFullImage() {
        return image;
    }

    /**
     * Get a single named sprite from the sheet
     *
     * @param name The name of the sprite to retrieve
     * @return The sprite requested (image of)
     */
    @Nonnull
    Image getSprite(String name) {
        Section section = sections.get(name);

        if (section == null) {
            throw new RuntimeException("Unknown sprite from packed sheet: "+name);
        }

        return image.getSubImage(section.x, section.y, section.width, section.height);
    }

    /**
     * Get a sprite sheet that has been packed into the greater image
     *
     * @param name The name of the sprite sheet to retrieve
     * @return The sprite sheet from the packed sheet
     */
    @Nonnull
    public SpriteSheet getSpriteSheet(String name) {
        Image image = getSprite(name);
        Section section = sections.get(name);

        return new SpriteSheet(image, section.width / section.tilesx, section.height / section.tilesy);
    }

    /**
     * Load the definition file and parse each of the sections
     *
     * @param def The location of the definitions file
     * @param trans The color to be treated as transparent
     * @throws SlickException Indicates a failure to read or parse the definitions file
     * or referenced image.
     */
    private void loadDefinition(String def, @Nullable Color trans) throws SlickException {
        try (
                final InputStream resourceAsStream = ResourceLoader.getResourceAsStream(def);
                final Reader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
                final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            image = new Image(basePath+reader.readLine(), false, filter, trans);
            while (reader.ready()) {
                if (reader.readLine() == null) {
                    break;
                }

                Section sect = new Section(reader);
                sections.put(sect.name, sect);

                if (reader.readLine() == null) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.error(e);
            throw new SlickException("Failed to process definitions file - invalid format?", e);
        }
    }

    /**
     * A single section defined within the packed sheet
     *
     * @author kevin
     */
    private class Section {
        /** The x position of the section */
        public int x;
        /** The y position of the section */
        public int y;
        /** The width of the section */
        public int width;
        /** The height of the section */
        public int height;
        /** The number of sprites across this section */
        public int tilesx;
        /** The number of sprites down this section */
        public int tilesy;
        /** The name of this section */
        public String name;

        /**
         * Create a new section by reading the stream provided
         *
         * @param reader The reader from which the definition can be read
         * @throws IOException Indicates a failure toread the provided stream
         */
        public Section(@Nonnull BufferedReader reader) throws IOException {
            name = reader.readLine().trim();

            x = Integer.parseInt(reader.readLine().trim());
            y = Integer.parseInt(reader.readLine().trim());
            width = Integer.parseInt(reader.readLine().trim());
            height = Integer.parseInt(reader.readLine().trim());
            tilesx = Integer.parseInt(reader.readLine().trim());
            tilesy = Integer.parseInt(reader.readLine().trim());
            reader.readLine().trim();
            reader.readLine().trim();

            tilesx = Math.max(1,tilesx);
            tilesy = Math.max(1,tilesy);
        }
    }
}
