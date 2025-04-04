package kz.ilotterytea.voxelphalia.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.audio.IdentifiedSound;

public class MainMenuTitleTable extends Table {
    private final Image[] letters;

    public MainMenuTitleTable() {
        super();
        align(Align.center);

        TextureAtlas atlas = VoxelphaliaGame.getInstance().getAssetManager().get("textures/gui/brandfont.atlas");

        String title = "voxelphalia";
        char[] chars = title.toCharArray();

        this.letters = new Image[chars.length];

        float x = 0;
        float pad = 5f;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            TextureRegion region = atlas.findRegion(String.valueOf(c));
            float d = MathUtils.random(0.25f, 0.5f);

            Image image = new Image(region);

            float w = image.getWidth() * 5f;
            float h = image.getHeight() * 5f;
            float sw = Gdx.graphics.getWidth() + w;
            float sh = Gdx.graphics.getHeight() + h;

            IdentifiedSound sound;

            if (c == 'x' || c == 'i' || c == 'p') {
                sound = VoxelphaliaGame.getInstance().getSoundRegistry()
                    .getEntry("voxelphalia:sfx.footsteps.stone");
            } else {
                sound = VoxelphaliaGame.getInstance().getSoundRegistry()
                    .getEntry("voxelphalia:sfx.footsteps.dirt");
            }

            image.addAction(Actions.sequence(
                Actions.moveTo(
                    (i % 2 == 0 ? 1 : -1) * sw,
                    (i % 2 == 1 ? 1 : -1) * sh
                ),
                Actions.scaleBy(10f, 10f),
                Actions.parallel(
                    Actions.moveTo(x, 0, d, Interpolation.smooth),
                    Actions.scaleTo(1, 1, d, Interpolation.smooth)
                )
            ));

            x += w + pad;

            Cell<Image> cell = add(image).size(w, h);
            if (i + 1 != chars.length) {
                cell.padRight(pad);
            }

            image.setOrigin(w / 2f, h / 2f);

            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    image.addAction(Actions.sequence(
                        Actions.rotateTo(5f, 0.25f, Interpolation.smooth),
                        Actions.rotateTo(0f, 0.25f, Interpolation.smooth)
                    ));
                    sound.getSound().play(VoxelphaliaGame.getInstance().getPreferences()
                        .getFloat("sfx", 1f));
                }
            });

            this.letters[i] = image;
        }
    }

    @Override
    public boolean addListener(EventListener listener) {
        for (Image letter : letters) {
            letter.addListener(listener);
        }
        return super.addListener(listener);
    }
}
