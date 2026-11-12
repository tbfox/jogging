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
    private final long recoveryMillis;

    public JogStopCommand(double recoverySeconds) {
        super("stop", "Stop jogging.");
        requireNoPermission();
        this.recoveryMillis = Math.round(recoverySeconds * 1000);
    }

    @Override
    protected void execute(
            CommandContext context,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef player,
            World world) {

        player.sendMessage(Message.raw("You have stopped jogging."));

        // After the recovery time, hop back onto the world thread and send the follow-up.
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            if (!world.isAlive()) {
                return;
            }
            world.execute(() -> {
                if (player.isValid()) {
                    player.sendMessage(Message.raw("You have recovered from jogging."));
                }
            });
        }, recoveryMillis, TimeUnit.MILLISECONDS);
    }
}
