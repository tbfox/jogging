# Getting Started Making a Hytale Mod

Notes put together on 2026-11-12. They're based on this Mac's Hytale install (server **0.7.7**) and the mods already in `UserData/Mods`.

---

## 1. What a mod actually is

A server mod (the game calls it a "plugin") is a Java `.jar` that contains:

- your compiled classes
- a `manifest.json` at the root of the jar

The game loads every jar in `UserData/Mods/`, and each world decides which ones are enabled.

Real manifests from installed mods, for reference:

```json
// EssentialsPlus: works on 0.7.7
{
  "Group": "fof1092",
  "Name": "EssentialsPlus",
  "Version": "1.21.0",
  "Main": "de.fof1092.essentialsplus.EssentialsPlus",
  "ServerVersion": ">=0.7.0",
  "Dependencies": {},
  "IncludesAssetPack": true
}
```

```json
// HomeManager: crashed on 0.7.7
{
  "Group": "HomeManager",
  "Name": "HomeManager",
  "Version": "1.3.0",
  "Main": "com.example.templateplugin.TemplatePlugin",
  "ServerVersion": "*",
  "IncludesAssetPack": true
}
```

Fields:
- **Group + Name**: the mod's ID, written as `Group:Name` (e.g. `fof1092:EssentialsPlus`). This is the key used in a world's `config.json`.
- **Main**: the full class name of your plugin's entry class.
- **ServerVersion**: the server versions you support. Use a real range like `">=0.7.0"`. **Don't use `"*"`.** It makes the game load your mod on versions it can't run on, which is exactly how HomeManager ended up crashing the world.
- **IncludesAssetPack**: `true` only if the jar also ships assets (models, textures, block/item definitions).

---

## 2. Important paths on this Mac

| What | Where |
|---|---|
| Server code to compile against | `~/Library/Application Support/Hytale/install/release/package/game/latest/Server/HytaleServer.jar` |
| Built-in game assets (reference) | `~/Library/Application Support/Hytale/install/release/package/game/latest/Assets.zip` |
| Java that ships with Hytale (Java 26) | `~/Library/Application Support/Hytale/install/release/package/jre/latest/Contents/Home/bin/java` |
| Where mod jars go | `~/Library/Application Support/Hytale/UserData/Mods/` |
| Your worlds | `~/Library/Application Support/Hytale/UserData/Saves/<World>/` |
| A world's enabled-mod list | `Saves/<World>/config.json` → `"Mods"` |
| A world's permissions | `Saves/<World>/permissions.json` |
| Server logs (your main debugging tool) | `Saves/<World>/logs/*.log` (newest = latest run) |

Gradle is already installed at `/opt/homebrew/bin/gradle`.

---

## 3. Tools

- **JDK**: compile against the same Java version the game runs (26). Install one with `brew install openjdk`, or point your IDE at Hytale's bundled JRE.
- **Editor**: IntelliJ IDEA Community (easiest for Java + Gradle), or VS Code with the "Extension Pack for Java".
- **Gradle**: already installed.

---

## 4. Project layout (suggested)

Keep your code in `~/Projects/...`, **not** inside `UserData`.

```
my-mod/
├── build.gradle
├── settings.gradle
├── install.sh                      # build + copy into Mods
└── src/main/
    ├── java/com/tbfox/mymod/
    │   └── MyMod.java              # the "Main" class
    └── resources/
        └── manifest.json
```

### build.gradle (sketch)

```groovy
plugins { id 'java' }

group = 'com.tbfox'
version = '0.1.0'

java {
    toolchain { languageVersion = JavaLanguageVersion.of(26) }
}

dependencies {
    // Compile against the game's server, but don't bundle it into your jar
    compileOnly files("${System.getProperty('user.home')}/Library/Application Support/Hytale/install/release/package/game/latest/Server/HytaleServer.jar")
}
```

### install.sh (sketch)

```bash
#!/bin/zsh
set -e
./gradlew build            # or: gradle build
cp build/libs/*.jar "$HOME/Library/Application Support/Hytale/UserData/Mods/"
echo "Installed. Enable it in your test world's config.json, then load the world."
```

