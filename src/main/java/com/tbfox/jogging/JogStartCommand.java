package com.tbfox.jogging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/** /jog start */
public final class JogStartCommand extends AbstractPlayerCommand {
    private final JogStates states;

    public JogStartCommand(JogStates states) {
        super("start", "Start jogging.");
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
        if (state.mode() == JogState.Mode.JOGGING) {
            player.sendMessage(Message.raw("You are already jogging."));
            return;
        }

        // Starting again while recovering skips the rest of the recovery.
        state.setMode(JogState.Mode.JOGGING);
        player.sendMessage(Message.raw("You have started jogging."));
    }
}
