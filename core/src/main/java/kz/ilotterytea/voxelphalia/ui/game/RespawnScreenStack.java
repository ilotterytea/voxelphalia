package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

public class RespawnScreenStack extends Stack {
    private final PlayerEntity entity;
    private final Label timerTitle;

    public RespawnScreenStack(Skin skin, PlayerEntity entity) {
        super();
        setZIndex(90);
        setFillParent(true);

        this.entity = entity;

        // background
        Image backgroundImage = new Image(skin.getDrawable("respawn-background"));
        backgroundImage.setFillParent(true);
        add(backgroundImage);

        // table
        Table table = new Table();
        table.setFillParent(true);
        add(table);

        Label title = new Label("You died!", skin, "respawn-title");
        table.add(title).padBottom(30f).row();

        timerTitle = new Label("", skin);
        timerTitle.setAlignment(Align.center);
        table.add(timerTitle).row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setVisible(entity.isDead());

        if (entity.isDead()) {
            timerTitle.setText("Respawn in " + (int) (entity.getRespawnTime() * 10f) / 10f + "...");
        }
    }
}
