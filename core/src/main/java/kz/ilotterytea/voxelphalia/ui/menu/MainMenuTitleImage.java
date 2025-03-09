package kz.ilotterytea.voxelphalia.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class MainMenuTitleImage extends Image {
    private final Texture texture;
    public MainMenuTitleImage() {
        String text = """
            /#...#..####..#...#.###.#....####.#..#.#####.#....#####.#####
            .#...#.#....#..#.#..#...#....#..#.#..#.#...#.#......#...#...#
            .#...#.#....#...#...###.#....####.####.#####.#......#...#####
            ..#.#..#....#..#.#..#...#....#....#..#.#...#.#......#...#...#
            ...#../.####/.#...#/###/####/#.../#..#/#...#/####/#####/#...#
            ..............................................................
            """;
        String[] lines = text.split("\n");

        // generating texture from terrain.png
        VoxelphaliaGame game = VoxelphaliaGame.getInstance();

        int blockWidth = 16, blockHeight = 16;
        Pixmap titlePixmap = new Pixmap(
            lines[0].trim().length() * blockWidth,
            lines.length * blockHeight,
            Pixmap.Format.RGBA8888
        );

        // generating a random texture for every letter
        int charX = 0;
        int charY = 0;
        Pixmap blockPixmap = null;
        TextureRegion blockRegion = null;

        while (charX + charY < text.length()) {
            if (charY >= lines.length) {
                charX++;
                charY = 0;
            }

            String line = lines[charY].trim();
            if (charX >= line.length()) break;
            char c = line.charAt(charX);
            charY++;

            switch (c) {
                // change texture
                case '/': {
                    Voxel voxel = game.getVoxelRegistry().getEntry("cobblestone");

                    blockRegion = voxel.getMaterial().getFrontTextureRegion(
                        game.getAssetManager().get("textures/terrain.png", Texture.class)
                    );

                    if (blockPixmap == null) {
                        if (!blockRegion.getTexture().getTextureData().isPrepared()) {
                            blockRegion.getTexture().getTextureData().prepare();
                        }

                        blockPixmap = blockRegion.getTexture().getTextureData().consumePixmap();
                    }
                    break;
                }
                // letter
                case '#': {
                    if (blockPixmap == null) {
                        Gdx.app.log(getClass().getSimpleName(), "Attempt to draw a letter without a texture");
                        break;
                    }

                    titlePixmap.drawPixmap(
                        blockPixmap,
                        blockRegion.getRegionWidth() * charX,
                        blockRegion.getRegionHeight() * charY,

                        blockRegion.getRegionX(),
                        blockRegion.getRegionY(),
                        blockRegion.getRegionWidth(),
                        blockRegion.getRegionHeight()
                    );
                    break;
                }
                default: break;
            }
        }

        this.texture = new Texture(titlePixmap);

        if (blockPixmap != null) blockPixmap.dispose();
        titlePixmap.dispose();

        setDrawable(new TextureRegionDrawable(texture));
    }
}
