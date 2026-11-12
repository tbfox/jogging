package com.tbfox.jogging;

import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.StatChangeDirection;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/** What jogging does to a player's movement and stamina, and how to undo it. */
public final class JogEffects {
    private JogEffects() {}

    // ---- Speed: running moves you at sprint speed ----

    /** Makes normal running as fast as sprinting. Only sends an update if something changed. */
    public static void applySprintSpeed(MovementManager movement, PlayerRef player) {
        if (movement == null) {
            return;
        }
        MovementSettings settings = movement.getSettings();
        if (settings == null || settings.forwardRunSpeedMultiplier == settings.forwardSprintSpeedMultiplier) {
            return;
        }
        settings.forwardRunSpeedMultiplier = settings.forwardSprintSpeedMultiplier;
        movement.update(player.getPacketHandler());
    }

    /** Puts running speed back to the game's default. */
    public static void restoreSpeed(MovementManager movement, PlayerRef player) {
        if (movement == null) {
            return;
        }
        MovementSettings settings = movement.getSettings();
        MovementSettings defaults = movement.getDefaultSettings();
        if (settings == null || defaults == null) {
            return;
        }
        settings.forwardRunSpeedMultiplier = defaults.forwardRunSpeedMultiplier;
        movement.update(player.getPacketHandler());
    }

    // ---- Stamina: never goes down while jogging ----

    public static void freezeStamina(EntityStatMap stats) {
        if (stats == null) {
            return;
        }
        int stamina = DefaultEntityStatTypes.getStamina();
        stats.setStatChangeSuppressed(stamina, StatChangeDirection.LOWER, true);
        stats.maximizeStatValue(stamina);
    }

    /** Backup for freezeStamina: refills stamina if anything managed to lower it. */
    public static void topUpStamina(EntityStatMap stats) {
        if (stats == null) {
            return;
        }
        int stamina = DefaultEntityStatTypes.getStamina();
        EntityStatValue value = stats.get(stamina);
        if (value != null && value.get() < value.getMax()) {
            stats.maximizeStatValue(stamina);
        }
    }

    // ---- Stopping: you're winded until the bar refills ----

    /** Lets stamina change normally again, then empties it to zero. */
    public static void drainStamina(EntityStatMap stats) {
        if (stats == null) {
            return;
        }
        int stamina = DefaultEntityStatTypes.getStamina();
        stats.setStatChangeSuppressed(stamina, StatChangeDirection.LOWER, false);
        stats.setStatValue(stamina, 0f);
    }

    /** True once the stamina bar is back to full. */
    public static boolean isStaminaFull(EntityStatMap stats) {
        if (stats == null) {
            return true;
        }
        EntityStatValue value = stats.get(DefaultEntityStatTypes.getStamina());
        return value == null || value.get() >= value.getMax();
    }
}
