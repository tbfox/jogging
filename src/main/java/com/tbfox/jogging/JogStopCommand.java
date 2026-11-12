package com.tbfox.jogging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/** /jog stop */
public final class JogStopCommand extends AbstractPlayerCommand {
    private final JogStates states;

    public JogStopCommand(JogStates states) {
        super("stop", "Stop jogging.");
        requireNoPermission();
        this.states = states;
    }

    @Override
    protected void execute(
            CommandContext context,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef player,
            World world) {

        JogState state = states.get(player.getUuid());
        if (state.mode() != JogState.Mode.JOGGING) {
            player.sendMessage(Message.raw("You are not jogging."));
            return;
        }

        // Actions stay locked until the stamina bar refills (see JogTickSystem).
        state.setMode(JogState.Mode.RECOVERING);
        player.sendMessage(Message.raw("You have stopped jogging."));

        JogEffects.drainStamina(store.getComponent(ref, EntityStatMap.getComponentType()));
        JogEffects.applyNormalSpeed(store.getComponent(ref, MovementManager.getComponentType()), player);
    }
}
