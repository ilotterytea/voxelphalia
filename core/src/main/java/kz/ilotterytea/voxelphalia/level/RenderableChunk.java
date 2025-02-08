package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Renderable;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class RenderableChunk implements Disposable, Tickable, Renderable {
    private final Chunk chunk;
    private final Vector3 offset;
    private ModelInstance modelInstance;

    public RenderableChunk(Chunk chunk, Vector3 offset) {
        this.chunk = chunk;
        this.offset = offset;
    }

    @Override
    public void tick(float delta) {
        if (chunk.isDirty) {
            rebuildModel();
            chunk.isDirty = false;
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (modelInstance == null) return;
        batch.render(modelInstance, environment);
    }

    @Override
    public void dispose() {
        if (modelInstance != null) {
            modelInstance.model.dispose();
            modelInstance = null;
        }
    }

    private void rebuildModel() {
        Gdx.app.log("RenderableChunk" + offset, "Rebuilding chunk model...");
        long startMilliseconds = System.currentTimeMillis();

        Texture terrainTexture = VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class);

        ModelBuilder modelBuilder = new ModelBuilder();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part(
            "block",
            GL20.GL_TRIANGLES,
            attr,
            new Material(TextureAttribute.createDiffuse(terrainTexture))
        );

        int i = 0;
        byte[] voxels = chunk.voxels;
        int width = chunk.width;
        int height = chunk.height;
        int depth = chunk.depth;

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = voxels[i];
                    VoxelType type = VoxelType.getById(voxel);
                    if (type == VoxelType.AIR) continue;

                    TextureRegion region = type.getTextureRegion(terrainTexture);

                    if (y < height - 1) {
                        if (voxels[i + width * depth] == 0) createTop(builder, x, y, z, region);
                    } else {
                        createTop(builder, x, y, z, region);
                    }
                    if (y > 0) {
                        if (voxels[i - width * depth] == 0)
                            createBottom(builder, x, y, z, region);
                    } else {
                        createBottom(builder, x, y, z, region);
                    }
                    if (x > 0) {
                        if (voxels[i - 1] == 0)
                            createLeft(builder, x, y, z, region);
                    } else {
                        createLeft(builder, x, y, z, region);
                    }
                    if (x < width - 1) {
                        if (voxels[i + 1] == 0)
                            createRight(builder, x, y, z, region);
                    } else {
                        createRight(builder, x, y, z, region);
                    }
                    if (z > 0) {
                        if (voxels[i - width] == 0)
                            createFront(builder, x, y, z, region);
                    } else {
                        createFront(builder, x, y, z, region);
                    }
                    if (z < depth - 1) {
                        if (voxels[i + width] == 0)
                            createBack(builder, x, y, z, region);
                    } else {
                        createBack(builder, x, y, z, region);
                    }
                }
            }
        }

        dispose();
        Model model = modelBuilder.end();
        modelInstance = new ModelInstance(model);

        long elapsedMilliseconds = System.currentTimeMillis() - startMilliseconds;
        Gdx.app.log("RenderableChunk" + offset, String.format("Finished in %sms!", elapsedMilliseconds));
    }

    private void createTop(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x, offset.y + y + 1, offset.z + z,
            offset.x + x + 1, offset.y + y + 1, offset.z + z,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1,
            offset.x + x, offset.y + y + 1, offset.z + z + 1,
            0, 1, 0
        );
    }

    private void createBottom(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x, offset.y + y, offset.z + z,
            offset.x + x, offset.y + y, offset.z + z + 1,
            offset.x + x + 1, offset.y + y, offset.z + z + 1,
            offset.x + x + 1, offset.y + y, offset.z + z,
            0, -1, 0
        );
    }

    private void createLeft(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x, offset.y + y, offset.z + z,
            offset.x + x, offset.y + y + 1, offset.z + z,
            offset.x + x, offset.y + y + 1, offset.z + z + 1,
            offset.x + x, offset.y + y, offset.z + z + 1,
            -1, 0, 0
        );
    }

    private void createRight(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x + 1, offset.y + y, offset.z + z,
            offset.x + x + 1, offset.y + y, offset.z + z + 1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z,
            1, 0, 0
        );
    }

    private void createFront(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x, offset.y + y, offset.z + z,
            offset.x + x + 1, offset.y + y, offset.z + z,
            offset.x + x + 1, offset.y + y + 1, offset.z + z,
            offset.x + x, offset.y + y + 1, offset.z + z,
            0, 0, 1
        );
    }

    private void createBack(MeshPartBuilder builder, int x, int y, int z, TextureRegion region) {
        builder.setUVRange(region);
        builder.rect(
            offset.x + x, offset.y + y, offset.z + z + 1,
            offset.x + x, offset.y + y + 1, offset.z + z + 1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1,
            offset.x + x + 1, offset.y + y, offset.z + z + 1,
            0, 0, -1
        );
    }

    public Vector3 getOffset() {
        return offset;
    }
}
