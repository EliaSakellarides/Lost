package com.lostthesis.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

/**
 * Gestisce la riproduzione audio per Lost Thesis
 */
public class AudioManager {
    private Clip backgroundMusic;
    private boolean musicEnabled;
    private float volume;
    
    public AudioManager() {
        this.musicEnabled = true;
        this.volume = 1.0f;  // Volume al massimo!
    }
    
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
            
            // Cerca il file audio
            InputStream audioStream = getClass().getResourceAsStream("/assets/music/" + filename);
            if (audioStream == null) {
                // Prova percorso alternativo
                File audioFile = new File("assets/music/" + filename);
                if (audioFile.exists()) {
                    audioStream = new FileInputStream(audioFile);
                }
            }
            
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
                    new Thread(() -> {
                        try {
                            Thread.sleep(stopAfterMs);
                            fadeOutAndStop(2000); // Fade out di 2 secondi
                        } catch (InterruptedException e) {
                            // Interrotto
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            // Audio non disponibile, continua senza musica
            System.out.println("Audio non disponibile: " + e.getMessage());
        }
    }
    
    /**
     * Fade out graduale e poi stop
     */
    public void fadeOutAndStop(int durationMs) {
        if (backgroundMusic == null || !backgroundMusic.isRunning()) return;
        
        new Thread(() -> {
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
        }).start();
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
    
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
    
    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (!musicEnabled) {
            stopBackgroundMusic();
        }
    }
    
    public boolean isMusicEnabled() { return musicEnabled; }
    public float getVolume() { return volume; }
}
