package com.anas.jconsoleaudioplayer.player;

// TODO: Move this enum from this packge to com.anas.jconsoleaudioplayer.userinterface
public enum Action {
    PLAY, STOP, PAUSE, RESUME, REST,
    MUTE, UN_MUTE, VOLUME_UP, VOLUME_DOWN, SHOW_VOLUME_LEVEL,
    LOOP_ON_ONE_CLIP, LOOP_ON_PLAY_LIST,
    SHUFFLE,
    NEXT, PREVIOUS,
    HOTKEYS_ON, HOTKEYS_OFF,
    SEARCH,
    OPEN_FILE_BROWSER,
    UNKNOWN,
    EXIT, SET_VOLUME, LOOP_ON_ONE_CLIP_ONE_TIME,
}
