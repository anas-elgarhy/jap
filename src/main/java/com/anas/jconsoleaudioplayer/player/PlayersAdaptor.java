package com.anas.jconsoleaudioplayer.player;

import com.anas.jconsoleaudioplayer.player.players.MainAudioPlayer;
import com.anas.jconsoleaudioplayer.playlist.EndPlayListException;
import com.anas.jconsoleaudioplayer.playlist.PlayList;
import com.anas.jconsoleaudioplayer.userinterface.playerinterface.PlayerInterface;

import javax.sound.sampled.LineEvent;
import java.util.Arrays;

public class PlayersAdaptor implements SuPlayer {
    // Singleton
    private static PlayersAdaptor playersAdaptor;
    private Player[] players;
    private Player currentPlayer;
    private PlayList playList;
    private Loop loopOnTrack;
    private double soundVolume, soundVolumeBeforeMute;
    private boolean paused, muted;

    private PlayersAdaptor() {
        players = new Player[0]; // No players
        this.soundVolume = 0.5;
        addPlayers(MainAudioPlayer.getInstance()); // Add the players here
        currentPlayer = players[0];
        loopOnTrack = Loop.NO_LOOP;
    }

    public static PlayersAdaptor getInstance() {
        if (playersAdaptor == null) {
            playersAdaptor = new PlayersAdaptor();
        }
        return playersAdaptor;
    }

    private void setAdapterOfAllPlayers() {
        for (Player player : players) {
            player.setPlayersAdaptor(this);
        }
    }

    public void play() {
        setTheCurrentPlayersToThePestPlayerForTheCurrentTrack();
        new Thread(() -> {
            try {
                if (!currentPlayer.isRunning()) {
                    currentPlayer.play(playList.playCurrentTrack());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        setVolume(soundVolume);
    }

    private void setTheCurrentPlayersToThePestPlayerForTheCurrentTrack() {
        if (players.length == 0) {
            throw new IllegalStateException("No players");
        }
        if (players.length > 1) { // if there are more than one player
            for (Player player : players) { // Get the supported player for the current file
                if (player.isSupportedFile(playList.getItems()[playList.getCurrentIndex()].getFile())) {
                    currentPlayer = player;
                    break;
                }
            }
            throw new IllegalStateException("No player for the current file");
        }
    }

    public void stop() {
        currentPlayer.stop();
    }

    @Override
    public void pause() {
        if (currentPlayer.isRunning())
            currentPlayer.pause();
        paused = true;
    }

    @Override
    public void resume() {
        if (currentPlayer.isRunning()) {
            setVolume(soundVolume);
            currentPlayer.resume();
        }
        paused = false;
    }

    /**
     * Enable and disable looping of the play list
     */
    public void loopOfPlayList() {
        playList.setLooping(!playList.isLooping()); // toggle looping
    }

    /**
     * Enable and disable looping of the current player
     */
    public void shuffle() {
        playList.setShuffling(!playList.isShuffling()); // toggle shuffling
    }

    /**
     * Change to the next song in the playlist
     */
    public void next() throws EndPlayListException {
        if (currentPlayer.isRunning())
            currentPlayer.stop();
        playList.played();
        playList.next();
        if (!isPaused())
            this.play();
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * Change to the previous song in the playlist
     */
    public void previous() throws EndPlayListException {
        if (currentPlayer.isRunning())
            currentPlayer.stop();
        playList.played();
        playList.previous();
        if (!isPaused())
            this.play();
    }

    /**
     * Mute and unmute the song
     */
    public void mute() {
        if (!muted) {
            soundVolumeBeforeMute = soundVolume;
            soundVolume = 0;
            muted = true;
        } else {
            soundVolume = soundVolumeBeforeMute;
            muted = false;
        }
        setVolume(soundVolume);
    }

    @Override
    public double getVolume() {
        return soundVolume;
    }

    @Override
    public void setVolume(double volume) {
        if (!(volume < 0.0 || volume > 1.0)) {
            this.soundVolume = volume;
            currentPlayer.setVolume(soundVolume);
        }
    }

    @Override
    public void addPositionListener(PositionListener positionListener) {
        for (Player player : players) {
            player.addPositionListener(positionListener);
        }
    }

    @Override
    public void removePositionListener(PositionListener positionListener) {
        for (Player player : players) {
            player.removePositionListener(positionListener);
        }
    }

    @Override
    public void exit() {
        currentPlayer.exit();
    }

    /**
     * Get the play list
     *
     * @return PlayList
     */
    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    /**
     * Get the current player
     *
     * @return Player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void event(PlayerEvent event) {
        if (event ==  PlayerEvent.END_OF_MEDIA) {
            playList.played();
            playList.getItems()[playList.getCurrentIndex()].setPlaying(false);
            checkLoopOfTrack();
            PlayerInterface.getInstance().rePrint();
        }
    }

    private void checkLoopOfTrack() {
        switch (loopOnTrack) {
            case LOOP_ONE_TIME -> {
                this.stop();
                this.play();
                loopOnTrack = Loop.NO_LOOP;
            }
            case LOOP -> {
                this.stop();
                this.play();
            }
            case NO_LOOP -> {
                try {
                    next();
                } catch (EndPlayListException ignored) {}
            }
        }
    }

    public final void addPlayers(Player... players) {
        this.players = players;
        setAdapterOfAllPlayers();
    }

    public Extension[] getSupportedExtensions() {
        Extension[] extensions = new Extension[0];
        for (Player player : players) {
            extensions = Arrays.copyOf(extensions, extensions.length + player.getSupportedExtensions().length);
            System.arraycopy(player.getSupportedExtensions(), 0, extensions,
                    extensions.length - player.getSupportedExtensions().length,
                    player.getSupportedExtensions().length);
        }
        return extensions;
    }

    public Loop getLoopOnTrack() {
        return loopOnTrack;
    }

    public void setLoopOnTrack(Loop loopOnTrack) {
        if (this.loopOnTrack == loopOnTrack) {
            this.loopOnTrack = Loop.NO_LOOP;
        } else {
            this.loopOnTrack = loopOnTrack;
        }
    }

    /**
     * Skip 10 seconds forward in the current track
     */
    public void skip10SecondsForward() {
        this.seekTo(10);
    }

    /**
     * Skip 10 seconds backward in the current track
     */

    public void skip10SecondsBackward() {
        this.seekTo(-10);
    }

    public void seekTo(int seconds) {
        try {
            if (seconds > 0) {
                this.currentPlayer.seekTo(seconds);
            } else {
                this.currentPlayer.seekToSeconds(seconds);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
