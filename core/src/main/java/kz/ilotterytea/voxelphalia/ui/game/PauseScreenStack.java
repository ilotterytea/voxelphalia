package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

public class PauseScreenStack extends Stack {
    private final PlayerEntity playerEntity;

    public PauseScreenStack(Skin skin, PlayerEntity playerEntity) {
        super();
        setFillParent(true);

        this.playerEntity = playerEntity;

        // background
        Image backgroundImage = new Image(skin.getDrawable("pause-background"));
        backgroundImage.setFillParent(true);
        add(backgroundImage);

        Image gradientImage = new Image(skin.getDrawable("pause-gradient"));
        gradientImage.setFillParent(true);
        add(gradientImage);

        // actual pause screen
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        add(table);

        Window window = new Window("", skin);
        window.pad(16f);
        window.align(Align.top);
        table.add(window).size(350f, 400f);

        Label windowLabel = new Label("GAME MENU", skin, "pause-title");
        windowLabel.setAlignment(Align.center);
        window.add(windowLabel).growX().padBottom(60f).padTop(30f).row();

        TextButton continueButton = new TextButton("Return to Game", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
                playerEntity.setFocused(true);
            }
        });
        window.add(continueButton).growX().padBottom(30f).row();

        TextButton quitButton = new TextButton("Quit to Desktop", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });
        window.add(quitButton).growX().row();

        setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            boolean areWindowsInactive = true;

            for (Actor actor : getStage().getActors()) {
                if (actor instanceof Window) {
                    if (actor.isVisible()) {
                        actor.setVisible(false);
                        playerEntity.setFocused(true);
                        areWindowsInactive = false;
                    }
                }
            }

            if (areWindowsInactive) {
                setVisible(!isVisible());
                playerEntity.setFocused(!isVisible());
            }
        }
    }
}
