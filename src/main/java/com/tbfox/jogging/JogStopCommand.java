package com.tbfox.jogging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.concurrent.TimeUnit;

/** /jog stop */
public final class JogStopCommand extends AbstractPlayerCommand {
    private final JogStates states;
    private final long recoveryMillis;

    public JogStopCommand(JogStates states, double recoverySeconds) {
        super("stop", "Stop jogging.");
        requireNoPermission();
        this.states = states;
        this.recoveryMillis = Math.round(recoverySeconds * 1000);
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

        state.setMode(JogState.Mode.RECOVERING);
        int generation = state.generation();
        player.sendMessage(Message.raw("You have stopped jogging."));

        // After the recovery time, hop back onto the world thread and finish recovering.
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            if (!world.isAlive()) {
                return;
            }
            world.execute(() -> {
                if (state.finishRecovery(generation) && player.isValid()) {
                    player.sendMessage(Message.raw("You have recovered from jogging."));
                }
            });
        }, recoveryMillis, TimeUnit.MILLISECONDS);
    }
}
