package com.tbfox.jogging;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/** /jog groups the jogging subcommands. On its own it only shows usage. */
public final class JogCommand extends AbstractCommandCollection {

    public JogCommand() {
        super("jog", "Jogging commands.");
        requireNoPermission();
        addSubCommand(new JogStartCommand());
        addSubCommand(new JogStopCommand());
    }
}
