package com.aiedevice.sdkdemo.utils;

public class VolumeHelper {

    public static final int[] VOLUME_VALUES = new int[]{50, 55, 65, 75, 85, 90, 100};

    public static int getVolumeStep(int volume) {
        for (int i = 0; i < VOLUME_VALUES.length; i++) {
            if (volume <= VOLUME_VALUES[i]) {
                return i;
            }
        }
        return VOLUME_VALUES.length - 1;
    }

    public static int volumeAdd(int volume) {
        int volumeStep = getVolumeStep(volume);
        volumeStep += 1;
        if (volumeStep >= VOLUME_VALUES.length) {
            volumeStep = VOLUME_VALUES.length - 1;
        }
        return VOLUME_VALUES[volumeStep];
    }

    public static int volumeReduce(int volume) {
        int volumeStep = getVolumeStep(volume);
        volumeStep -= 1;
        if (volumeStep < 0) {
            volumeStep = 0;
        }
        return VOLUME_VALUES[volumeStep];
    }


}
