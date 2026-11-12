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
        if (mode == Mode.RECOVERING) {
            recoveredStamina = 0f;
        }
    }

    /** How much stamina has come back so far during recovery. */
    private float recoveredStamina;

    public synchronized float recoveredStamina() {
        return recoveredStamina;
    }

    public synchronized void setRecoveredStamina(float value) {
        recoveredStamina = value;
    }

    /** Actions are locked while jogging and until stamina has refilled afterwards. */
    public synchronized boolean actionsLocked() {
        return mode != Mode.NORMAL;
    }

    /** The HUD label currently on the player's screen (null = none), so we only send changes. */
    private JogHud.Label shownLabel;

    public synchronized JogHud.Label shownLabel() {
        return shownLabel;
    }

    public synchronized void setShownLabel(JogHud.Label label) {
        shownLabel = label;
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
