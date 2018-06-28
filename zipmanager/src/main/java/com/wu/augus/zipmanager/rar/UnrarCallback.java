package com.wu.augus.zipmanager.rar;


public interface UnrarCallback {

    boolean isNextVolumeReady(Volume nextVolume);

    void volumeProgressChanged(long current, long total);
}
