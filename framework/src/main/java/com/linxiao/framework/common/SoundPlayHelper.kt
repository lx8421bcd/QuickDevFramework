package com.linxiao.framework.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.annotation.RawRes;

import java.util.HashMap;


public final class SoundEffectPlayUtil {

    enum VolumeType {
        RING(2), MEDIA(3);

        public final int value;
        VolumeType(int value) {
            this.value = value;
        }
    }

    private static SoundEffectPlayUtil defaultHelper;

    public static SoundEffectPlayUtil getDefault() {
        synchronized (SoundEffectPlayUtil.class) {
            if (defaultHelper == null) {
                defaultHelper = new SoundEffectPlayUtil();
            }
        }
        return defaultHelper;
    }

    private final SoundPool soundPool = new SoundPool.Builder()
            .setMaxStreams(5)
            .build();
    private final HashMap<Integer, Integer> soundPoolMap = new HashMap<>();
    private VolumeType volumeType = VolumeType.MEDIA;


    public void setVolume(VolumeType volumeType) {
        this.volumeType = volumeType;
    }

    public void release() {
        soundPool.release();
    }

    /**
     * play raw sound resources
     * @param res resources id of raw sound
     * @param times loop times, -1 means loop forever
     */
    public synchronized void playSoundRes(@RawRes int res, int times) {
        if (soundPoolMap.get(res) == null) {
            soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                soundPool.setOnLoadCompleteListener(null);
                playSound(res, times);
            });
            soundPoolMap.put(res, soundPool.load(ContextProviderKt.getGlobalContext(), res, 1));
            return;
        }
        playSound(res, times);
    }

    public synchronized void playSoundRes(@RawRes int res) {
        playSoundRes(res, 1);
    }

    /**
     * 播放声音
     *
     * @param soundId 所添加声音的编号
     * @param times   循环次数，0:不循环，-1:永远循环
     */
    private void playSound(int soundId, int times) {
        // 实例化AudioManager对象
        AudioManager am = (AudioManager) ContextProviderKt.getGlobalContext().getSystemService(Context.AUDIO_SERVICE);
        // 返回当前AudioManager对象播放所选声音的类型的最大音量值
        float maxVolume = am.getStreamMaxVolume(volumeType.value);
        // 返回当前AudioManager对象的音量值
        float currentVolume = am.getStreamVolume(volumeType.value);
        // 比值
        float volumeRatio = currentVolume / maxVolume;
        Integer sound = soundPoolMap.get(soundId);
        if (sound == null) {
            return;
        }
        soundPool.play(sound, volumeRatio, volumeRatio, 1, times, 1);
    }
}