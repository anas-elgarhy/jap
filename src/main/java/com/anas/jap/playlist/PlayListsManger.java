package com.anas.jap.playlist;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayListsManger implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private ArrayList<PlayList> playLists;
    private int currentPlayListIndex;
    private static PlayListsManger instance;

    private PlayListsManger() {
        init();
    }

    public static PlayListsManger getInstance() {
        if (instance == null) {
            instance = new PlayListsManger();
        }
        return instance;
    }

    public static void setInstance(PlayListsManger instance) {
        PlayListsManger.instance = instance;
    }

    private void init() {
        playLists = new ArrayList<>();
        playLists.add(new PlayList("Default", 0));
        currentPlayListIndex = 0;
    }

    public ArrayList<PlayList> getPlayLists() {
        return playLists;
    }

    public void addPlayList(PlayList playList) {
        playLists.add(playList);
    }


    public boolean removePlayList(PlayList playList) {
        return playLists.remove(playList);
    }

    public PlayList getPlayList(String name) throws IllegalStateException {
        for (PlayList playList : playLists) {
            if (playList.getName().equals(name)) {
                return playList;
            }
        }
        throw new IllegalStateException("PlayList not found");
    }

    public PlayList getPlayList(int index) throws IndexOutOfBoundsException {
        return playLists.get(index);
    }

    public PlayList getCurrentPlayList() {
        return playLists.get(currentPlayListIndex);
    }

    public void setCurrentPlayList(String name) {
        PlayList playList = getPlayList(name);
        if (playList != null) {
            currentPlayListIndex = playLists.indexOf(playList);
        }
    }

    public void setCurrentPlayList(PlayList playList) {
        if (playList == null)
            return;
        if (!playLists.contains(playList)) {
            playLists.add(playList);
        }
        currentPlayListIndex = playLists.indexOf(playList);
    }

    public void setCurrentPlayList(int index) {
        if (index < 0 || index >= playLists.size()) {
            throw new IndexOutOfBoundsException("index out of bounds");
        }
        currentPlayListIndex = index;
    }

    public void removePlayList(int index) {
        playLists.remove(index);
    }

    public void setPlayListName(int index, String name) {
        int preFix = 0;
        for (PlayList playList : playLists) {
            if (playList.getName().equals(name + (preFix == 0 ? "" : "_" + preFix))) {
                preFix++;
            }
        }
        playLists.get(index).setNameAndPrefix(name, preFix);
    }

    public void newPlayList(String name) {
        playLists.add(new PlayList(name, 0));
        this.setCurrentPlayList(playLists.get(playLists.size() - 1));
        this.setPlayListName(this.getCurrentPlayListIndex(), name);
    }

    public int getCurrentPlayListIndex() {
        return currentPlayListIndex;
    }

    public void updatePlayList(PlayList playList) {
        if (playLists.contains(playList)) {
            playLists.set(playLists.indexOf(playList), playList);
        } else {
            playLists.add(playList);
        }
    }
}
