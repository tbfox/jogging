# Jogging

A Hytale server mod for covering long distances. Start jogging and you can run at sprint speed for as long as you like, without using up stamina. The catch: you can't fight while jogging, and when you stop you're **winded** until your stamina comes back.

## How it plays

| | Jogging | Winded | Normal |
|---|---|---|---|
| **How you get here** | `/jog start` | `/jog stop` | Stamina bar refills |
| **Speed** | Running is as fast as sprinting | Normal, no sprinting | Normal |
| **Stamina** | Stays full | Drops to empty, refills over ~10 seconds | Normal |
| **Attack, block, abilities, use** | Locked | Locked | Allowed |
| **Corner label** | **JOGGING** (green) | **WINDED** (orange) | None |

- **Jogging:** forward running moves you at sprint speed without holding the sprint key, and the stamina bar never goes down.
- **Locked actions:** while jogging and while winded, you can't attack or mine, block, place blocks, eat or drink, use weapon abilities, or press E on doors and chests. Trying shows a short message in chat. Picking up items still works.
- **Winded:** stopping empties your stamina bar. It refills at a steady rate over about 10 seconds. You can't sprint until it's full, and then everything unlocks with "You have recovered from jogging."

## Commands

| Command | What it does |
|---|---|
| `/jog start` | Start jogging. |
| `/jog stop` | Stop jogging and become winded. |
| `/jog` | Shows usage help. |

Any player can use these commands. They don't need admin.

- `/jog start` while already jogging tells you so and does nothing.
- `/jog stop` when you're not jogging tells you so and does nothing.
- `/jog start` while winded puts you back into jogging. Stopping again empties the bar again, so this doesn't skip recovery.

## Installing

### Requirements

- Hytale server **0.7.7** or newer. The mod is compiled against 0.7.7's `HytaleServer.jar`.
- To build from source: a **JDK 25 or newer**, because Hytale's server is built for Java 25, plus **Gradle**.

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

### Turn it on in a world

The mod's ID is `TBFox:Jogging`. Enable it in the world's settings in-game, or add it to `Saves/<World>/config.json`:

```json
"Mods": {
  "TBFox:Jogging": { "Enabled": true }
}
```

Hytale only loads mods when a world starts. After installing a new build, restart the world; you don't need to restart the whole game.

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

## Known limitations

- **No dedicated key.** Server mods can't add keybinds, so jogging is toggled with commands.
- **Brief swing animation.** When an action is locked, the client may start the animation for a moment before the server cancels it. No damage or effect happens.
- **State isn't saved.** Jogging state is reset when you leave the game or the world restarts. Leaving while winded unlocks you when you come back.
- **Rebuild after game updates.** The mod is compiled against a specific server version. If a Hytale update breaks it (for example `NoSuchMethodError` in the server log), rebuild against the new `HytaleServer.jar`.
