package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;

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

        LocalizationManager localizationManager = VoxelphaliaGame.getInstance().getLocalizationManager();

        Label windowLabel = new Label(localizationManager.getLine(LineId.PAUSE_TITLE), skin, "pause-title");
        windowLabel.setAlignment(Align.center);
        window.add(windowLabel).growX().padBottom(60f).padTop(30f).row();

        // -- CONTINUE BUTTON --
        TextButton continueButton = new TextButton(localizationManager.getLine(LineId.PAUSE_RETURN), skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
                playerEntity.setFocused(true);
            }
        });
        window.add(continueButton).growX().padBottom(30f).row();

        // -- SETTINGS BUTTON --
        Table settingsTable = new Table();
        settingsTable.setFillParent(true);
        add(settingsTable);

        SettingsWindow settingsWindow = new SettingsWindow(skin);
        settingsWindow.setVisible(false);
        settingsTable.add(settingsWindow).size(350f, 400f);

        TextButton settingsButton = new TextButton(localizationManager.getLine(LineId.PAUSE_SETTINGS), skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                settingsWindow.setVisible(!settingsWindow.isVisible());
            }
        });
        window.add(settingsButton).growX().padBottom(30f).row();

        // -- QUIT BUTTON --
        TextButton quitButton = new TextButton(localizationManager.getLine(LineId.PAUSE_QUIT), skin);
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
