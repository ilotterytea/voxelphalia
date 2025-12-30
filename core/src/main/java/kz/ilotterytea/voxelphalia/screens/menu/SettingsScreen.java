package kz.ilotterytea.voxelphalia.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.ui.menu.TiledVoxelTexture;
import kz.ilotterytea.voxelphalia.ui.sound.*;

import java.util.HashMap;

public class SettingsScreen implements Screen {
    private Stage stage;

    private Preferences preferences;
    private LocalizationManager localizationManager;

    private TiledVoxelTexture backgroundTexture;

    private Skin skin;

    private final float tablePad = 8f, sectionPad = 16f, rowPad = 16f;

    @Override
    public void show() {
        VoxelphaliaGame game = VoxelphaliaGame.getInstance();
        preferences = game.getPreferences();
        stage = new Stage(new ScreenViewport());
        skin = game.getAssetManager().get("textures/gui/gui.skin");
        localizationManager = game.getLocalizationManager();

        TextureAtlas iconAtlas = game.getAssetManager().get("textures/gui/settings.atlas");

        // background
        backgroundTexture = new TiledVoxelTexture(game.getVoxelRegistry().getEntry("dirt"));
        Image bgTileImage = new Image(backgroundTexture.getRegion());
        bgTileImage.setFillParent(true);
        stage.addActor(bgTileImage);

        // tinting the background
        Image tintImage = new Image(skin.getDrawable("black-transparent"));
        tintImage.setFillParent(true);
        stage.addActor(tintImage);

        // --- MAIN TABLE ---
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        stage.addActor(table);

        // -- TOP --
        Table topTable = new Table(skin);
        topTable.setBackground("window-background");
        topTable.pad(tablePad);
        Label titleLabel = new Label("", skin);
        titleLabel.setAlignment(Align.left);
        topTable.add(titleLabel).grow();

        // Close button
        SoundingTextButton closeButton = new SoundingTextButton(localizationManager.getLine(LineId.MENU_BACK), skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new MenuScreen());
            }
        });
        topTable.add(closeButton);

        table.add(topTable).fillX().row();

        // -- CENTER --
        Table centerTable = new Table();
        centerTable.align(Align.top);
        table.add(centerTable).grow().row();

        // - SIDEBAR -
        Table sidebarTable = new Table(skin);
        sidebarTable.setBackground("window-background");
        sidebarTable.pad(tablePad);
        sidebarTable.align(Align.topLeft);
        ScrollPane sidebarPane = new ScrollPane(sidebarTable, skin);
        sidebarPane.setFadeScrollBars(true);
        sidebarPane.setScrollbarsVisible(true);
        sidebarPane.setScrollingDisabled(true, false);
        centerTable.add(sidebarPane).growY();

        // - CONTENT -
        Table contentTable = new Table(skin);
        contentTable.pad(tablePad);
        contentTable.align(Align.topLeft);
        ScrollPane contentPane = new ScrollPane(contentTable, skin);
        contentPane.setFadeScrollBars(true);
        contentPane.setScrollbarsVisible(true);
        contentPane.setScrollingDisabled(true, false);
        centerTable.add(contentPane).grow();

        // filling sidebar and content
        HashMap<String, Table> categories = new HashMap<>();
        categories.put("general", generalTable());
        categories.put("sound", soundTable());
        categories.put("graphics", graphicsTable());

        categories.forEach((id, content) -> {
            LineId lineId = LineId.parse("settings.category." + id);
            String text = localizationManager.getLine(lineId);
            SoundingIconButton sidebarButton = new SoundingIconButton(text, new Image(iconAtlas.findRegion(id)), skin);
            sidebarButton.addCaptureListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    contentTable.clear();
                    contentTable.reset();
                    contentTable.add(content).grow();
                    titleLabel.setText(String.format("%s - %s", localizationManager.getLine(LineId.MENU_SETTINGS), text));
                }
            });
            sidebarTable.add(sidebarButton).growX().padBottom(tablePad).row();
        });

        titleLabel.setText(String.format("%s - %s", localizationManager.getLine(LineId.MENU_SETTINGS), localizationManager.getLine(LineId.SETTINGS_CATEGORY_GENERAL)));
        contentTable.add(categories.get("general")).grow();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundTexture.getRegion().setRegionWidth(width / 6);
        backgroundTexture.getRegion().setRegionHeight(height / 6);
    }

    @Override
    public void pause() {
        hide();
    }

    @Override
    public void resume() {
        show();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        backgroundTexture.dispose();
        stage.dispose();
    }

    private Table generalTable() {
        Table table = new Table();
        table.align(Align.topLeft);
        table.pad(tablePad);

        // --- GAMEPLAY ---
        Label gameplayTitle = new Label(localizationManager.getLine(LineId.SETTINGS_GAMEPLAY), skin);
        table.add(gameplayTitle).growX().padBottom(sectionPad).row();

        // -- LOCALIZATION --
        Table localizationTable = new Table();
        localizationTable.align(Align.left);
        table.add(localizationTable).growX().padLeft(rowPad).padBottom(sectionPad).row();

        Label localizationLabel = new Label(localizationManager.getLine(LineId.SETTINGS_LOCALIZATION), skin);
        localizationLabel.setAlignment(Align.left);
        localizationTable.add(localizationLabel).growX().padRight(rowPad);

        SoundingSelectBox<String> localizationSelectBox = new SoundingSelectBox<>(skin);
        Array<String> names = localizationManager.getNames();
        localizationSelectBox.setItems(names);
        localizationSelectBox.setSelected(names.get(localizationManager.getLocaleIndex()));
        localizationSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedString = localizationSelectBox.getSelected();
                int index = names.indexOf(selectedString, false);
                preferences.putInteger("localization-id", index);
                preferences.flush();
                localizationManager.setLocaleIndex(index);
                VoxelphaliaGame.getInstance().setScreen(new SettingsScreen());
            }
        });
        localizationTable.add(localizationSelectBox).width(256f);

        // -- MATURE CONTENT --
        Table matureContentTable = new Table();
        matureContentTable.align(Align.left);
        table.add(matureContentTable).growX().padLeft(rowPad).padBottom(sectionPad).row();

        Label violentContentLabel = new Label(localizationManager.getLine(LineId.SETTINGS_VIOLENTCONTENT), skin);
        violentContentLabel.setAlignment(Align.left);
        matureContentTable.add(violentContentLabel).growX().padRight(rowPad);

        SoundingCheckbox matureContentBox = new SoundingCheckbox("", skin);
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
        matureContentTable.add(matureContentBox);

        // --- PLAYER ---
        Label playerTitle = new Label(localizationManager.getLine(LineId.SETTINGS_PLAYER), skin);
        table.add(playerTitle).growX().padBottom(sectionPad).row();

        // -- BOBBING --
        Table bobbingTable = new Table();
        bobbingTable.align(Align.left);
        table.add(bobbingTable).growX().padLeft(rowPad).padBottom(sectionPad).row();

        Label bobbingLabel = new Label(localizationManager.getLine(LineId.SETTINGS_PLAYER_BOBBING), skin);
        bobbingLabel.setAlignment(Align.left);
        bobbingTable.add(bobbingLabel).growX().padRight(rowPad);

        SoundingCheckbox bobbingBox = new SoundingCheckbox("", skin);
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
        bobbingTable.add(bobbingBox);

        // -- AUTO-JUMP --
        Table autoJumpTable = new Table();
        autoJumpTable.align(Align.left);
        table.add(autoJumpTable).growX().padLeft(rowPad).padBottom(sectionPad).row();

        Label autoJumpLabel = new Label(localizationManager.getLine(LineId.SETTINGS_PLAYER_AUTOJUMP), skin);
        autoJumpLabel.setAlignment(Align.left);
        autoJumpTable.add(autoJumpLabel).growX().padRight(rowPad);

        SoundingCheckbox autoJumpBox = new SoundingCheckbox("", skin);
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
        autoJumpTable.add(autoJumpBox);

        return table;
    }

    private Table soundTable() {
        Table table = new Table();
        table.align(Align.topLeft);
        table.pad(tablePad);

        // -- SLIDERS --
        String[] sliderNames = {"sfx", "music"};

        for (String sliderName : sliderNames) {
            Table sliderTable = new Table();
            table.add(sliderTable).growX().padBottom(sectionPad).row();

            Label sliderLabel = new Label(localizationManager.getLine(LineId.parse("settings." + sliderName)), skin);
            sliderLabel.setAlignment(Align.left);
            sliderTable.add(sliderLabel).growX().padRight(rowPad);

            Label sliderValue = new Label("", skin);

            SoundingSlider slider = new SoundingSlider(0f, 1f, 0.1f, false, skin);
            slider.setValue(preferences.getFloat(sliderName, 1f));
            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    sliderValue.setText((int) (slider.getValue() * 100));
                    preferences.putFloat(sliderName, slider.getValue());
                    preferences.flush();
                }
            });

            sliderValue.setText((int) (slider.getValue() * 100));

            sliderTable.add(slider).width(192f).padRight(rowPad);
            sliderTable.add(sliderValue).width(64f);
        }

        return table;
    }

    private Table graphicsTable() {
        Table table = new Table();
        table.align(Align.topLeft);
        table.pad(tablePad);

        // -- RENDER DISTANCE --
        Table renderTable = new Table();
        table.add(renderTable).growX().padBottom(sectionPad).row();

        Label renderLabel = new Label(localizationManager.getLine(LineId.SETTINGS_GRAPHICS_RENDER), skin);
        renderLabel.setAlignment(Align.left);
        renderTable.add(renderLabel).growX().padRight(rowPad);

        SoundingSelectBox<String> renderSelectBox = new SoundingSelectBox<>(skin);
        Array<String> renderArray = new Array<>(
            new String[]{
                localizationManager.getLine(LineId.SETTINGS_GRAPHICS_RENDER_TINY),
                localizationManager.getLine(LineId.SETTINGS_GRAPHICS_RENDER_SMALL),
                localizationManager.getLine(LineId.SETTINGS_GRAPHICS_RENDER_MEDIUM),
                localizationManager.getLine(LineId.SETTINGS_GRAPHICS_RENDER_FAR)
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
        renderTable.add(renderSelectBox).width(256f);

        // -- FULLSCREEN --
        Table fullscreenTable = new Table();
        table.add(fullscreenTable).growX().padBottom(sectionPad).row();

        Label fullscreenLabel = new Label(localizationManager.getLine(LineId.SETTINGS_GRAPHICS_FULLSCREEN), skin);
        fullscreenLabel.setAlignment(Align.left);
        fullscreenTable.add(fullscreenLabel).growX().padRight(rowPad);

        SoundingCheckbox fullscreenCheckbox = new SoundingCheckbox("", skin);
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
        fullscreenTable.add(fullscreenCheckbox);

        return table;
    }
}
