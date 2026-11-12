package com.tbfox.jogging;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.StatChangeDirection;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/** What jogging does to a player's movement and stamina, and how to undo it. */
public final class JogEffects {
    private JogEffects() {}

    /** How long it takes to refill the stamina bar from empty after you stop jogging. */
    public static final float RECOVERY_SECONDS = 10f;

    // ---- Speed ----

    /**
     * Run and sprint speed must never be exactly equal: the game client crashes
     * ("value NaN is not finite") when they are, most likely by dividing by their difference.
     * So we always keep them at least this far apart (2%).
     */
    private static final float SPEED_GAP = 0.02f;

    /** Jogging: normal running moves you at (just under) sprint speed. */
    public static void applyJogSpeed(MovementManager movement, PlayerRef player) {
        MovementSettings defaults = defaults(movement);
        if (defaults != null) {
            float sprint = defaults.forwardSprintSpeedMultiplier;
            setSpeed(movement, player, sprint * (1f - SPEED_GAP), sprint);
        }
    }

    /** Normal: the game's default running and sprinting speeds. */
    public static void applyNormalSpeed(MovementManager movement, PlayerRef player) {
        MovementSettings defaults = defaults(movement);
        if (defaults != null) {
            setSpeed(movement, player, defaults.forwardRunSpeedMultiplier, defaults.forwardSprintSpeedMultiplier);
        }
    }

    private static MovementSettings defaults(MovementManager movement) {
        return movement == null ? null : movement.getDefaultSettings();
    }

    /** Only sends an update to the client if something actually changed. */
    private static void setSpeed(MovementManager movement, PlayerRef player, float run, float sprint) {
        MovementSettings settings = movement.getSettings();
        if (settings == null
                || (settings.forwardRunSpeedMultiplier == run && settings.forwardSprintSpeedMultiplier == sprint)) {
            return;
        }
        settings.forwardRunSpeedMultiplier = run;
        settings.forwardSprintSpeedMultiplier = sprint;
        movement.update(player.getPacketHandler());
    }

    // ---- Winded: no sprinting while recovering ----

    /** Our effect asset (src/main/resources/Server/Entity/Effects/TBFox_Jogging_Winded.json). */
    private static final String WINDED_EFFECT_ID = "TBFox_Jogging_Winded";

    /**
     * Adds or removes the "winded" effect, which disables sprinting.
     * Does nothing if the player already has (or doesn't have) it.
     */
    public static void setWinded(Ref<EntityStore> ref, EffectControllerComponent effects,
                                 ComponentAccessor<EntityStore> accessor, boolean winded) {
        if (effects == null) {
            return;
        }
        EntityEffect effect = EntityEffect.getAssetMap().getAsset(WINDED_EFFECT_ID);
        if (effect == null || effects.hasEffect(effect) == winded) {
            return;
        }
        if (winded) {
            effects.addEffect(ref, effect, accessor);
        } else {
            effects.removeEffect(ref, EntityEffect.getAssetMap().getIndex(WINDED_EFFECT_ID), accessor);
        }
    }

    // ---- Stamina while jogging: never goes down ----

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

    // ---- Stamina after jogging: empty, then a slow refill ----

    /** Lets stamina change normally again, then empties it to zero. */
    public static void drainStamina(EntityStatMap stats) {
        if (stats == null) {
            return;
        }
        int stamina = DefaultEntityStatTypes.getStamina();
        stats.setStatChangeSuppressed(stamina, StatChangeDirection.LOWER, false);
        stats.setStatValue(stamina, 0f);
    }

    /**
     * Refills stamina at a steady rate that takes RECOVERY_SECONDS from empty to full,
     * overriding the game's own (faster) refill. Returns true once the bar is full.
     */
    public static boolean recoverStamina(EntityStatMap stats, JogState state, float dt) {
        if (stats == null) {
            return true;
        }
        int stamina = DefaultEntityStatTypes.getStamina();
        EntityStatValue value = stats.get(stamina);
        if (value == null) {
            return true;
        }
        float max = value.getMax();
        float recovered = Math.min(max, state.recoveredStamina() + dt * max / RECOVERY_SECONDS);
        state.setRecoveredStamina(recovered);
        if (value.get() != recovered) {
            stats.setStatValue(stamina, recovered);
        }
        return recovered >= max;
    }
}
