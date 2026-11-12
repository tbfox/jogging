package com.tbfox.jogging;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.event.InteractionChainStartEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.EnumSet;
import java.util.Set;

/**
 * Cancels player activities while jogging, and afterwards until the stamina bar has refilled.
 * Uses the same hook as the game's own portal "curse", which stops players using items.
 */
public final class ActivityLockSystem extends EntityEventSystem<EntityStore, InteractionChainStartEvent> {

    /** Attack/mine, block/place/eat, weapon abilities, guarding, and E-to-use (doors, chests). */
    private static final Set<InteractionType> LOCKED = EnumSet.of(
            InteractionType.Primary,
            InteractionType.Secondary,
            InteractionType.Ability1,
            InteractionType.Ability2,
            InteractionType.Ability3,
            InteractionType.Ability4,
            InteractionType.Wielding,
            InteractionType.Use);

    private static final long MESSAGE_COOLDOWN_MS = 2_000;

    private final JogStates states;
    private final Query<EntityStore> query;

    public ActivityLockSystem(JogStates states) {
        super(InteractionChainStartEvent.class);
        this.states = states;
        this.query = Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, InteractionChainStartEvent event) {
        if (!LOCKED.contains(event.getType())) {
            return;
        }
        PlayerRef player = chunk.getComponent(index, PlayerRef.getComponentType());
        if (player == null) {
            return;
        }
        JogState state = states.get(player.getUuid());
        if (!state.actionsLocked()) {
            return;
        }

        event.setCancelled(true);
        if (state.shouldSendBlockedMessage(System.currentTimeMillis(), MESSAGE_COOLDOWN_MS)) {
            String text = state.mode() == JogState.Mode.JOGGING
                    ? "You can't do that while jogging."
                    : "You're still catching your breath.";
            player.sendMessage(Message.raw(text));
        }
    }
}
