package kz.ilotterytea.voxelphalia.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class MusicPlayer implements Tickable {
    public enum Type {
        GAME
    }

    private final Array<Music> music;
    private final Array<Integer> usedMusic;
    private final float minDelaySec, maxDelaySec;
    private int index;
    private float delay, maxDelay;
    private boolean playing;

    public MusicPlayer(Type type) {
        this(type, 300f, 600f);
    }

    public MusicPlayer(Type type, float minDelaySec, float maxDelaySec) {
        this.minDelaySec = minDelaySec;
        this.maxDelaySec = maxDelaySec;
        this.music = new Array<>();
        this.usedMusic = new Array<>();

        AssetManager assetManager = VoxelphaliaGame.getInstance().getAssetManager();
        FileHandle handle = Gdx.files.internal("assets.txt");
        String folderPath = "music/" + type.name().toLowerCase() + "/";

        for (String path : handle.readString().split("\n")) {
            if (!path.startsWith(folderPath)) continue;

            Music m = assetManager.get(path);
            music.add(m);
        }

        maxDelay = MathUtils.random(minDelaySec, maxDelaySec);

        if (music.isEmpty()) {
            throw new GdxRuntimeException("No music loaded");
        }

        index = MathUtils.random(0, music.size - 1);
        usedMusic.add(index);
    }

    @Override
    public void tick(float delta) {
        if (!playing) return;
        else if (delay != -1 && delay < maxDelay) {
            delay += delta;
            return;
        } else if (delay >= maxDelay) {
            delay = -1;
            if (usedMusic.size >= music.size) usedMusic.clear();
            do {
                index = MathUtils.random(0, music.size - 1);
            } while (usedMusic.contains(index, false));
            usedMusic.add(index);
            play();
        }

        Music currentMusic = getCurrentMusic();
        if (!currentMusic.isPlaying()) {
            delay = 0;
            maxDelay = MathUtils.random(minDelaySec, maxDelaySec);
        }
    }

    public void play() {
        playing = true;
        this.music.get(index).play();
    }

    public void pause() {
        playing = false;
        this.music.get(index).pause();
    }

    public void resetAll() {
        for (Music m : music) {
            m.pause();
            m.setPosition(0.0f);
        }
    }

    public Music getCurrentMusic() {
        return this.music.get(index);
    }
}
