package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.l10n.LineId;

public class EnergyHotbarTable extends Table {
    private final PlayerEntity entity;
    private float previousEnergy;
    private final Skin skin;
    private final Image[] energyIcons;

    public EnergyHotbarTable(Skin skin, PlayerEntity entity) {
        super(skin);
        setBackground("healthbar-background");
        align(Align.left);
        pad(12f);

        this.entity = entity;
        this.skin = skin;
        this.previousEnergy = entity.getHealth();

        this.energyIcons = new Image[(int) (entity.getMaxEnergy() / 20)];

        Label title = new Label(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.HOTBAR_ENERGY_TITLE),
            skin,
            "energybar-title"
        );
        title.setAlignment(Align.left);
        add(title).grow().padBottom(4f).padRight(16f);

        Table heartTable = new Table();
        add(heartTable);

        for (int i = 0; i < energyIcons.length; i++) {
            energyIcons[i] = new Image(skin.getDrawable("energy-full"));
            Cell<Image> image = heartTable.add(energyIcons[i]).size(32f, 32f);

            if (i < energyIcons.length - 1) {
                image.padRight(8f);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float d = Math.abs(entity.getEnergy() - previousEnergy);

        for (int i = 0; i < energyIcons.length; i++) {
            Image energy = energyIcons[i];

            float segment = entity.getEnergy() / 20f;

            if (segment > i + 0.5f) {
                energy.setDrawable(skin.getDrawable("energy-full"));
            } else if (segment > i && segment <= i + 0.5f) {
                energy.setDrawable(skin.getDrawable("energy-half"));
            } else {
                energy.setDrawable(skin.getDrawable("energy-empty"));
            }

            if (previousEnergy != entity.getEnergy()) {
                float y = MathUtils.random(-20f, 20f) * (d / 10);
                float x = MathUtils.random(-20f, 20f) * (d / 10);
                energy.addAction(Actions.sequence(
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

        previousEnergy = entity.getEnergy();
    }
}
