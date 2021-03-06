package com.anas.jap.player;

public interface PositionListener {
    /**
     * Called when the position of the song changes.
     *
     * @param position the new position
     */
    void onPositionChanged(AudioPosition position);
}
