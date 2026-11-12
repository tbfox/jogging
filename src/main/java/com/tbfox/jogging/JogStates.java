package com.tbfox.jogging;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Jogging state for every online player, keyed by player UUID. Kept in memory only. */
public final class JogStates {
    private final Map<UUID, JogState> states = new ConcurrentHashMap<>();

    public JogState get(UUID player) {
        return states.computeIfAbsent(player, id -> new JogState());
    }

    public void remove(UUID player) {
        states.remove(player);
    }
}
