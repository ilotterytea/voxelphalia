package kz.ilotterytea.voxelphalia.environment;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.level.Level;
import kz.ilotterytea.voxelphalia.utils.registries.VoxelRegistry;

public class InfiniteOcean implements RenderableProvider, Disposable {
    private Mesh mesh;
    private final Material material;
    private final Texture texture;
    private final Rectangle[] rectangles;
    private final float y;

    public InfiniteOcean(Level level, float y) {
        VoxelRegistry voxels = VoxelphaliaGame.getInstance().getVoxelRegistry();
        Texture atlas = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/terrain.png");

        TextureRegion r = voxels.getEntry("water").getMaterial().getTopTextureRegion(atlas);

        if (!r.getTexture().getTextureData().isPrepared()) {
            r.getTexture().getTextureData().prepare();
        }

        Pixmap terrainPixmap = r.getTexture().getTextureData().consumePixmap();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(terrainPixmap, 0, 0, r.getRegionX(), r.getRegionY(), r.getRegionWidth(), r.getRegionHeight());

        texture = new Texture(pixmap);
        terrainPixmap.dispose();
        pixmap.dispose();

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        material = new Material(TextureAttribute.createDiffuse(texture));

        float w = level.getWidthInVoxels(), size = 128f;
        this.rectangles = new Rectangle[4];

        this.rectangles[0] = new Rectangle(-size, 0, size, w);
        this.rectangles[1] = new Rectangle(w, 0, size, w);
        this.rectangles[2] = new Rectangle(-size, w, w + size * 2f, size);
        this.rectangles[3] = new Rectangle(-size, -size, w + size * 2f, size);

        this.y = y;

        rebuildMesh();
    }

    private void rebuildMesh() {
        if (mesh != null) {
            mesh.dispose();
            mesh = null;
        }

        float[] v = new float[rectangles.length * 4 * 8];
        short[] i = new short[rectangles.length * 6];
        int vo = 0, io = 0;
        short vb = 0;

        for (Rectangle r : rectangles) {
            float u1 = 0;
            float v1 = 0;
            float u2 = r.width;
            float v2 = r.height;

            v[vo++] = r.x;
            v[vo++] = y;
            v[vo++] = r.y;
            v[vo++] = 0;
            v[vo++] = 1f;
            v[vo++] = 0;
            v[vo++] = u1;
            v[vo++] = v1;

            v[vo++] = r.x + r.width;
            v[vo++] = y;
            v[vo++] = r.y;
            v[vo++] = 0;
            v[vo++] = 1f;
            v[vo++] = 0;
            v[vo++] = u2;
            v[vo++] = v1;

            v[vo++] = r.x + r.width;
            v[vo++] = y;
            v[vo++] = r.y + r.height;
            v[vo++] = 0;
            v[vo++] = 1f;
            v[vo++] = 0;
            v[vo++] = u2;
            v[vo++] = v2;

            v[vo++] = r.x;
            v[vo++] = y;
            v[vo++] = r.y + r.height;
            v[vo++] = 0;
            v[vo++] = 1f;
            v[vo++] = 0;
            v[vo++] = u1;
            v[vo++] = v2;

            i[io++] = vb;
            i[io++] = (short) (vb + 1);
            i[io++] = (short) (vb + 2);
            i[io++] = vb;
            i[io++] = (short) (vb + 2);
            i[io++] = (short) (vb + 3);

            vb += 4;
        }

        mesh = new Mesh(true, rectangles.length * 4, rectangles.length * 6, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        mesh.setVertices(v);
        mesh.setIndices(i);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        Renderable renderable = pool.obtain();
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.size = mesh.getNumIndices();
        renderable.material = material;
        renderables.add(renderable);
    }

    @Override
    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
            mesh = null;
        }
        texture.dispose();
    }
}
