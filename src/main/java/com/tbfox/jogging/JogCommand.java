package com.tbfox.jogging;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/** /jog groups the jogging subcommands. On its own it only shows usage. */
public final class JogCommand extends AbstractCommandCollection {

    public JogCommand(JogStates states, double recoverySeconds) {
        super("jog", "Jogging commands.");
        requireNoPermission();
        addSubCommand(new JogStartCommand(states));
        addSubCommand(new JogStopCommand(states, recoverySeconds));
    }
}
