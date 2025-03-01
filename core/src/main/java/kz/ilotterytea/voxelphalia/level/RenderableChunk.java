package kz.ilotterytea.voxelphalia.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Identifier;
import kz.ilotterytea.voxelphalia.utils.Tickable;
import kz.ilotterytea.voxelphalia.utils.tuples.Pair;
import kz.ilotterytea.voxelphalia.voxels.Voxel;
import kz.ilotterytea.voxelphalia.voxels.VoxelMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderableChunk implements Disposable, Tickable, RenderableProvider {
    private final Level level;
    protected final Chunk chunk;
    private Mesh mesh;
    private final Material material;
    private boolean rebuilding;
    private int meshVertexCount, meshIndexCount;

    public RenderableChunk(Chunk chunk, Level level) {
        this.chunk = chunk;
        this.level = level;

        Texture terrainTexture = VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class);
        this.material = new Material(TextureAttribute.createDiffuse(terrainTexture));
    }

    @Override
    public void tick(float delta) {
        if (chunk.isDirty || mesh == null) {
            runRebuildMeshThread();
            chunk.isDirty = false;
        }

        for (Voxel voxel : chunk.voxelStates) {
            if (voxel == null) continue;
            voxel.tick(delta);
        }
    }

    @Override
    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
            mesh = null;
        }
    }

    private void runRebuildMeshThread() {
        if (rebuilding) return;
        rebuilding = true;
        meshVertexCount = 0;
        meshIndexCount = 0;

        new Thread(() -> {
            Gdx.app.debug("RenderableChunk" + chunk.offset, "Rebuilding chunk mesh...");
            long startTimestamp = System.currentTimeMillis();

            Pair<Pair<List<Float>, List<Short>>, Integer> data = generateMeshData();
            meshVertexCount = data.first.first.size();
            meshIndexCount = data.first.second.size();

            Gdx.app.postRunnable(() -> {
                rebuildMesh(data);
                rebuilding = false;
                Gdx.app.debug("RenderableChunk" + chunk.offset, String.format("Finished in %sms!", System.currentTimeMillis() - startTimestamp));
            });
        }).start();
    }

    private Pair<Pair<List<Float>, List<Short>>, Integer> generateMeshData() {
        Texture terrainTexture = VoxelphaliaGame.getInstance()
            .getAssetManager().get("textures/terrain.png", Texture.class);

        List<Float> vertices = new ArrayList<>();
        List<Short> indices = new ArrayList<>();

        short indexOffset = 0;
        int faceCount = 0;

        for (int y = 0; y < chunk.size; y++) {
            for (int z = 0; z < chunk.size; z++) {
                for (int x = 0; x < chunk.size; x++) {
                    Identifier v = chunk.getVoxel(x, y, z);
                    if (v == null) continue;

                    Voxel voxel = VoxelphaliaGame.getInstance()
                        .getVoxelRegistry()
                        .getEntry(v);

                    VoxelMaterial vm = voxel.getMaterial();

                    boolean[] faces = getVisibleFaces((int) (chunk.offset.x + x), (int) (chunk.offset.y + y), (int) (chunk.offset.z + z));
                    faceCount += faces.length;

                    if (faces[0]) {
                        createTop(vertices, indices, x, y, z, vm.getTopTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[1]) {
                        createBottom(vertices, indices, x, y, z, vm.getBottomTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[2]) {
                        createLeft(vertices, indices, x, y, z, vm.getLeftTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[3]) {
                        createRight(vertices, indices, x, y, z, vm.getRightTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[4]) {
                        createFront(vertices, indices, x, y, z, vm.getFrontTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }

                    if (faces[5]) {
                        createBack(vertices, indices, x, y, z, vm.getBackTextureRegion(terrainTexture), indexOffset);
                        indexOffset += 4;
                    }
                }
            }
        }

        return new Pair<>(new Pair<>(vertices, indices), faceCount);
    }

    private void rebuildMesh(Pair<Pair<List<Float>, List<Short>>, Integer> data) {
        float[] v = new float[data.first.first.size()];
        for (int i = 0; i < data.first.first.size(); i++) v[i] = data.first.first.get(i);
        short[] i = new short[data.first.second.size()];
        for (int j = 0; j < data.first.second.size(); j++) i[j] = data.first.second.get(j);

        dispose();
        mesh = new Mesh(true, data.second * 4, data.second * 6,
            VertexAttribute.Position(),
            VertexAttribute.Normal(),
            VertexAttribute.TexCoords(0)
        );

        mesh.setVertices(v);
        mesh.setIndices(i);
    }

    private void createTop(List<Float> vertices, List<Short> indices, int x, int y, int z, TextureRegion region, short indexOffset) {
        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        vertices.addAll(Arrays.asList(
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z, 0f, 1f, 0f, u1, v1,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z, 0f, 1f, 0f, u2, v1,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z + 1, 0f, 1f, 0f, u2, v2,
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z + 1, 0f, 1f, 0f, u1, v2
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
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z, 0f, -1f, 0f, u1, v1,
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z + 1, 0f, -1f, 0f, u1, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z + 1, 0f, -1f, 0f, u2, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z, 0f, -1f, 0f, u2, v1
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
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z, -1f, 0f, 0f, u1, v2,
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z, -1f, 0f, 0f, u1, v1,
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z + 1, -1f, 0f, 0f, u2, v1,
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z + 1, -1f, 0f, 0f, u2, v2
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
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z, 1f, 0f, 0f, u2, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z + 1, 1f, 0f, 0f, u1, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z + 1, 1f, 0f, 0f, u1, v1,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z, 1f, 0f, 0f, u2, v1
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
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z, 0f, 0f, 1f, u2, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z, 0f, 0f, 1f, u1, v2,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z, 0f, 0f, 1f, u1, v1,
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z, 0f, 0f, 1f, u2, v1
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
            chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z + 1, 0f, 0f, -1f, u1, v2,
            chunk.offset.x + x, chunk.offset.y + y + 1, chunk.offset.z + z + 1, 0f, 0f, -1f, u1, v1,
            chunk.offset.x + x + 1, chunk.offset.y + y + 1, chunk.offset.z + z + 1, 0f, 0f, -1f, u2, v1,
            chunk.offset.x + x + 1, chunk.offset.y + y, chunk.offset.z + z + 1, 0f, 0f, -1f, u2, v2
        ));

        indices.addAll(Arrays.asList(
            indexOffset, (short) (indexOffset + 1), (short) (indexOffset + 2),
            indexOffset, (short) (indexOffset + 2), (short) (indexOffset + 3)
        ));
    }

    private boolean[] getVisibleFaces(int x, int y, int z) {
        boolean[] faces = new boolean[]{
            // top, bottom, left, right, front, back
            false, false, false, false, false, false
        };

        if (level.getVoxel(x, y + 1, z) == null)
            faces[0] = true;
        if (level.getVoxel(x, y - 1, z) == null)
            faces[1] = true;
        if (level.getVoxel(x - 1, y, z) == null)
            faces[2] = true;
        if (level.getVoxel(x + 1, y, z) == null)
            faces[3] = true;
        if (level.getVoxel(x, y, z - 1) == null)
            faces[4] = true;
        if (level.getVoxel(x, y, z + 1) == null)
            faces[5] = true;

        return faces;
    }

    public int getMeshIndexCount() {
        return meshIndexCount;
    }

    public int getMeshVertexCount() {
        return meshVertexCount;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if (mesh == null) return;

        Preferences p = VoxelphaliaGame.getInstance().getPreferences();

        Renderable renderable = pool.obtain();
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.primitiveType = p.getInteger("render-mode", 0) == 0 ? GL20.GL_TRIANGLES : GL20.GL_LINE_STRIP;
        renderable.meshPart.size = mesh.getNumIndices();

        renderable.material = material;
        renderables.add(renderable);
    }
}
