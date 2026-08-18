# ColorOS Launcher — Galaxy Z Fold7

Launcher Android personnalisé, style ColorOS (Oppo/Realme), pensé pour le Galaxy Z Fold7
(cover screen + écran interne + Flex Mode). 100% Kotlin/Jetpack Compose, sans root.

Ce dépôt contient la **Phase 1 (MVP)** : un launcher fonctionnel installable, avec grille
d'icônes personnalisable, dock, tiroir d'applications, dossiers, gestion du wallpaper et
adaptation automatique au pli. Voir [Ce qui est fait / reste à faire](#état-davancement) plus bas.

## Architecture

```
:app             Point d'entrée. Manifest HOME/DEFAULT, MainActivity, DI manuelle.
:launcher-core   Cœur métier : modèles, Room, repositories, ViewModels, UI Compose du launcher.
:widgets         Intégration AppWidgetHost (widgets tiers embarqués sur l'écran d'accueil).
:theme           Palette, typographie, formes "squircle", ColorOSTheme, config centralisée.
```

- **UI** : Jetpack Compose exclusivement, pas de XML de layout.
- **Architecture** : MVVM + Repository. Pas de framework DI (Hilt/Dagger) — un
  `ServiceLocator` (`app/.../di/ServiceLocator.kt`) fournit les dépendances pour rester léger.
- **Persistance** : Room (`launcher-core/.../db`) pour la grille, le dock et les dossiers.
- **Apps installées** : `LauncherApps` (API native), avec écoute live des installations /
  désinstallations / mises à jour (`AppRepository`).
- **Pliable** : `androidx.window` (`FoldStateManager` + `FoldStateMapper`) traduit l'état
  physique de l'écran en `FoldState` (cover/inconnu, déplié à plat, Flex Mode), et pilote le
  nombre de colonnes/lignes de la grille ainsi que l'affichage côte-à-côte du panneau
  "Smart Assistant" quand l'écran interne est à plat.

### Personnaliser le style sans toucher au code

Toutes les couleurs, rayons d'arrondi ("squircle"), dimensions de grille et durées
d'animation sont centralisées dans **`theme/src/main/java/com/coloroslauncher/theme/ThemeConfig.kt`**.
Modifier ce seul fichier suffit à re-skinner le launcher.

## État d'avancement

### Phase 1 — MVP (ce commit)
- [x] Grille d'icônes personnalisable, drag & drop (appui long) pour réorganiser
- [x] Dock fixe en bas d'écran
- [x] Tiroir d'applications, scroll vertical, recherche par texte
- [x] Dossiers : création en glissant une icône sur une autre, rejoindre un dossier existant,
      renommer, lister/lancer les membres
- [x] Wallpaper statique, avec emplacement distinct pour cover screen vs écran déplié
- [x] Détection foldable temps réel (`androidx.window`), grille adaptative
      (4 colonnes replié / 6 déplié / repli Flex Mode compact), sans redémarrage d'activité
      (`configChanges` gérés dans le manifest)
- [x] `LauncherApps` avec mise à jour live de la liste d'apps
- [x] Déclaration comme launcher par défaut (intent-filter `HOME`/`DEFAULT`)
- [x] Widgets : `AppWidgetHost` natif, picker minimal, hébergement Compose (`WidgetHostContainer`)
- [x] Thème clair/sombre auto + override manuel (`SettingsRepository`)
- [x] Mode Focus (masquer temporairement des apps) — logique de filtrage en place
      (`HomeUiState.visibleWorkspaceItems`), UI de sélection à brancher en Phase 3
- [x] Tests unitaires : logique de grille/dossiers (`GridLayoutLogicTest`) et mapping
      fold state (`FoldStateMapperTest`, `FoldStateManagerCompanionTest`)

### Phase 2 — Habillage visuel (partiellement posé, à compléter)
- [x] Formes squircle homogènes, palette douce, typographie fine (module `:theme`)
- [x] Transition de page avec `HorizontalPager` (base pour l'effet de profondeur)
- [ ] Animation zoom+fade à l'ouverture d'une app depuis son icône (source bounds déjà
      transmis à `LauncherApps.startMainActivity`, l'animation de transition d'activité reste
      à implémenter avec `ActivityOptions.makeScaleUpAnimation` / `makeClipRevealAnimation`)
- [ ] Widget horloge/météo natif grand format (le module `:widgets` est prêt à l'accueillir)
- [ ] Effet de parallax plus poussé entre pages

### Phase 3 — Avancé (non démarré, points d'ancrage posés)
- [ ] Gestes : swipe up pour drawer ✅ (déjà implémenté dans `MainActivity`), double-tap
      lock, swipe down notifications — restants
- [ ] Smart Sidebar (tiret latéral)
- [ ] Panneau "Smart Assistant" (stub visuel présent dans `HomeScreen.kt`, contenu à faire)
- [ ] Badges de notification — le modèle `AppInfo.badgeCount` et l'UI existent, la source
      (NotificationListenerService) reste à brancher
- [ ] UI du mode Focus (sélection des apps à masquer)

## Build

Prérequis : **Android Studio (Koala ou plus récent)** avec le SDK Android (API 35) installé,
ou le SDK en ligne de commande + variable `ANDROID_HOME` configurée.

```bash
git clone <ce dépôt>
cd ColorOS-pour-zfold7
./gradlew assembleDebug
```

L'APK debug est généré dans `app/build/outputs/apk/debug/app-debug.apk`.

> Remarque : ce dépôt a été préparé dans un environnement sandbox sans accès à
> `dl.google.com` (dépôt Maven Google), donc sans SDK Android disponible. La structure Gradle
> a été validée (résolution de `settings.gradle.kts` / version catalog OK), mais la
> compilation complète et la génération de l'APK n'ont pas pu être exécutées ici. Elles le
> seront normalement sans problème dans Android Studio, qui gère automatiquement le
> téléchargement du SDK et le wrapper Gradle.

### Lint / qualité

```bash
./gradlew detekt        # analyse statique, échoue si issues (maxIssues: 0)
./gradlew ktlintCheck   # style de code Kotlin
./gradlew test          # tests unitaires (tous modules)
```

## Installation sur le Z Fold7

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Puis, sur l'appareil :
1. **Paramètres → Applications → Application par défaut → Application d'accueil**
2. Sélectionner **ColorOS Launcher**

Pour revenir à One UI : refaire la même manipulation et resélectionner l'application
d'accueil Samsung. Le launcher ne modifie ni ne supprime aucune app système ; le
désactiver comme launcher par défaut restaure immédiatement le comportement normal.

## Permissions

- `SET_WALLPAPER` : nécessaire pour appliquer le fond d'écran choisi par l'utilisateur.
- Aucune permission de stockage n'est requise : le choix d'image de fond passe par le
  sélecteur système (`ACTION_GET_CONTENT`), pas d'accès direct au stockage.
- Aucun service Google propriétaire non essentiel n'est utilisé.

## Modules en détail

| Module | Contenu clé |
|---|---|
| `theme` | `ThemeConfig.kt` (config centralisée), `ColorOSTheme.kt`, palette clair/sombre |
| `launcher-core` | `model/`, `db/` (Room), `repository/` (Apps, Layout, Wallpaper, Settings), `fold/` (détection pliable), `viewmodel/`, `ui/` (écrans Compose) |
| `widgets` | `WidgetHostManager` (AppWidgetHost natif), `WidgetHostContainer` (pont Compose), `WidgetPicker` |
| `app` | `LauncherApplication`, `MainActivity`, `ServiceLocator`, manifest launcher par défaut |
