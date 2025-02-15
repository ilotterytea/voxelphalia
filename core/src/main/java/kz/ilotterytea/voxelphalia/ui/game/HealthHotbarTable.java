package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.entities.LivingEntity;

public class HealthHotbarTable extends Table {
    private final LivingEntity entity;
    private final Skin skin;
    private final Image[] heartIcons;

    public HealthHotbarTable(Skin skin, LivingEntity entity) {
        super();
        setFillParent(true);
        align(Align.bottomLeft);

        this.entity = entity;
        this.skin = skin;

        this.heartIcons = new Image[entity.getMaxHealth() / 2];

        for (int i = 0; i < heartIcons.length; i++) {
            heartIcons[i] = new Image(skin.getDrawable("heart-full"));
            add(heartIcons[i]).size(32f, 32f).padRight(4f);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float health = entity.getHealth() / 2f;

        for (int i = 0; i < heartIcons.length; i++) {
            Image heart = heartIcons[i];
            float d = health - i;

            if (1f <= d) {
                heart.setDrawable(skin.getDrawable("heart-full"));
            } else if (0f >= d) {
                heart.setDrawable(skin.getDrawable("heart-empty"));
            } else {
                heart.setDrawable(skin.getDrawable("heart-half"));
            }

            if (entity.getHealth() <= 5f && !heart.hasActions()) {
                float y = MathUtils.random(2f, 4f);
                d = 5f - entity.getHealth();
                heart.addAction(Actions.sequence(
                    Actions.moveBy(0f, y + d, 0.05f),
                    Actions.moveBy(0f, -y - d, 0.05f)
                ));
            }
        }
    }
}