### manifest.json

```json
{
  "Group": "TBFox",
  "Name": "MyMod",
  "Version": "0.1.0",
  "Description": "My first Hytale mod",
  "Main": "com.tbfox.mymod.MyMod",
  "ServerVersion": ">=0.7.0",
  "IncludesAssetPack": false
}
```

---

## 5. Writing the code

- Your `Main` class extends the game's plugin base class, which has setup/start/shutdown hooks. You can see these phases in the logs: "HomeManager is setting up...", "has started successfully!", "is shutting down...".
- HomeManager was clearly built from an **official template project** (its package is `com.example.templateplugin`). Search for the Hytale plugin template / official modding docs and start from that rather than guessing class names.
- A good first goal: register a `/hello` command that sends you a chat message.
- Package names that showed up in the installed mods, useful for finding things in the server jar:
  - `com.hypixel.hytale.server.core.permissions.PermissionsModule` (groups and permissions)
  - `com.hypixel.hytale.server.core.modules.entity.component.TransformComponent` (entity position)
  - `com.hypixel.hytale.server.core.Message` (chat messages)
  - `com.hypixel.hytale.math.vector.Vector3d`

### Browsing the server API without docs

```bash
# list classes in the server jar
unzip -l ".../Server/HytaleServer.jar" | grep -i command

# show the methods on a class
javap -cp ".../Server/HytaleServer.jar" com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
```

### Learning from mods that work

`javap -c -p` on classes from a mod that works on your version (e.g. EssentialsPlus) shows exactly which game methods it calls:

```bash
mkdir -p /tmp/ep && cd /tmp/ep && unzip -oq ~/Library/Application\ Support/Hytale/UserData/Mods/EssentialsPlus-1.21.0.jar
javap -c -p path/to/SomeClass.class | grep invoke
```

---

## 6. Testing loop

1. **Make a throwaway test world** in the game (don't test on TBFox). An exception on the world thread shuts down the whole world.
2. Build and copy the jar: `./install.sh`
3. Enable the mod in `Saves/<TestWorld>/config.json`:
   ```json
   "Mods": {
     "TBFox:MyMod": { "Enabled": true }
   }
   ```
   (or switch it on in the in-game world settings)
4. Give yourself admin in `Saves/<TestWorld>/permissions.json` if needed:
   ```json
   { "users": { "<your-uuid>": { "groups": ["hytale:Admin"] } } }
   ```
   Your UUID is `e9f81990-1ec3-4ad5-a1ee-42fcf614f8cd` (from TBFox's permissions file).
5. Load the world and try your command.
6. **Read the newest log** in `Saves/<TestWorld>/logs/`:
   ```bash
   cd ~/Library/Application\ Support/Hytale/UserData/Saves/<TestWorld>/logs
   grep -n -E "MyMod|SEVERE|Exception|Caused by" "$(ls -t | head -1)"
   ```
   Things to look for:
   - `Enabled plugin TBFox:MyMod`: it loaded
   - `Exception ... potentially caused by TBFox:MyMod`: your crash, followed by the full error report
7. After every rebuild, **restart the world**. The server only loads jars at startup.

---

## 7. Lessons from the HomeManager / TPE crash

- `java.lang.NoSuchMethodError` means the mod was compiled against a different server version than the one running. HomeManager and Teleporting Essentials both called `TransformComponent.getPosition()` returning `Vector3d`, which the 0.7.7 server no longer has.
- Always compile against the `HytaleServer.jar` you're actually running, and rebuild after game updates.
- Set an honest `ServerVersion` range so the game refuses to load your mod on versions you haven't tested.
- Wrap command logic in try/catch while learning. An uncaught exception on the world thread brings down the whole world.

---

## 8. Next ideas once `/hello` works

- A command with arguments (e.g. `/greet <player>`)
- Reading and saving a config file in your mod's data folder (HomeManager used `Mods/homemanager-data/config.json`)
- Permission checks via `PermissionsModule`
- Listening for events (player join, chat, block break)
- Adding an asset pack (`IncludesAssetPack: true`) for custom blocks/items, using `Assets.zip` as reference
