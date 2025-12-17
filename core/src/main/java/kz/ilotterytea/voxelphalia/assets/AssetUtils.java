package kz.ilotterytea.voxelphalia.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetUtils {
    public static void queue(AssetManager assetManager) {
        FileHandle assetsFile = Gdx.files.internal("assets.txt");
        String contents = assetsFile.readString();
        String[] filePaths = contents.split("\n");

        for (String filePath : filePaths) {
            String[] splitFilePath = filePath.split("/");
            String[] splitFileName = splitFilePath[splitFilePath.length - 1].split("\\.");
            String extension = splitFileName[splitFileName.length - 1];

            if (filePath.startsWith("music/")) extension = "mp3";

            Class<?> type = switch (extension) {
                case "png" -> Texture.class;
                case "atlas" -> TextureAtlas.class;
                case "skin" -> Skin.class;
                case "wav", "mp3" -> Music.class;
                case "ogg" -> Sound.class;
                default -> null;
            };

            if (type == null) {
                continue;
            }

            assetManager.load(filePath, type);
        }
    }
}
