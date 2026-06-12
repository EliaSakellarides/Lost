package com.lost.audio;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Gestisce la riproduzione audio per Lost
 */
public class AudioManager {
    private Clip backgroundMusic;
    private boolean musicEnabled;
    private float volume;
    
    /** Crea il gestore audio con musica abilitata e volume al massimo. */
    public AudioManager() {
        this.musicEnabled = true;
        this.volume = 1.0f;  // Volume al massimo!
    }
    
    /**
     * Riproduce un brano in loop continuo.
     * @param filename nome del file audio in /music/
     */
    public void playBackgroundMusic(String filename) {
        playBackgroundMusic(filename, true, 0);
    }
    
    /**
     * Riproduce musica con opzioni
     * @param filename nome del file audio
     * @param loop se true ripete in loop, altrimenti una volta sola
     * @param stopAfterMs ferma dopo N millisecondi (0 = mai)
     */
    public void playBackgroundMusic(String filename, boolean loop, int stopAfterMs) {
        if (!musicEnabled) return;
        
        try {
            stopBackgroundMusic();
            
            // Cerca il file audio dal classpath
            InputStream audioStream = getClass().getResourceAsStream("/music/" + filename);
            
            if (audioStream != null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(audioStream));
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(ais);
                setVolume(volume);
                
                if (loop) {
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
                backgroundMusic.start();
                
                // Ferma dopo N millisecondi se specificato
                if (stopAfterMs > 0) {
                    startDaemonThread(() -> {
                        try {
                            Thread.sleep(stopAfterMs);
                            fadeOutAndStop(2000); // Fade out di 2 secondi
                        } catch (InterruptedException e) {
                            // Interrotto
                        }
                    }, "lost-audio-stop");
                }
            }
        } catch (Exception e) {
            // Audio non disponibile, continua senza musica
            System.out.println("Audio non disponibile: " + e.getMessage());
        }
    }
    
    /**
     * Fade out graduale e poi stop
     * @param durationMs durata del fade out in millisecondi
     */
    public void fadeOutAndStop(int durationMs) {
        if (backgroundMusic == null || !backgroundMusic.isRunning()) return;
        
        startDaemonThread(() -> {
            try {
                float originalVolume = volume;
                int steps = 20;
                int stepDelay = durationMs / steps;
                
                for (int i = steps; i >= 0; i--) {
                    setVolume(originalVolume * i / steps);
                    Thread.sleep(stepDelay);
                }
                
                stopBackgroundMusic();
                setVolume(originalVolume); // Ripristina volume per prossima volta
            } catch (Exception e) {
                stopBackgroundMusic();
            }
        }, "lost-audio-fade");
    }
    
    /** Ferma e chiude la clip in riproduzione, se presente. */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            backgroundMusic.close();
            backgroundMusic = null;
        }
    }
    
    /**
     * Imposta il volume della musica, limitato all'intervallo 0-1.
     * @param vol volume desiderato (0 = muto, 1 = massimo)
     */
    public void setVolume(float vol) {
        this.volume = Math.max(0f, Math.min(1f, vol));
        if (backgroundMusic != null) {
            try {
                FloatControl gainControl = (FloatControl) 
                    backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                // Controllo volume non disponibile
            }
        }
    }
    
    /** Abilita/disabilita la musica; se disabilitata ferma la riproduzione. */
    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (!musicEnabled) {
            stopBackgroundMusic();
        }
    }

    private void startDaemonThread(Runnable task, String name) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        thread.start();
    }
    
    /** {@return true se la musica e' abilitata} */
    public boolean isMusicEnabled() { return musicEnabled; }
    /** {@return il volume corrente (0-1)} */
    public float getVolume() { return volume; }
}
