package com.tbfox.jogging;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

/**
 * Every tick, for each player:
 * - jogging: keep stamina full and keep running at sprint speed (re-applied each tick so it
 *   survives things that reset you, like respawning);
 * - recovering: once the stamina bar is full again, unlock actions.
 */
public final class JogTickSystem extends EntityTickingSystem<EntityStore> {
    private final JogStates states;
    private final Query<EntityStore> query;

    public JogTickSystem(JogStates states) {
        this.states = states;
        this.query = Query.and(
                Player.getComponentType(),
                PlayerRef.getComponentType(),
                EntityStatMap.getComponentType(),
                MovementManager.getComponentType());
    }

    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    /** Run after the game's own stamina regen/drain, so we get the last word each tick. */
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, EntityStatsModule.PlayerRegenerateStatsSystem.class));
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<EntityStore> chunk,
                     Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef player = chunk.getComponent(index, PlayerRef.getComponentType());
        if (player == null) {
            return;
        }
        JogState state = states.get(player.getUuid());
        EntityStatMap stats = chunk.getComponent(index, EntityStatMap.getComponentType());

        switch (state.mode()) {
            case JOGGING -> {
                JogEffects.topUpStamina(stats);
                JogEffects.applySprintSpeed(chunk.getComponent(index, MovementManager.getComponentType()), player);
            }
            case RECOVERING -> {
                if (JogEffects.isStaminaFull(stats)) {
                    state.setMode(JogState.Mode.NORMAL);
                    player.sendMessage(Message.raw("You have recovered from jogging."));
                }
            }
            case NORMAL -> { }
        }
    }
}
