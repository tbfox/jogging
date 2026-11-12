# Development

Developer notes for the Jogging mod: building, tweaking, and how the code fits together. For how the mod plays, see the [README](../README.md).

## Building from source

### Requirements

- Hytale server **0.7.7** or newer. The mod is compiled against 0.7.7's `HytaleServer.jar`.
- A **JDK 25 or newer**, because Hytale's server is built for Java 25, plus **Gradle**.

### Build and install (macOS)

```bash
./install.sh
```

This builds `build/libs/Jogging-<version>.jar` and copies it into `~/Library/Application Support/Hytale/UserData/Mods/`.

`install.sh` uses Homebrew's JDK at `/opt/homebrew/opt/openjdk`. To use a different JDK, set `JAVA_HOME_HYTALE`:

```bash
JAVA_HOME_HYTALE=/path/to/jdk ./install.sh
```

On other systems, run `gradle build` and copy the jar from `build/libs/` into your Hytale `UserData/Mods` folder. You'll also need to point the `hytaleServerJar` path in `build.gradle` at your install.

Hytale only loads mods when a world starts. After installing a new build, restart the world; you don't need to restart the whole game.

### Rebuilding after game updates

The mod is compiled against a specific server version. If a Hytale update breaks it (for example `NoSuchMethodError` in the server log), rebuild against the new `HytaleServer.jar`.

## Tweaking

| What | Where |
|---|---|
| Recovery time (seconds from empty to full) | `RECOVERY_SECONDS` in `JogEffects.java` |
| Jogging speed versus sprint speed | `SPEED_GAP` in `JogEffects.java` |
| Which actions are locked | `LOCKED` in `ActivityLockSystem.java` |
| Label text, colours, background, position | `JogHud.java` |
| What "winded" does to movement | `src/main/resources/Server/Entity/Effects/TBFox_Jogging_Winded.json` |

**Keep `SPEED_GAP` above 0.** Run and sprint speeds must never be exactly equal, or the game client crashes ("value NaN is not finite"). The gap keeps them 2% apart, so jogging is 98% of sprint speed.

## How it works

The code is in `src/main/java/com/tbfox/jogging/`.

| File | Role |
|---|---|
| `JoggingPlugin.java` | Entry point. Registers the command, the two systems, and clears a player's state when they disconnect. |
| `JogCommand.java`, `JogStartCommand.java`, `JogStopCommand.java` | The `/jog` commands. They switch the player's state and apply the immediate changes. |
| `JogState.java`, `JogStates.java` | Each player's state (normal, jogging, winded), kept in memory. |
| `JogTickSystem.java` | Runs every tick. Keeps stamina full and speed up while jogging, refills stamina slowly while winded, applies the winded effect, and keeps the corner label in sync. |
| `ActivityLockSystem.java` | Cancels locked actions as they start, using the same hook as the game's portal "curse". |
| `JogEffects.java` | The speed, stamina and winded-effect changes. |
| `JogHud.java` | The JOGGING / WINDED label, built from inline UI markup like the game's own HUD labels. |

The mod ships one asset, `TBFox_Jogging_Winded`, an infinite effect with `DisableSprint` that's on only while you're winded. That's why `manifest.json` has `"IncludesAssetPack": true`.

## Releasing

Publishing a GitHub Release sends its jar to CurseForge, through `.github/workflows/curseforge.yml`. The jar is built on your machine, not on GitHub, because the build needs your local `HytaleServer.jar`.

### One-time setup

1. Create the CurseForge project and upload the first file by hand, so the project exists and has an ID.
2. Generate a CurseForge API token in the author console, under API tokens.
3. Add these to the GitHub repo, under Settings, then Secrets and variables, then Actions:

| Kind | Name | Value |
|---|---|---|
| Secret | `CURSEFORGE_TOKEN` | The API token. |
| Variable | `CURSEFORGE_PROJECT_ID` | The numeric project ID from the CurseForge project page. |
| Variable (optional) | `CURSEFORGE_GAME_VERSIONS` | Comma-separated game version names as CurseForge lists them, for example `0.7.7`. |

### Each release

1. Raise the version in both `build.gradle` and `src/main/resources/manifest.json`.
2. Build, commit, and push:

   ```bash
   ./install.sh
   ```

3. Create the release with the jar attached. The release notes become the CurseForge changelog:

   ```bash
   gh release create v0.2.0 build/libs/Jogging-0.2.0.jar --title "0.2.0" --notes "What changed."
   ```

A release marked as a pre-release is uploaded to CurseForge as a beta. New files still go through CurseForge moderation before they're public.

If the upload fails, fix the cause and re-run it from the Actions tab: choose "Publish to CurseForge", then "Run workflow", and enter the tag.

## Technical limitations

- **No dedicated key.** Server mods can't add keybinds, so jogging is toggled with commands.
- **Brief swing animation.** Locked actions are cancelled on the server, so the client may start the animation for a moment before the cancel arrives.
- **State is in memory only.** `JogStates` is cleared on disconnect and isn't persisted across world restarts.
