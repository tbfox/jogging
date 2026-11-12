package com.tbfox.jogging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class JoggingPlugin extends JavaPlugin {
    private static final double DEFAULT_RECOVERY_SECONDS = 5;

    public JoggingPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        double recoverySeconds = readRecoverySeconds();
        getLogger().at(Level.INFO).log("Jogging recovery time: %s seconds", recoverySeconds);
        getCommandRegistry().registerCommand(new JogCommand(recoverySeconds));
    }

    /** Reads "RecoverySeconds" from this mod's own manifest.json, falling back to the default. */
    private double readRecoverySeconds() {
        try (JarFile jar = new JarFile(getFile().toFile());
             Reader reader = new InputStreamReader(
                     jar.getInputStream(jar.getEntry("manifest.json")), StandardCharsets.UTF_8)) {
            JsonObject manifest = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement value = manifest.get("RecoverySeconds");
            if (value != null && value.getAsDouble() >= 0) {
                return value.getAsDouble();
            }
            getLogger().at(Level.WARNING).log("RecoverySeconds missing or negative in manifest.json; using %s",
                    DEFAULT_RECOVERY_SECONDS);
        } catch (Exception e) {
            getLogger().at(Level.WARNING).log("Couldn't read RecoverySeconds from manifest.json (%s); using %s",
                    e, DEFAULT_RECOVERY_SECONDS);
        }
        return DEFAULT_RECOVERY_SECONDS;
    }
}
