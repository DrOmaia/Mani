# Instructions for any AI tool (or person) changing Mani

- Owner writes in Arabic (Levantine); answer in Arabic, feminine address, short. Ask first if a request is ambiguous.
- App: Arabic ⇄ Kirundi translator on one phone. Plain HTML/JS in `www/index.html`, wrapped by Capacitor;
  the APK is built only by `.github/workflows/build.yml` (no local Android SDK needed).
- **Never change or delete `release.keystore`** or the appId `io.github.dromaia.mani`: installed apps would stop updating.
  It is public in this repository by the owner's choice (same as prayer-tv).
- Version = build number (set by the workflow in `www/version.js` and `versionCode`); do not edit by hand.
- Every push to `main` publishes release `vN`; older releases are deleted, only the newest and the `STABLE` one stay.
  The app shows an update bar when a newer `vN` exists; it downloads into the app cache and deletes it on the next start.
- `STABLE` = last version the owner tested. Actions → Build APK → Run workflow → `mark-stable` / `restore-stable`.
- Never force-push `main`; never delete the newest or the `STABLE` release. Add an entry to `CHANGELOG.md` for every change.
