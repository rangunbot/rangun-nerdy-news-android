# Rangun Nerdy News Android

Android-WebView-App für die Web-Ausgabe von Gunnars Tageszeitung
(https://news.digitalstep.de/).

## Build

```bash
./gradlew assembleDebug
```

Minimaler WebView-Wrapper (Java, keine externen Dependencies):
- JavaScript + DOM-Storage aktiviert
- Edge-to-Edge mit Padding für die System-Bars (Android 15+, targetSdk 35),
  damit die festen Header-Buttons erreichbar bleiben
- Back-Button navigiert im WebView-Verlauf zurück

## Verwandte Repos

- [rangun-nerdy-news](https://github.com/rangunbot/rangun-nerdy-news) — die Pipeline dahinter
- [mcp-latex](https://github.com/gbastkowski/mcp-latex) — PDF-Rendering
