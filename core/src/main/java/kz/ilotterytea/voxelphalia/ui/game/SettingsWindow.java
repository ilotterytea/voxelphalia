package kz.ilotterytea.voxelphalia.ui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;

public class SettingsWindow extends Window {
    private final Skin skin;
    private final VoxelphaliaGame game;
    private final Preferences preferences;

    public SettingsWindow(Skin skin) {
        super("", skin);
        setMovable(true);
        setModal(true);

        this.skin = skin;
        this.game = VoxelphaliaGame.getInstance();
        this.preferences = game.getPreferences();

        pad(16f);
        align(Align.top);

        showMainWindow();
    }

    private void showMainWindow() {
        clear();
        layout();

        LocalizationManager localizationManager = game.getLocalizationManager();

        // -- TITLE --
        Table headerTable = new Table();
        add(headerTable).growX().padBottom(60f).padTop(30f).row();

        // Window label
        Label windowLabel = new Label(localizationManager.getLine(LineId.PAUSE_SETTINGS), skin, "pause-title");
        windowLabel.setAlignment(Align.left);
        headerTable.add(windowLabel).growX();

        // Close button
        ImageButton closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
            }
        });
        headerTable.add(closeButton);

        // -- SLIDERS --
        String[] sliderNames = {"sfx"};

        for (String sliderName : sliderNames) {
            Table sliderTable = new Table();
            add(sliderTable).growX().padBottom(30f).row();

            Label sliderLabel = new Label(localizationManager.getLine(LineId.parse("settings." + sliderName)), skin);
            sliderLabel.setAlignment(Align.center);
            sliderTable.add(sliderLabel).padRight(8f);

            Label sliderValue = new Label("", skin);

            Slider slider = new Slider(0f, 1f, 0.1f, false, skin);
            slider.setValue(preferences.getFloat("sfx", 1f));
            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    sliderValue.setText((int) (slider.getValue() * 100));
                    preferences.putFloat("sfx", slider.getValue());
                    game.getPreferences().flush();
                }
            });

            sliderValue.setText((int) (slider.getValue() * 100));

            sliderTable.add(slider).growX();
            sliderTable.add(sliderValue);
        }

        // -- CATEGORIES --
        Table categoriesTable = new Table();
        add(categoriesTable).growX().padBottom(30f).row();

        // Graphics
        TextButton graphicsButton = new TextButton(localizationManager.getLine(LineId.SETTINGS_GRAPHICS), skin);
        graphicsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showGraphicsWindow();
            }
        });
        categoriesTable.add(graphicsButton).growX();

        // -- PLAYER SETTINGS --
        Table playerSettingsTable = new Table();
        add(playerSettingsTable).growX().padBottom(30f).row();

        CheckBox bobbingBox = new CheckBox(localizationManager.getLine(LineId.SETTINGS_PLAYER_BOBBING), skin);
        bobbingBox.setChecked(preferences.getBoolean("view-bobbing", true));
        bobbingBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                preferences.putBoolean("view-bobbing", !preferences.getBoolean("view-bobbing", true));
                preferences.flush();
                bobbingBox.setChecked(preferences.getBoolean("view-bobbing", true));
            }
        });
        playerSettingsTable.add(bobbingBox).expandX().padBottom(16f).row();

        CheckBox autoJumpBox = new CheckBox(localizationManager.getLine(LineId.SETTINGS_PLAYER_AUTOJUMP), skin);
        autoJumpBox.setChecked(preferences.getBoolean("auto-jump", true));
        autoJumpBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                preferences.putBoolean("auto-jump", !preferences.getBoolean("auto-jump", true));
                preferences.flush();
                autoJumpBox.setChecked(preferences.getBoolean("auto-jump", true));
            }
        });
        playerSettingsTable.add(autoJumpBox).expandX().row();

        CheckBox matureContentBox = new CheckBox(localizationManager.getLine(LineId.SETTINGS_VIOLENTCONTENT), skin);
        matureContentBox.setChecked(preferences.getBoolean("violent-content", true));
        matureContentBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                preferences.putBoolean("violent-content", !preferences.getBoolean("violent-content", true));
                preferences.flush();
                matureContentBox.setChecked(preferences.getBoolean("violent-content", true));
            }
        });
        playerSettingsTable.add(matureContentBox).expandX().row();
    }

    private void showGraphicsWindow() {
        clear();
        layout();

        // -- TITLE --
        Table headerTable = new Table();
        add(headerTable).growX().padBottom(60f).padTop(30f).row();

        // Window label
        Label windowLabel = new Label(game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS), skin, "pause-title");
        windowLabel.setAlignment(Align.left);
        headerTable.add(windowLabel).growX();

        // Close button
        ImageButton closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showMainWindow();
            }
        });
        headerTable.add(closeButton);

        // -- RENDER DISTANCE --
        Table renderTable = new Table();
        add(renderTable).growX().padBottom(30f).row();

        Label renderLabel = new Label(game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS_RENDER), skin);
        renderTable.add(renderLabel);

        SelectBox<String> renderSelectBox = new SelectBox<>(skin);
        Array<String> renderArray = new Array<>(
            new String[]{
                game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS_RENDER_TINY),
                game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS_RENDER_SMALL),
                game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS_RENDER_MEDIUM),
                game.getLocalizationManager().getLine(LineId.SETTINGS_GRAPHICS_RENDER_FAR)
            }
        );
        renderSelectBox.setItems(renderArray);
        renderSelectBox.setSelected(renderArray.get(
            preferences.getInteger("render-distance", 1) - 1
        ));

        renderSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedString = renderSelectBox.getSelected();
                int index = renderArray.indexOf(selectedString, false);

                preferences.putInteger("render-distance", index + 1);
                preferences.flush();
            }
        });
        renderTable.add(renderSelectBox).growX();

        // -- FULLSCREEN --
        CheckBox fullscreenCheckbox = new CheckBox("fullscreen", skin);
        fullscreenCheckbox.setChecked(preferences.getBoolean("fullscreen", false));
        fullscreenCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                boolean fullscreen = !preferences.getBoolean("fullscreen", false);

                preferences.putBoolean("fullscreen", fullscreen);
                preferences.flush();
                fullscreenCheckbox.setChecked(fullscreen);

                if (fullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(800, 600);
                }
            }
        });
        add(fullscreenCheckbox).expandX().padBottom(30f).row();
    }
}
