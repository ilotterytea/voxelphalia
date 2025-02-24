package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.l10n.LineId;
import kz.ilotterytea.voxelphalia.l10n.LocalizationManager;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.ui.IconButton;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.List;
import java.util.Map;

public class CraftingWindow extends Window {
    protected final PlayerEntity playerEntity;
    protected final Skin skin;
    protected final RecipeWorkbenchLevel level;

    protected final Image productImage;
    protected final Label productLabel, productDescription;
    protected final Table productIngredientBody;
    protected final TextButton craftButton;
    protected final IconButton[] recipeButtons;

    protected Recipe selectedRecipe;

    public CraftingWindow(Skin skin, PlayerEntity playerEntity, RecipeWorkbenchLevel level) {
        super("", skin);
        setModal(true);
        setMovable(true);
        pad(16f);

        this.playerEntity = playerEntity;
        this.skin = skin;
        this.level = level;
        VoxelphaliaGame game = VoxelphaliaGame.getInstance();

        setSize(800, 600);
        super.setVisible(false);

        LineId windowTitleId, craftButtonId;

        switch (level) {
            case WORKBENCH -> {
                windowTitleId = LineId.CRAFTING_BASIC_TITLE;
                craftButtonId = LineId.CRAFTING_CRAFT;
            }
            case FURNACE -> {
                windowTitleId = LineId.SMELTING_TITLE;
                craftButtonId = LineId.SMELTING_SMELT;
            }
            default -> {
                windowTitleId = LineId.CRAFTING_HANDMADE_TITLE;
                craftButtonId = LineId.CRAFTING_CRAFT;
            }
        }

        // --- WINDOW HEADER ---
        Table windowHeader = new Table();
        add(windowHeader).growX().padBottom(15f).row();

        LocalizationManager localizationManager = game.getLocalizationManager();

        // Window title
        Table title = new Table();
        title.align(Align.left);
        Label titleLabel = new Label(localizationManager.getLine(windowTitleId), skin);
        title.add(titleLabel);
        windowHeader.add(title).growX();

        // Close button
        ImageButton closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
            }
        });
        windowHeader.add(closeButton);

        // --- WINDOW BODY ---
        Table windowBody = new Table();
        windowBody.align(Align.top);
        add(windowBody).grow();

        // --- RECIPES BODY ---
        Table recipesBody = new Table();
        recipesBody.align(Align.top);
        ScrollPane recipesScrollpane = new ScrollPane(recipesBody, skin);
        recipesScrollpane.setScrollingDisabled(true, false);
        recipesScrollpane.setFadeScrollBars(false);
        recipesScrollpane.setScrollbarsVisible(true);
        windowBody.add(recipesScrollpane).padRight(16f).grow();

        // Loading recipes
        TextureAtlas voxels = game.getAssetManager().get("textures/gui/gui_voxels.atlas");
        List<Recipe> recipes = VoxelphaliaGame.getInstance()
            .getRecipeRegistry()
            .getEntries()
            .stream()
            .filter((x) -> x.level() == level)
            .toList();
        recipeButtons = new IconButton[recipes.size()];
        Texture items = game.getAssetManager().get("textures/items.png", Texture.class);


        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);

            TextureRegion region;

            if (game.getItemRegistry().containsEntry(recipe.resultId())) {
                region = game.getItemRegistry().getEntry(recipe.resultId())
                    .getMaterial()
                    .getTextureRegion(items);
            } else {
                region = voxels.findRegion(recipe.resultId().getName());
            }

            if (region == null) {
                region = voxels.findRegion(VoxelphaliaGame.getInstance().getIdentifierRegistry().getEntry("missing_voxel").getName());
            }

            LineId lineId;

            String name = recipe.resultId().getName().replace("_", "");

            if (game.getItemRegistry().containsEntry(recipe.resultId())) {
                lineId = LineId.parse("item." + name + ".name");
            } else {
                lineId = LineId.parse("voxel." + name + ".name");
            }

            String localizedLine = game
                .getLocalizationManager()
                .getLine(lineId);

            IconButton btn = new IconButton(
                localizedLine,
                new Image(region),
                skin
            );

            recipeButtons[i] = btn;

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (btn.isDisabled()) return;
                    showRecipe(recipe);
                }
            });
            recipesBody.add(btn).growX().padBottom(8f).row();
        }

        // --- PRODUCT BODY ---
        Table productBody = new Table();
        productBody.align(Align.top);
        windowBody.add(productBody).grow();

        // --- PRODUCT HEADER ---
        Table productHeader = new Table(skin);
        productHeader.setBackground("window-foreground");
        productHeader.align(Align.left);
        productHeader.pad(8f);
        productBody.add(productHeader).growX().padBottom(15f).row();

        // Product image
        productImage = new Image();
        productHeader.add(productImage).size(32f, 32f).top().padRight(16f);

        // -- PRODUCT INFORMATION BODY --
        Table productInformationBody = new Table();
        productHeader.add(productInformationBody).grow();

        // Product name
        productLabel = new Label("", skin);
        productLabel.setAlignment(Align.left);
        productInformationBody.add(productLabel).growX().row();

        // Product description
        productDescription = new Label("", skin, "tiny-default");
        productDescription.setAlignment(Align.left);
        productDescription.setWrap(true);
        productInformationBody.add(productDescription).growX().row();

        // -- PRODUCT INGREDIENT BODY --
        productIngredientBody = new Table(skin);
        productIngredientBody.setBackground("window-foreground");
        ScrollPane ingredientsScrollpane = new ScrollPane(productIngredientBody, skin);
        ingredientsScrollpane.setScrollingDisabled(true, false);
        ingredientsScrollpane.setFadeScrollBars(false);
        ingredientsScrollpane.setScrollbarsVisible(true);
        productBody.add(ingredientsScrollpane).grow().padBottom(15f).row();

        // --- CREATION BUTTON ---
        craftButton = new TextButton(localizationManager.getLine(craftButtonId), skin);
        craftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (craftButton.isDisabled()) return;

                Inventory inventory = playerEntity.getInventory();

                for (Map.Entry<Identifier, Byte> entry : selectedRecipe.ingredients().entrySet()) {
                    inventory.remove(entry.getKey(), entry.getValue());
                }

                inventory.add(selectedRecipe.resultId(), selectedRecipe.resultAmount());
                showRecipe(selectedRecipe);
            }
        });
        productBody.add(craftButton).growX();

        showRecipe(game.getRecipeRegistry()
            .getEntries()
            .stream()
            .filter((x) -> x.level() == level)
            .findFirst()
            .orElse(null)
        );
    }

    protected void showRecipe(Recipe recipe) {
        if (recipe == null || recipe.level() != level) return;
        this.selectedRecipe = recipe;

        VoxelphaliaGame game = VoxelphaliaGame.getInstance();

        TextureAtlas atlas = game.getAssetManager().get("textures/gui/gui_voxels.atlas");
        Texture items = game.getAssetManager().get("textures/items.png", Texture.class);

        // recipe name and icon
        TextureRegion region;

        if (game.getItemRegistry().containsEntry(recipe.resultId())) {
            region = game.getItemRegistry().getEntry(recipe.resultId())
                .getMaterial()
                .getTextureRegion(items);
        } else {
            region = atlas.findRegion(recipe.resultId().getName());
        }

        if (region == null) {
            region = atlas.findRegion(VoxelphaliaGame.getInstance().getIdentifierRegistry().getEntry("missing_voxel").getName());
        }

        productImage.setDrawable(new TextureRegionDrawable(region));

        String recipeName = recipe.resultId().getName().replace("_", "");
        LineId name, desc;

        try {
            name = LineId.parse("voxel." + recipeName + ".name");
            desc = LineId.parse("voxel." + recipeName + ".description");
        } catch (Exception e) {
            name = LineId.parse("item." + recipeName + ".name");
            desc = LineId.parse("item." + recipeName + ".description");
        }

        productLabel.setText(
            VoxelphaliaGame.getInstance()
                .getLocalizationManager()
                .getLine(name));
        productDescription.setText(
            VoxelphaliaGame.getInstance()
                .getLocalizationManager()
                .getLine(desc));

        productIngredientBody.clear();
        productIngredientBody.layout();

        boolean craftable = true;
        LocalizationManager localizationManager = VoxelphaliaGame.getInstance().getLocalizationManager();

        // ingredients
        int i = 0;
        for (Map.Entry<Identifier, Byte> entry : recipe.ingredients().entrySet()) {
            int totalAmount = playerEntity.getInventory().getTotalVoxelAmount(entry.getKey());

            Table ingredient = new Table();
            ingredient.align(Align.center);
            ingredient.pad(8f);

            if (game.getItemRegistry().containsEntry(entry.getKey())) {
                region = game.getItemRegistry().getEntry(entry.getKey())
                    .getMaterial()
                    .getTextureRegion(items);
            } else {
                region = atlas.findRegion(entry.getKey().getName());
            }

            if (region == null) {
                region = atlas.findRegion(VoxelphaliaGame.getInstance().getIdentifierRegistry().getEntry("missing_voxel").getName());
            }

            Image icon = new Image(region);
            ingredient.add(icon).size(32f, 32f).row();

            Label amount = new Label(localizationManager.getLine(LineId.CRAFTING_INGREDIENTS, totalAmount, entry.getValue()), skin);
            amount.setAlignment(Align.center);
            ingredient.add(amount).row();

            if (totalAmount < entry.getValue()) {
                amount.setColor(Color.SALMON);
                icon.setColor(Color.SALMON);
                craftable = false;
            } else {
                amount.setColor(Color.WHITE);
                icon.setColor(Color.WHITE);
            }

            String ingredientName = entry.getKey().getName().replace("_", "");
            LineId ingredientNameId, ingredientDescId;

            try {
                ingredientNameId = LineId.parse("voxel." + ingredientName + ".name");
                ingredientDescId = LineId.parse("voxel." + ingredientName + ".description");
            } catch (Exception e) {
                ingredientNameId = LineId.parse("item." + ingredientName + ".name");
                ingredientDescId = LineId.parse("item." + ingredientName + ".description");
            }

            ingredient.addListener(new TextTooltip(
                VoxelphaliaGame.getInstance()
                    .getLocalizationManager()
                    .getLine(ingredientNameId) + ".\n" +
                    VoxelphaliaGame.getInstance()
                        .getLocalizationManager()
                        .getLine(ingredientDescId)
                , skin));

            productIngredientBody.add(ingredient).grow();

            if (i % 3 == 2) {
                productIngredientBody.row();
            }

            i++;
        }

        craftButton.setDisabled(!craftable);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        playerEntity.setFocused(!isVisible());
        showRecipe(selectedRecipe);

        if (isVisible()) {
            setPosition(getStage().getWidth() / 2f - getWidth() / 2f, getStage().getHeight() / 2f - getHeight() / 2f);
        }
    }
}
