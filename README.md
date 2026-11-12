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

You need a Hytale server on version **0.7.7** or newer.

1. Put `Jogging-<version>.jar` in your Hytale `UserData/Mods` folder. On macOS that's `~/Library/Application Support/Hytale/UserData/Mods/`.
2. Turn the mod on for your world, either in the world's settings in-game, or by adding it to `Saves/<World>/config.json`:

   ```json
   "Mods": {
     "TBFox:Jogging": { "Enabled": true }
   }
   ```

3. Restart the world. Mods load when a world starts, so you don't need to restart the whole game.

## Good to know

- **No jog key.** Mods can't add keybinds, so you start and stop jogging with commands.
- **Brief swing animation.** If you try a locked action, your character may start the animation for a moment before it's cancelled. Nothing actually happens.
- **Jogging resets when you leave.** If you leave the game or the world restarts, you come back in the normal state, even if you left while winded.
- **Stopped working after a Hytale update?** The mod may need a new build for the new game version.

Building from source, tweaking values, and how the code works are covered in [docs/development.md](docs/development.md).
