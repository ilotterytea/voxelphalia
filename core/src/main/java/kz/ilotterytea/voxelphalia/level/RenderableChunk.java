package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Renderable;
import kz.ilotterytea.voxelphalia.utils.Tickable;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderableChunk implements Disposable, Tickable, Renderable {
    private final Level level;
    private final Chunk chunk;
    private final Vector3 offset;
    private ModelInstance modelInstance;
    private boolean rebuilding;
    private int meshVertexCount, meshIndexCount;

    public RenderableChunk(Chunk chunk, Level level, Vector3 offset) {
        this.chunk = chunk;
        this.level = level;
        this.offset = offset;
    }

    @Override
    public void tick(float delta) {
        if (chunk.isDirty || modelInstance == null) {
            runRebuildModelThread();
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

    private void runRebuildModelThread() {
        if (rebuilding) return;
        rebuilding = true;
        meshVertexCount = 0;
        meshIndexCount = 0;

        new Thread(() -> {
            Gdx.app.log("RenderableChunk" + offset, "Rebuilding chunk mesh...");
            long startTimestamp = System.currentTimeMillis();

            Pair<List<Float>, List<Short>> data = generateMeshData();
            meshVertexCount = data.first.size();
            meshIndexCount = data.second.size();

            Gdx.app.postRunnable(() -> {
                rebuildModel(data);
                rebuilding = false;
                Gdx.app.log("RenderableChunk" + offset, String.format("Finished in %sms!", System.currentTimeMillis() - startTimestamp));
            });
        }).start();
    }

    private Pair<List<Float>, List<Short>> generateMeshData() {
        Texture terrainTexture = VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class);

        List<Float> vertices = new ArrayList<>();
        List<Short> indices = new ArrayList<>();

        int width = chunk.width;
        int height = chunk.height;
        int depth = chunk.depth;

        short indexOffset = 0;

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    byte voxel = chunk.getVoxel(x, y, z);
                    if (voxel == 0) continue;

                    VoxelType type = VoxelType.getById(voxel);
                    TextureRegion sideRegion = type.getSideTextureRegion(terrainTexture);
                    TextureRegion topRegion = type.getTopTextureRegion(terrainTexture);
                    TextureRegion bottomRegion = type.getBottomTextureRegion(terrainTexture);

                    boolean[] faces = getVisibleFaces((int) (offset.x + x), (int) (offset.y + y), (int) (offset.z + z));

                    if (faces[0]) {
                        createTop(vertices, indices, x, y, z, topRegion, indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[1]) {
                        createBottom(vertices, indices, x, y, z, bottomRegion, indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[2]) {
                        createLeft(vertices, indices, x, y, z, sideRegion, indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[3]) {
                        createRight(vertices, indices, x, y, z, sideRegion, indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[4]) {
                        createFront(vertices, indices, x, y, z, sideRegion, indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[5]) {
                        createBack(vertices, indices, x, y, z, sideRegion, indexOffset);
                        indexOffset += 4;
                    }
                }
            }
        }

        return new Pair<>(vertices, indices);
    }

    private void rebuildModel(Pair<List<Float>, List<Short>> data) {
        float[] v = new float[data.first.size()];
        for (int i = 0; i < data.first.size(); i++) v[i] = data.first.get(i);
        short[] i = new short[data.second.size()];
        for (int j = 0; j < data.second.size(); j++) i[j] = data.second.get(j);

        Texture terrainTexture = VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class);

        ModelBuilder modelBuilder = new ModelBuilder();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part(
            "chunk",
            GL20.GL_TRIANGLES,
            attr,
            new Material(TextureAttribute.createDiffuse(terrainTexture))
        );
        builder.addMesh(v, i);

        dispose();
        modelInstance = new ModelInstance(modelBuilder.end());
    }

    private void createTop(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x, offset.y + y + 1, offset.z + z, 0f, 1f, 0f, u1, v1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z, 0f, 1f, 0f, u2, v1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1, 0f, 1f, 0f, u2, v2,
            offset.x + x, offset.y + y + 1, offset.z + z + 1, 0f, 1f, 0f, u1, v2
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private void createBottom(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x, offset.y + y, offset.z + z, 0f, -1f, 0f, u1, v1,
            offset.x + x, offset.y + y, offset.z + z + 1, 0f, -1f, 0f, u1, v2,
            offset.x + x + 1, offset.y + y, offset.z + z + 1, 0f, -1f, 0f, u2, v2,
            offset.x + x + 1, offset.y + y, offset.z + z, 0f, -1f, 0f, u2, v1
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private void createLeft(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x, offset.y + y, offset.z + z, -1f, 0f, 0f, u1, v2,
            offset.x + x, offset.y + y + 1, offset.z + z, -1f, 0f, 0f, u1, v1,
            offset.x + x, offset.y + y + 1, offset.z + z + 1, -1f, 0f, 0f, u2, v1,
            offset.x + x, offset.y + y, offset.z + z + 1, -1f, 0f, 0f, u2, v2
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private void createRight(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x + 1, offset.y + y, offset.z + z, 1f, 0f, 0f, u2, v2,
            offset.x + x + 1, offset.y + y, offset.z + z + 1, 1f, 0f, 0f, u1, v2,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1, 1f, 0f, 0f, u1, v1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z, 1f, 0f, 0f, u2, v1
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private void createFront(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x, offset.y + y, offset.z + z, 0f, 0f, 1f, u2, v2,
            offset.x + x + 1, offset.y + y, offset.z + z, 0f, 0f, 1f, u1, v2,
            offset.x + x + 1, offset.y + y + 1, offset.z + z, 0f, 0f, 1f, u1, v1,
            offset.x + x, offset.y + y + 1, offset.z + z, 0f, 0f, 1f, u2, v1
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private void createBack(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            offset.x + x, offset.y + y, offset.z + z + 1, 0f, 0f, -1f, u1, v2,
            offset.x + x, offset.y + y + 1, offset.z + z + 1, 0f, 0f, -1f, u1, v1,
            offset.x + x + 1, offset.y + y + 1, offset.z + z + 1, 0f, 0f, -1f, u2, v1,
            offset.x + x + 1, offset.y + y, offset.z + z + 1, 0f, 0f, -1f, u2, v2
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    public Vector3 getOffset() {
        return offset;
    }

    private boolean[] getVisibleFaces(int x, int y, int z) {
        boolean[] faces = new boolean[]{
            // top, bottom, left, right, front, back
            false, false, false, false, false, false
        };

        if (level.getVoxel(x, y + 1, z) == 0)
            faces[0] = true;
        if (level.getVoxel(x, y - 1, z) == 0)
            faces[1] = true;
        if (level.getVoxel(x - 1, y, z) == 0)
            faces[2] = true;
        if (level.getVoxel(x + 1, y, z) == 0)
            faces[3] = true;
        if (level.getVoxel(x, y, z - 1) == 0)
            faces[4] = true;
        if (level.getVoxel(x, y, z + 1) == 0)
            faces[5] = true;

        return faces;
    }

    public int getMeshIndexCount() {
        return meshIndexCount;
    }

    public int getMeshVertexCount() {
        return meshVertexCount;
    }
}
