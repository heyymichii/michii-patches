# 🧩 Michii Patches

LinkedIn patches for use with [Morphe](https://morphe.software), by **heyymichii**.

Michii Patches is an independent project. It is not affiliated with, endorsed by, or part of the
Morphe project or LinkedIn.

## ❓ About

Michii Patches removes ads and clutter from LinkedIn and adds features the app does not have:

- **Ads & promotions:** hide promoted posts, promoted jobs, suggested posts, sponsored messages,
  and Premium / AI upsells (feed, profiles, Jobs, and the "Me" panel).
- **Download:** a download button on full screen photos, videos, profile photos, and banners,
  with a configurable save location.
- **Feed filters** (off by default): focus mode (hide like/comment/repost counts), celebrations,
  job cards, reposts, video posts, the "New posts" pill, and "See translation".
- **Chat:** ghost mode (no typing indicator or automatic read status; mark chats read by hand
  from the chat list), hide sponsored messages.
- **Privacy:** open links directly without LinkedIn's warning page (including `lnkd.in` short
  links), remove tracking parameters from copied and shared links, and an optional analytics block.
- **Disable double-tap like** on posts and photos.
- **Settings:** a "Michii Patches" entry in the "Me" panel (tap your profile photo) to turn each
  feature on or off, search settings, back up and restore them, see which patches are applied,
  and check for updates.

### Supported version

| App | Version |
|---|---|
| LinkedIn (`com.linkedin.android`) | **4.1.1255.1** (APKMirror variant *Android 10+*) |

Other versions are marked experimental and may not work.

### How to use these patches

Click here to add these patches to Morphe: https://morphe.software/add-source?github=heymichii/michii-patches

Then patch LinkedIn with Morphe Manager. Do not share patched APKs; share this patch source instead.

> ⚠️ Modifying the LinkedIn app may violate LinkedIn's terms of service. Use at your own risk.

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->

<!-- Do not modify this section by hand. The patch list is generated when release.yml creates a new release. -->

#### The patch list is generated automatically with the first release.

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

Michii Patches are licensed under the [GNU General Public License v3.0](LICENSE), including the
additional terms in [NOTICE](NOTICE).

Based on the [Morphe patches template](https://github.com/MorpheApp/morphe-patches-template).
