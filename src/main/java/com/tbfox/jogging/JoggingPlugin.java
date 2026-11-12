package com.tbfox.jogging;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class JoggingPlugin extends JavaPlugin {
    private final JogStates states = new JogStates();

    public JoggingPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new JogCommand(states));
        getEntityStoreRegistry().registerSystem(new JogTickSystem(states));
        getEntityStoreRegistry().registerSystem(new ActivityLockSystem(states));
        getEventRegistry().register(PlayerDisconnectEvent.class,
                event -> states.remove(event.getPlayerRef().getUuid()));
    }
}
