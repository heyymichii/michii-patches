package app.linkedin.patches.theme

import app.linkedin.patches.shared.Constants.COMPATIBILITY_LINKEDIN
import app.linkedin.patches.shared.Constants.EXTENSION_PACKAGE
import app.linkedin.patches.shared.settingsPatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.ResourcePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction

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

/** Resource colors: XML layouts and the View based screens. */
private val materialYouResourcePatch = resourcePatch {
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

/** Packages of the Compose themes (Delux and Mercado), which hardcode their palettes in static initializers. */
private val COMPOSE_THEME_PACKAGES = listOf(
    "Lcom/linkedin/android/delux/compose/theme",
    "Lcom/linkedin/android/mercado/mvp/compose/",
)

/** Colors replaced at runtime, keep in sync with MaterialYou.java. */
private val COMPOSE_COLORS = setOf(
    0xF6FBFF, 0xE8F3FF, 0xD0E8FF, 0xD2E9FF, 0xA8D4FF, 0xAAD6FF, 0x70B5F9, 0x71B7FB,
    0x378FE9, 0x0A66C2, 0x004182, 0x09223B, 0x0A2037,
    0x1B1F23, 0x1D2226, 0x293037, 0x293138, 0xF4F2EE, 0xF3F2EF,
).map { 0xFF000000L or it.toLong() }.toSet()

@Suppress("unused")
val materialYouPatch = bytecodePatch(
    name = "Material You",
    description = "Uses your wallpaper colors (Android 12+) for accents and backgrounds instead of LinkedIn blue and gray. " +
        "Some screens drawn by LinkedIn's servers may keep their own colors.",
    default = false
) {
    compatibleWith(COMPATIBILITY_LINKEDIN)
    dependsOn(settingsPatch, materialYouResourcePatch)

    execute {
        val themes = mutableListOf<ClassDef>()
        classDefForEach { classDef ->
            if (COMPOSE_THEME_PACKAGES.any { classDef.type.startsWith(it) } &&
                classDef.methods.any { it.name == "<clinit>" }
            ) themes += classDef
        }

        var replaced = 0
        themes.forEach { classDef ->
            val method = mutableClassDefBy(classDef).methods.first { it.name == "<clinit>" }
            val colors = method.implementation!!.instructions.withIndex().filter { (_, instruction) ->
                instruction.opcode == Opcode.CONST_WIDE &&
                    (instruction as WideLiteralInstruction).wideLiteral in COMPOSE_COLORS
            }
            // From the end, so the indexes of the earlier constants stay valid.
            colors.reversed().forEach { (index, instruction) ->
                val register = (instruction as OneRegisterInstruction).registerA
                method.addInstructions(
                    index + 1,
                    """
                        invoke-static/range { v$register .. v${register + 1} }, $EXTENSION_PACKAGE/MaterialYou;->color(J)J
                        move-result-wide v$register
                    """
                )
            }
            replaced += colors.size
        }
        if (replaced == 0) throw PatchException("No Compose theme colors found")
    }
}
