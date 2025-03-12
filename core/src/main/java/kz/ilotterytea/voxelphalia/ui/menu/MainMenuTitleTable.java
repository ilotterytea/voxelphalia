package kz.ilotterytea.voxelphalia.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;

public class MainMenuTitleTable extends Table {
    public MainMenuTitleTable() {
        super();
        align(Align.center);

        TextureAtlas atlas = VoxelphaliaGame.getInstance().getAssetManager().get("textures/gui/brandfont.atlas");

        String title = "voxelphalia";
        char[] chars = title.toCharArray();
        float x = 0;
        float pad = 5f;

        for (int i = 0; i < chars.length; i++) {
            TextureRegion region = atlas.findRegion(String.valueOf(chars[i]));
            float d = MathUtils.random(0.25f, 0.5f);

            Image image = new Image(region);

            float w = image.getWidth() * 5f;
            float h = image.getHeight() * 5f;
            float sw = Gdx.graphics.getWidth() + w;
            float sh = Gdx.graphics.getHeight() + h;

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
        }
    }
}
