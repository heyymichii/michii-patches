package app.linkedin.extension;

import android.view.View;
import android.view.ViewGroup;

/** Hooks of the bottom bar's tabs (SlimNotificationBadge), for "Bottom bar style". */
@SuppressWarnings("unused")
public final class BottomBarStyle {
    private static int labelId;
    private static int iconId;

    private BottomBarStyle() {
    }

    /**
     * After SlimNotificationBadge.setBadgeLabelText: with "Hide tab labels" on, hides the label under the icon and
     * centers the icon, which LinkedIn moves up to make room for the label.
     */
    public static void onTabLabel(View tab) {
        try {
            if (tab == null || !Settings.hideTabLabels()) return;
            if (labelId == 0) {
                labelId = id(tab, "slim_notification_badge_label");
                iconId = id(tab, "slim_notification_badge_icon");
            }
            View label = labelId == 0 ? null : tab.findViewById(labelId);
            if (label == null) return;
            label.setVisibility(View.GONE);
            View icon = iconId == 0 ? null : tab.findViewById(iconId);
            if (icon != null && icon.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
                params.topMargin = 0;
                params.bottomMargin = 0;
                icon.setLayoutParams(params);
            }
        } catch (Throwable t) {
            Diagnostics.error("onTabLabel failed", t);
        }
    }

    private static int id(View view, String name) {
        return view.getResources().getIdentifier(name, "id", view.getContext().getPackageName());
    }
}
