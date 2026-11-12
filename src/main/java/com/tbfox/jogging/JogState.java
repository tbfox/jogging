package com.tbfox.jogging;

/** One player's jogging state. */
public final class JogState {
    public enum Mode { NORMAL, JOGGING, RECOVERING }

    private Mode mode = Mode.NORMAL;

    /**
     * Bumped every time the mode changes. A recovery timer remembers the value it started with,
     * so a timer left over from an earlier stop can tell it's stale and do nothing.
     */
    private int generation;

    public synchronized Mode mode() {
        return mode;
    }

    public synchronized int generation() {
        return generation;
    }

    public synchronized void setMode(Mode mode) {
        this.mode = mode;
        generation++;
    }

    /** Ends recovery only if nothing has changed since the timer was started. */
    public synchronized boolean finishRecovery(int expectedGeneration) {
        if (mode != Mode.RECOVERING || generation != expectedGeneration) {
            return false;
        }
        setMode(Mode.NORMAL);
        return true;
    }
}
