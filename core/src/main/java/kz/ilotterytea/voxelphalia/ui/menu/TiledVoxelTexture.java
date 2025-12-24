package kz.ilotterytea.voxelphalia.ui.menu;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.voxels.Voxel;

public class TiledVoxelTexture implements Disposable {

    private final Texture texture;
    private TextureRegion region;

    public TiledVoxelTexture(Voxel voxel) {
        region = voxel.getMaterial().getFrontTextureRegion(VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class));

        if (!region.getTexture().getTextureData().isPrepared()) {
            region.getTexture().getTextureData().prepare();
        }

        Pixmap terrainPixmap = region.getTexture().getTextureData().consumePixmap();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(terrainPixmap, 0, 0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

        texture = new Texture(pixmap);
        terrainPixmap.dispose();
        pixmap.dispose();

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture, 0, 0, texture.getWidth() * 16, texture.getHeight() * 16);
    }

    public static TiledVoxelTexture random() {
        VoxelphaliaGame game = VoxelphaliaGame.getInstance();
        Voxel voxel = game.getVoxelRegistry().getEntries().get((byte) MathUtils.random(1, game.getVoxelRegistry().getEntries().size() - 1));
        return new TiledVoxelTexture(voxel);
    }

    public TextureRegion getRegion() {
        return this.region;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
