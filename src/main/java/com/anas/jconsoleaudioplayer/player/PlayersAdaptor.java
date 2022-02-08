package com.anas.jconsoleaudioplayer.player;

import com.anas.jconsoleaudioplayer.player.players.WAVPlayer;
import com.anas.jconsoleaudioplayer.playlist.PlayList;
import com.anas.jconsoleaudioplayer.userinterface.player.PlayerInterface;

import javax.sound.sampled.LineEvent;
import java.io.File;
import java.util.Arrays;

public class PlayersAdaptor implements SuPlayer {
    // Singleton
    private static PlayersAdaptor playersAdaptor;
    private Player[] players;
    private Player currentPlayer;
    private PlayList playList;
    private double soundVolume, soundVolumeBeforeMute;

    private PlayersAdaptor() {
        players = new Player[0]; // No players
        this.soundVolume = 0.5;
        addPlayers(WAVPlayer.getInstance()); // Add the players here
        currentPlayer = players[0];
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
        if (players.length == 0)
            throw new IllegalStateException("No players");

        if (players.length > 1) { // if there are more than one player
            for (Player player : players) { // Get the supported player for the current file
                if (player.isSupportedFile(playList.getItems()[playList.getCurrentIndex()].getFile())) {
                    currentPlayer = player;
                    break;
                }
            }
        }
        // TODO: Re implement this
        new Thread(() -> {
            try {
                currentPlayer.play(playList.getCurrentTrack().getFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void stop() {
        currentPlayer.stop();
    }

    @Override
    public void pause() {
        currentPlayer.pause();
    }

    @Override
    public void resume() {
        currentPlayer.resume();
    }

    @Override
    public void loop() {
        currentPlayer.loop();
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
    public void next() {
        currentPlayer.stop();
        playList.next();
        this.play();
    }

    /**
     * Change to the previous song in the playlist
     */
    public void previous() {
        currentPlayer.stop();
        playList.previous();
        this.play();
    }

    @Override
    public void mute() {
        currentPlayer.mute();
        soundVolumeBeforeMute = soundVolume;
        soundVolume = 0;
    }

    @Override
    public double getVolume() {
        return soundVolume;
    }

    @Override
    public void setVolume(double volume) {
        this.soundVolume = volume;
        if (currentPlayer != null)
            currentPlayer.setVolume(soundVolume);
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


    public void event(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            playList.played();
            playList.getItems()[playList.getCurrentIndex()].setPlaying(false);
            next();
            PlayerInterface.getInstance().rePrint();
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
            System.arraycopy(player.getSupportedExtensions(), 0, extensions, extensions.length - player.getSupportedExtensions().length, player.getSupportedExtensions().length);
        }
        return extensions;
    }
}
