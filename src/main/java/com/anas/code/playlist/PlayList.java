package com.anas.code.playlist;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PlayList {
    private ListItem[] list;
    private int currentIndex;
    private boolean looping, shuffling;


    public PlayList() {
        list = new ListItem[0];
        currentIndex = 0;
        looping = false;
        shuffling = false;
    }

    public void add(ListItem item) {
        item.setPlayed(false);
        item.setIndex(list.length - 1);
        if (!contains(item)) {
            ListItem[] newList = new ListItem[list.length + 1];
            System.arraycopy(list, 0, newList, 0, list.length);
            newList[newList.length - 1] = item;
            list = newList;
        }
    }

    private boolean contains(ListItem item) {
        for (ListItem listItem : list) {
            if (listItem.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void addAll(ListItem[] items) {
        if (items != null && items.length > 0) {
            ListItem[] newList = deleteContains(items);
            setUpItems(newList);
            newList = new ListItem[list.length + newList.length];
            System.arraycopy(list, 0, newList, 0, list.length);
            System.arraycopy(items, 0, newList, list.length, items.length);
            list = newList;
        }
    }

    private void setUpItems(ListItem[] newList) {
        for (int i = 0; i < newList.length; i++) {
            newList[i].setIndex(i + list.length); // Set the index of the item
            newList[i].setPlayed(false); // Set the item as not played
        }
    }

    public void remove(int index) {
        // Remove the item at the specified index
        ListItem[] newList = new ListItem[list.length - 1];
        System.arraycopy(list, 0, newList, 0, index);
        System.arraycopy(list, index + 1, newList, index, list.length - index - 1);
        list = newList;
    }

    private ListItem[] deleteContains(ListItem[] newItems) {
        ListItem[] newList = new ListItem[1];
        if (list.length > 0) {
            for (ListItem item : newItems) {
                boolean found = false;
                for (ListItem listItem : list) {
                    if (listItem.equals(item)) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // If not found, add to current play list
                    ListItem[] temp = Arrays.copyOf(newList, newList.length + 1);
                    temp[temp.length - 1] = item;
                    newList = temp;
                }
            }
        } else {
            newList = newItems; // If no items in list, just add the new items
        }
        return newList;
    }

    public ListItem get(int index) {
        return list[index];
    }

    public int size() {
        return list.length;
    }

    public void next() {
        if (currentIndex == list.length - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
    }

    public void previous() {
        if (currentIndex == 0) {
            currentIndex = list.length - 1;
        } else {
            currentIndex--;
        }
    }

    public PlayList shuffle() {
        int randomIndex = currentIndex;
        while (randomIndex == currentIndex) {
            randomIndex = (int) (Math.random() * list.length);
        }
        // Check if the random index is played before
        boolean end = false;
        if (list[randomIndex].isPlayed() && !(end = isEnded())) {
            return shuffle();
        } else if (end && looping) {
            reset();
            return shuffle();
        }
        currentIndex = randomIndex;
        return this;
    }

    public void played() {
        list[currentIndex].setPlayed(true);
    }

    public void reset() {
        for (ListItem item : list) {
            item.setPlayed(false);
        }
    }

    public boolean isEnded() {
        boolean ended = true;
        for (ListItem item : list) {
            if (!item.isPlayed()) {
                ended = false;
                break;
            }
        }
        return ended;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public void print() {
        print(-1);
    }

    public void print(int from) {
        if (from == -1) // Print the play list from current index
            from = currentIndex;
        if (list.length > 0) {
            // Print the first 10 elements of the playlist from the current index
            for (int i = from; i < from + 10; i++) {
                ListItem item = getItems()[i];
                if (i < getItems().length) {
                    System.out.println((item.isPlaying()? "> " : "") + i + ": " + (item.isPlayed()? "[Played] " : "") + item.getFileName());
                }
            }
        } else {
            System.out.println("Empty play list");
        }
    }

    public AudioInputStream getAudioInputStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(play());
    }

    private File play() {
        if (shuffling) {
            shuffle();
        }
        return list[currentIndex].getFile();
    }

    public void setShuffling(boolean shuffling) {
        this.shuffling = shuffling;
    }

    public boolean isShuffling() {
        return shuffling;
    }

    public int search(String name){
        for (int i = 0; i < list.length; i++) {
            if (list[i].getFileName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getCurrentIndex() {
        if (list.length > 0)
            return currentIndex;
        return -1;
    }

    public ListItem[] getItems() {
        return list;
    }
}
