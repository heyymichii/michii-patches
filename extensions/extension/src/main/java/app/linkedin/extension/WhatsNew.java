package app.linkedin.extension;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.util.Log;

/** Shows the changes of the installed patch bundle once, the first time LinkedIn opens after patching. */
final class WhatsNew {
    /** Newest first: {version, notes in English}. Notes are translated like any other text. */
    static final String[][] CHANGELOG = {
            {"1.0.1", "• The settings now use your phone's language (English, Indonesian, Spanish, Portuguese, "
                    + "French, German, Vietnamese, Thai, Turkish, Russian, or Japanese). You can pick another language "
                    + "under Other → Language.\n\n"
                    + "Open the settings from the Me panel → Michii Patches, or long press the LinkedIn icon "
                    + "→ Michii Patches."},
            {"1.0.0", "The first release of Michii Patches.\n\n"
                    + "• Hide ads, promoted jobs, suggested posts, and Premium promotions\n"
                    + "• Download photos, videos, profile photos, and banners, with a configurable save location\n"
                    + "• Feed filters: focus mode, celebrations, jobs, reposts, videos\n"
                    + "• Chat: hide sponsored messages, and ghost mode\n"
                    + "• Open links directly (including lnkd.in) and clean share links\n"
                    + "• Block tracking and disable double-tap like\n\n"
                    + "Open the settings from the Me panel → Michii Patches."},
    };

    private static boolean checked;

    private WhatsNew() {
    }

    /** Text for a version, or null. Pre-release builds (1.1.0-dev.2) use their base version's entry. */
    static String notesFor(String version) {
        String base = version.split("-", 2)[0];
        for (String[] entry : CHANGELOG) {
            if (entry[0].equals(base)) return I18n.tr(entry[1]);
        }
        return null;
    }

    static void maybeShow(Activity activity) {
        if (checked) return;
        checked = true;
        try {
            String version = Settings.patchesVersion();
            if (version.equals(Settings.getString(Settings.LAST_SEEN_VERSION, null))) return;
            Settings.setString(Settings.LAST_SEEN_VERSION, version);

            String notes = notesFor(version);
            if (notes == null) return;
            boolean dark = (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES;
            new AlertDialog.Builder(activity, dark ? android.R.style.Theme_DeviceDefault_Dialog_Alert
                    : android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                    .setTitle(I18n.f("What's new in %1$s %2$s", SettingsActivity.BRAND, version))
                    .setMessage(notes)
                    .setPositiveButton("OK", null)
                    .setNeutralButton(I18n.tr("Settings"), (dialog, which) -> SettingsActivity.open(activity))
                    .show();
        } catch (Throwable t) {
            Log.e(Settings.TAG, "WhatsNew failed", t);
        }
    }
}
