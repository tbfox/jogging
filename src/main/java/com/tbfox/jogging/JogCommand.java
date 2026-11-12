package com.tbfox.jogging;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/** /jog sends a chat message. */
public final class JogCommand extends AbstractPlayerCommand {

    public JogCommand() {
        super("jog", "Start jogging.");
        requireNoPermission();
    }

    @Override
    protected void execute(
            CommandContext context,
            Store<EntityStore> store,
            Ref<EntityStore> ref,
            PlayerRef player,
            World world) {

        player.sendMessage(Message.raw("You have started jogging."));
    }
}
