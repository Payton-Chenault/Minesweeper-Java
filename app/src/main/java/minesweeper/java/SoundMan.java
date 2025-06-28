package minesweeper.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundMan {
    private static final Logger logger = Logger.getLogger(SoundMan.class.getName());
    private final Map<App.Audios, Clip> audioClips = new HashMap<>();
    private boolean inError = false;

    static {
        try {

            File logDir = new File("logs");
            if(!logDir.exists()) {
                logDir.mkdirs();
            }

            FileHandler handler = new FileHandler("logs/audiolog.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(App.Audios audio, String path) {
        try (InputStream is = App.class.getResourceAsStream(path)) {
            if (is == null) {
                logger.log(Level.SEVERE, String.format("Audio Not Found! -> %s", path));
                inError = true;
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            audioClips.put(audio, clip);
            logger.log(Level.INFO, String.format("%s was saved under the audio %s", path, audio.toString()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Failed to load audio %s from %s", audio.toString(), path), e);
        }
    }

    public void play(App.Audios audio) {
        if(inError) {logger.log(Level.INFO, "Sound Manager Is In Error State, Canceling Playback"); return;}
        if(audioClips.get(audio) == null) {logger.log(Level.WARNING, String.format("Invalid Audio %s", audio.toString())); return;}
        Clip clip = audioClips.get(audio);
        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    public void closeAll() {
        for (Clip clip : audioClips.values()) {
            clip.close();
        }
    }
}
