package kz.ilotterytea.voxelphalia.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import kz.ilotterytea.voxelphalia.utils.Renderable;

public class RenderableEntity extends Entity implements Renderable {
    protected Decal decal;

    public RenderableEntity() {
        super();
        this.decal = null;
    }

    @Override
    public void render(DecalBatch batch) {
        Renderable.super.render(batch);
        if (this.decal != null) batch.add(decal);
    }

    @Override
    public void tick(float delta, Camera camera) {
        super.tick(delta, camera);

        Vector3 lookAt = camera.position.cpy();
        lookAt.y = this.position.y;

        if (this.decal != null) this.decal.lookAt(lookAt, Vector3.Y);
    }

    @Override
    public void setDirection(float x, float y, float z) {
        super.setDirection(x, y, z);
        if (this.decal != null) this.decal.lookAt(direction.cpy().scl(1f, 0f, 1f).add(0f, position.y, 0f), Vector3.Y);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        if (this.decal != null) this.decal.setPosition(position);
    }

    protected void setDecal(TextureRegion region, float width, float height) {
        this.decal = Decal.newDecal(width, height, region, true);
    }

    public TextureRegion getTextureRegion() {
        return this.decal.getTextureRegion();
    }

    public void setColor(Color color) {
        this.decal.setColor(color);
    }
}
