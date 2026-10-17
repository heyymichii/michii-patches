package app.linkedin.patches.theme

import app.linkedin.patches.shared.Constants.COMPATIBILITY_LINKEDIN
import app.morphe.patcher.patch.ResourcePatchContext
import app.morphe.patcher.patch.resourcePatch

/**
 * LinkedIn's blue palette (blue_* and the Mercado design system's mercado_mvp_blue_*) mapped to
 * the system accent palette. Tone 70 is the action color on light backgrounds, tone 50 on dark ones.
 */
private val ACCENT_SHADES = mapOf(
    "10" to "10",
    "20" to "50",
    "30" to "100",
    "40" to "200",
    "50" to "200",
    "60" to "400",
    "70" to "600",
    "80" to "700",
    "90" to "800",
    "100" to "900",
)

/**
 * Backgrounds of the Mercado light and dark themes mapped to the tinted system neutral palettes,
 * so the canvas and cards follow the wallpaper too instead of staying gray.
 */
private val BACKGROUNDS = mapOf(
    "mercado_mvp_color_background_canvas" to "system_neutral2_50",
    "mercado_mvp_color_background_canvas_mobile" to "system_neutral2_100",
    "mercado_mvp_color_background_canvas_tint" to "system_neutral1_10",
    "mercado_mvp_color_background_container" to "system_neutral1_10",
    "mercado_mvp_color_background_container_tint" to "system_neutral2_50",
    "mercado_mvp_dark_color_background_canvas" to "system_neutral1_900",
    "mercado_mvp_dark_color_background_canvas_mobile" to "system_neutral1_900",
    "mercado_mvp_dark_color_background_canvas_tint" to "system_neutral2_800",
    "mercado_mvp_dark_color_background_canvas_dark_tint" to "system_neutral2_800",
    "mercado_mvp_dark_color_background_container" to "system_neutral2_800",
    "mercado_mvp_dark_color_background_container_dark" to "system_neutral2_800",
    "mercado_mvp_dark_color_background_container_tint" to "system_neutral2_700",
    "mercado_mvp_dark_color_background_container_dark_tint" to "system_neutral2_700",
    // Dark variants that the light theme uses for dark surfaces.
    "mercado_mvp_color_background_canvas_dark" to "system_neutral1_900",
    "mercado_mvp_color_background_canvas_dark_tint" to "system_neutral2_800",
    "mercado_mvp_color_background_container_dark" to "system_neutral2_800",
    "mercado_mvp_color_background_container_dark_tint" to "system_neutral2_700",
    "color_splash_canvas_background" to "system_neutral2_50",
)

/** Colors that LinkedIn defines separately for dark mode (values-night). */
private val NIGHT_BACKGROUNDS = mapOf(
    "color_splash_canvas_background" to "system_neutral1_900",
    "color_status_bar" to "system_neutral1_900",
)

@Suppress("unused")
val materialYouPatch = resourcePatch(
    name = "Material You",
    description = "Uses your wallpaper colors (Android 12+) for accents and backgrounds instead of LinkedIn blue and gray. " +
        "Some screens drawn by LinkedIn's servers may keep their own colors.",
    default = false
) {
    compatibleWith(COMPATIBILITY_LINKEDIN)

    execute {
        val colors = mutableMapOf<String, String>()
        for ((tone, accent) in ACCENT_SHADES) {
            // blue_90 and mercado_mvp_blue_100 do not exist in LinkedIn's palette.
            if (tone != "90") colors["blue_$tone"] = "system_accent1_$accent"
            if (tone != "100") colors["mercado_mvp_blue_$tone"] = "system_accent1_$accent"
        }
        colors += BACKGROUNDS

        // -v31: the system palette only exists on Android 12+, older versions keep LinkedIn's colors.
        writeColors("values-v31", colors)
        writeColors("values-night-v31", NIGHT_BACKGROUNDS)
    }
}

private fun ResourcePatchContext.writeColors(directory: String, colors: Map<String, String>) {
    val xml = buildString {
        appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
        appendLine("<resources>")
        for ((name, system) in colors) {
            appendLine("""    <color name="$name">@android:color/$system</color>""")
        }
        appendLine("</resources>")
    }
    get("res").resolve(directory).apply { mkdirs() }.resolve("colors.xml").writeText(xml)
}
