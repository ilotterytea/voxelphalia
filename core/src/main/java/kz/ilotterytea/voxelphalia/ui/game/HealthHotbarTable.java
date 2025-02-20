package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.LivingEntity;
import kz.ilotterytea.voxelphalia.l10n.LineId;

public class HealthHotbarTable extends Table {
    private final LivingEntity entity;
    private float previousHealth;
    private final Skin skin;
    private final Image[] heartIcons;
    private final Label title;

    public HealthHotbarTable(Skin skin, LivingEntity entity) {
        super(skin);
        setBackground("healthbar-background");
        align(Align.left);
        pad(12f);

        this.entity = entity;
        this.skin = skin;
        this.previousHealth = entity.getHealth();

        this.heartIcons = new Image[entity.getMaxHealth() / 20];

        title = new Label(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.HOTBAR_HEALTH_TITLE),
            skin,
            "healthbar-title"
        );
        title.setAlignment(Align.left);
        add(title).grow().padBottom(4f).row();

        Table heartTable = new Table();
        add(heartTable).grow();

        for (int i = 0; i < heartIcons.length; i++) {
            heartIcons[i] = new Image(skin.getDrawable("heart-full"));
            Cell<Image> image = heartTable.add(heartIcons[i]).size(64f, 64f);

            if (i < heartIcons.length - 1) {
                image.padRight(8f);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float d = Math.abs(entity.getHealth() - previousHealth);
        previousHealth = entity.getHealth();

        for (int i = 0; i < heartIcons.length; i++) {
            Image heart = heartIcons[i];

            float segment = entity.getHealth() / 20f;

            if (segment > i + 0.5f) {
                heart.setDrawable(skin.getDrawable("heart-full"));
            } else if (segment > i && segment <= i + 0.5f) {
                heart.setDrawable(skin.getDrawable("heart-half"));
            } else {
                heart.setDrawable(skin.getDrawable("heart-empty"));
            }

            if ((segment <= 0.5f && !heart.hasActions()) || d > 0) {
                float y = MathUtils.random(-20f, 20f) * (d / 10);
                float x = MathUtils.random(-20f, 20f) * (d / 10);
                heart.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(x, y, 0.05f),
                        Actions.color(Color.LIGHT_GRAY)
                    ),
                    Actions.parallel(
                        Actions.moveBy(-x, -y, 0.05f),
                        Actions.color(Color.WHITE)
                    )
                ));
                title.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(x, y, 0.05f),
                        Actions.color(Color.LIGHT_GRAY)
                    ),
                    Actions.parallel(
                        Actions.moveBy(-x, -y, 0.05f),
                        Actions.color(Color.WHITE)
                    )
                ));
            }
        }

        previousHealth = entity.getHealth();
    }
}
