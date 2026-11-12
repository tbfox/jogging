package com.tbfox.jogging;

/** One player's jogging state. */
public final class JogState {
    public enum Mode { NORMAL, JOGGING, RECOVERING }

    private Mode mode = Mode.NORMAL;
    private long lastBlockedMessageMs;

    public synchronized Mode mode() {
        return mode;
    }

    public synchronized void setMode(Mode mode) {
        this.mode = mode;
    }

    /** Actions are locked while jogging and until stamina has refilled afterwards. */
    public synchronized boolean actionsLocked() {
        return mode != Mode.NORMAL;
    }

    /** Rate-limits the "can't do that" message so spam-clicking doesn't flood chat. */
    public synchronized boolean shouldSendBlockedMessage(long now, long cooldownMs) {
        if (now - lastBlockedMessageMs < cooldownMs) {
            return false;
        }
        lastBlockedMessageMs = now;
        return true;
    }
}
