package io.rocketbase.toggl.backend.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * holds a list of colors<br>
 * provides a function to transform from hex-color to rgb-color<br>
 * furthermore it allows to find a nearest fitting color to given hex-code
 */
public enum ColorPalette {

    BRICK("a54e3c"),
    POMEGRANTE("881f30"),
    CABERNET("721432"),
    AUBERGINE("5b1946"),
    INDIGO("461c5d"),
    PLUM("5c145e"),
    ROYAL("97348a"),
    FRENCH_BLUE("3266ad"),
    TEAL("337b8d"),
    AQUAMARINE("22586f"),
    MARINE("18456b"),
    NAVY("0e2e57"),
    LILAC("ae9cc7"),
    ORCHID("985a9c"),
    COBALT("4099d4"),
    POOL("9cd6f5"),
    PERIWINKLE("a7b4d0"),
    DOVE("aec4d0"),
    DUCK_EGG("97babf"),
    TURQUOISE("6fbabf"),
    JADE("73aba0"),
    TIFFANY("a5d2c1"),
    MINT("c8e0c4"),
    SPRING("d3e2a3"),
    CHIVE("b8d26e"),
    TREE_TOP("759150"),
    BOTTLE("378159"),
    EMERALD("29634e"),
    LEMON("fef6a6"),
    SUNSHINE("fcf151"),
    TUMERIC("f9d549"),
    CLEMENTINE("f3b143"),
    PEACH("f7d19d"),
    SALMON("f0b79f"),
    CORAL("e89487"),
    WATERMELON("e3708e"),
    RASBERRY("ac2c69"),
    MAGENTA("db318a"),
    RED_BALLOON("db3656"),
    FIRE_ENGINE("db3732"),
    BLOOD_ORANGE("e06151"),
    PAPAYA("e88b5b"),
    ROSE("e6aab3"),
    CHERRY_BLOSSOM("f6d6dc"),
    BLUSH("f6d6d1"),
    FRENCH_VANILLA("f8f1de"),
    IVORY("fbf8da"),
    CLAY("e8d4b8"),
    SANDSTONE("c4b199"),
    SAGE("a5b4a3"),
    MOSS("a2a495"),
    OLIVE("7b7d6a"),
    SMOKE("63605c"),
    ESPRESSO("473937"),
    CHOCOLATE("684940"),
    CARAMEL("957d62"),
    BLACK("221f20"),
    CHARCOAL("58585a"),
    SLATE("949599"),
    SILVER_LINING("d1d2d4"),
    OYSTER("e3ddd4"),
    STONE("d2cfc9"),
    SMOG("928b88"),
    WHITE("ffffff");

    public static final String STYLE_BASE_NAME = "color-palette";
    @Getter
    private String hexCode;

    ColorPalette(String hexCode) {
        this.hexCode = hexCode;
    }

    /**
     * find a nearest fitting @{@link ColorPalette} by given hexcode
     *
     * @param hexCode sting of color could start with # or not...
     * @return when no problem @{@link ColorPalette}
     */
    public static ColorPalette getNearest(String hexCode) {
        RgbColor given = hex2rgb(hexCode);
        if (given == null) {
            return null;
        }
        List<Double> differenceArray = new ArrayList<>();
        Arrays.asList(values())
              .forEach(color -> {
                  RgbColor c = color.getRgbColor();
                  differenceArray.add(Math.sqrt((given.getR() - c.getR()) * (given.getR() - c.getR())
                          + (given.getG() - c.getG()) * (given.getG() - c.getG())
                          + (given.getB() - c.getB()) * (given.getB() - c.getB())));
              });
        Double min = Collections.min(differenceArray);
        int index = differenceArray.indexOf(min);

        return values()[index];
    }

    public static ColorPalette getRandomValue() {
        return values()[new Random().nextInt(values().length)];
    }

    /**
     * read's given hex string and returns a it's color representation by red, green, blue
     *
     * @param hexCode sting of color could start with # or not...
     * @return @{@link RgbColor}
     */
    public static RgbColor hex2rgb(String hexCode) {
        if (hexCode == null) {
            return null;
        }
        String colour = new String(hexCode);
        if (colour.startsWith("#")) {
            colour = colour.substring(1);
        }

        int r = 0, g = 0, b = 0;

        if (colour.length() == 6) {
            r = Integer.valueOf(colour.substring(0, 1), 16);
            g = Integer.valueOf(colour.substring(2, 3), 16);
            b = Integer.valueOf(colour.substring(4, 5), 16);
        } else if (colour.length() == 3) {
            r = Integer.valueOf(colour.substring(0, 1) + colour.substring(0, 1), 16);
            g = Integer.valueOf(colour.substring(1, 2) + colour.substring(1, 2), 16);
            b = Integer.valueOf(colour.substring(2, 3) + colour.substring(2, 3), 16);
        } else {
            return null;
        }

        return new RgbColor(r, g, b);
    }

    public RgbColor getRgbColor() {
        return hex2rgb(this.getHexCode());
    }

    public static ColorPalette getRandomValue(Collection<ColorPalette> existingCollection) {
        ColorPalette newValue = null;
        if (existingCollection == null || existingCollection.size() >= values().length) {
            return null;
        }
        do {
            newValue = getRandomValue();
        } while (!existingCollection.contains(newValue));
        return newValue;
    }

    public String getStyleName() {
        return STYLE_BASE_NAME + "-" +
                this.name()
                    .toLowerCase()
                    .replace("_", "-");
    }

    /**
     * calculate the best constrast color to given color
     *
     * @return when true text-color should be black otherwise white
     */
    public boolean isTextConstrastBlack() {
        RgbColor color = getRgbColor();
        // return (color.getR() * 0.299 + color.getG() * 0.587 + color.getB() * 0.114) > 186 ? true : false;
        return (Integer.valueOf(hexCode, 16) > 0xffffff / 2) ? true : false;
    }

    @RequiredArgsConstructor
    @Getter
    public static class RgbColor {
        /**
         * red
         */
        private final int r;
        /**
         * green
         */
        private final int g;
        /**
         * blue
         */
        private final int b;
    }

}
