package kz.ilotterytea.voxelphalia.environment;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.utils.Tickable;

public class SkyClouds implements RenderableProvider, Disposable, Tickable, kz.ilotterytea.voxelphalia.utils.Renderable {
    private PerspectiveCamera camera;
    private Mesh mesh;
    private final Material material;
    private final TextureRegion region;
    private final Vector3 position, scale;

    public SkyClouds(Vector3 position, Vector3 scale) {
        camera = new PerspectiveCamera();
        camera.far = 10000f;
        camera.near = 0.1f;
        camera.update();

        Texture texture = VoxelphaliaGame.getInstance()
            .getAssetManager()
            .get("textures/environment/clouds.png");

        region = new TextureRegion(texture, 0, 0, 64, 64);

        material = new Material(TextureAttribute.createDiffuse(texture));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        this.position = position;
        this.scale = scale;

        this.position.sub(scale.x / 2f, scale.y / 2f, scale.z / 2f);

        rebuildMesh();
    }

    @Override
    public void tick(float delta, Camera camera) {
        if (camera instanceof PerspectiveCamera c) {
            this.camera.fieldOfView = c.fieldOfView;
            this.camera.viewportWidth = c.viewportWidth;
            this.camera.viewportHeight = c.viewportHeight;
            this.camera.direction.set(c.direction);
            this.camera.up.set(c.up);
            this.camera.position.set(c.position);
            this.camera.update();
        }

        region.scroll(delta * 0.0001f, 0f);
        rebuildMesh();
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        batch.begin(camera);
        batch.render(this);
        batch.end();
    }

    private void rebuildMesh() {
        dispose();

        float u1 = region.getU();
        float v1 = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[]{
            position.x, position.y, position.z, 0f, -1f, 0f, u1, v1,
            position.x, position.y, position.z + scale.z, 0f, -1f, 0f, u1, v2,
            position.x + scale.x, position.y, position.z + scale.z, 0f, -1f, 0f, u2, v2,
            position.x + scale.x, position.y, position.z, 0f, -1f, 0f, u2, v1
        });

        mesh.setIndices(new short[]{
            0, 1, 2,
            0, 2, 3
        });
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
    }
}
