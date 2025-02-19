package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.l10n.LineId;

public class RespawnScreenStack extends Stack {
    private final PlayerEntity entity;
    private final Label timerTitle;
    private final Image gradientImage;

    public RespawnScreenStack(Skin skin, PlayerEntity entity) {
        super();
        setZIndex(90);
        setFillParent(true);

        this.entity = entity;

        // background
        Image backgroundImage = new Image(skin.getDrawable("respawn-background"));
        backgroundImage.setFillParent(true);
        add(backgroundImage);

        gradientImage = new Image(skin.getDrawable("respawn-gradient"));
        gradientImage.setFillParent(true);
        add(gradientImage);

        // table
        Table table = new Table();
        table.setFillParent(true);
        add(table);

        Label title = new Label(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.DEATH_TITLE), skin, "respawn-title");
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
            timerTitle.setText(VoxelphaliaGame.getInstance().getLocalizationManager().getLine(LineId.DEATH_RESPAWN, (int) (entity.getRespawnTime() * 10f) / 10f));
            if (!gradientImage.hasActions()) {
                gradientImage.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.alpha(1f, 10f)
                ));
            }
        } else {
            gradientImage.clearActions();
        }
    }
}
