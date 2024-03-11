package com.linxiao.framework.common

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import androidx.annotation.RawRes

object SoundPlayHelper {
    internal enum class VolumeType(val value: Int) {
        RING(2),
        MEDIA(3)
    }

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .build()
    private val soundPoolMap = HashMap<Int, Int?>()

    private var volumeType = VolumeType.MEDIA

    fun release() {
        soundPool.release()
    }

    /**
     * play raw sound resources
     * @param res resources id of raw sound
     * @param times loop times, -1 means loop forever
     */
    @Synchronized
    @JvmOverloads
    fun playSoundRes(@RawRes res: Int, times: Int = 0) {
        if (soundPoolMap[res] == null) {
            soundPool.setOnLoadCompleteListener { soundPool: SoundPool, sampleId: Int, status: Int ->
                soundPool.setOnLoadCompleteListener(null)
                playSound(res, times)
            }
            soundPoolMap[res] = soundPool.load(globalContext, res, 1)
            return
        }
        playSound(res, times)
    }

    /**
     * 播放声音
     *
     * @param soundId 所添加声音的编号
     * @param times   循环次数，0:不循环，-1:永远循环
     */
    @Synchronized
    private fun playSound(soundId: Int, times: Int = 0) {
        // 实例化AudioManager对象
        val am = globalContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 返回当前AudioManager对象播放所选声音的类型的最大音量值
        val maxVolume = am.getStreamMaxVolume(volumeType.value).toFloat()
        // 返回当前AudioManager对象的音量值
        val currentVolume = am.getStreamVolume(volumeType.value).toFloat()
        // 比值
        val volumeRatio = currentVolume / maxVolume
        val sound = soundPoolMap[soundId] ?: return
        soundPool.play(sound, volumeRatio, volumeRatio, 1, times, 1f)
    }
}