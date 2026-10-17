package app.linkedin.extension;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * Material You for LinkedIn's Compose themes (Delux and Mercado), which define their colors in code instead of
 * resources. The patch passes every color constant of those themes through {@link #color(long)}.
 */
@SuppressWarnings("unused")
public final class MaterialYou {
    /** LinkedIn color (0xRRGGBB) to the system palette color that replaces it. */
    private static final Map<Integer, Integer> SYSTEM_COLORS = new HashMap<>();

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Blue palette: links, buttons, selected states.
            put(0xF6FBFF, android.R.color.system_accent1_10);
            put(0xE8F3FF, android.R.color.system_accent1_50);
            put(0xD0E8FF, android.R.color.system_accent1_100);
            put(0xD2E9FF, android.R.color.system_accent1_100);
            put(0xA8D4FF, android.R.color.system_accent1_200);
            put(0xAAD6FF, android.R.color.system_accent1_200);
            put(0x70B5F9, android.R.color.system_accent1_200);
            put(0x71B7FB, android.R.color.system_accent1_200);
            put(0x378FE9, android.R.color.system_accent1_400);
            put(0x0A66C2, android.R.color.system_accent1_600);
            put(0x004182, android.R.color.system_accent1_700);
            put(0x09223B, android.R.color.system_accent1_800);
            put(0x0A2037, android.R.color.system_accent1_900);
            // Backgrounds: cards in dark mode, and the light canvas.
            put(0x1B1F23, android.R.color.system_neutral2_800);
            put(0x1D2226, android.R.color.system_neutral2_800);
            put(0x293037, android.R.color.system_neutral2_700);
            put(0x293138, android.R.color.system_neutral2_700);
            put(0xF4F2EE, android.R.color.system_neutral2_50);
            put(0xF3F2EF, android.R.color.system_neutral2_50);
        }
    }

    private MaterialYou() {
    }

    private static void put(int rgb, int systemColor) {
        SYSTEM_COLORS.put(rgb, systemColor);
    }

    /** Injected after each color constant of the Compose themes: an ARGB value as used by Color(long). */
    public static long color(long argb) {
        try {
            if (SYSTEM_COLORS.isEmpty() || (argb >>> 24) != 0xFF) return argb;
            Integer systemColor = SYSTEM_COLORS.get((int) (argb & 0xFFFFFF));
            if (systemColor == null) return argb;
            Context context = Settings.app();
            Resources resources = context != null ? context.getResources() : Resources.getSystem();
            return resources.getColor(systemColor, null) & 0xFFFFFFFFL;
        } catch (Throwable t) {
            Diagnostics.error("Material You color failed", t);
            return argb;
        }
    }
}
