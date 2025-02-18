package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;
import kz.ilotterytea.voxelphalia.inventory.Inventory;
import kz.ilotterytea.voxelphalia.recipes.Recipe;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.ui.IconButton;
import kz.ilotterytea.voxelphalia.voxels.specialvoxels.FurnaceVoxel;

import java.util.List;

public class SmeltingWindow extends Window {
    private final PlayerEntity playerEntity;
    private final Skin skin;

    private final IconButton[] recipeButtons;
    private final Image productImage;
    private final Label productLabel, titleLabel;
    private final Table productIngredients;
    private final TextButton smeltButton;

    private Recipe selectedRecipe;
    private FurnaceVoxel furnace;

    public SmeltingWindow(Skin skin, PlayerEntity playerEntity) {
        super("", skin);
        setModal(true);
        setMovable(true);
        pad(16f);

        this.playerEntity = playerEntity;
        this.skin = skin;
        setSize(600, 450);
        setVisible(false);

        // HEADER
        Table header = new Table();
        add(header).growX().padBottom(15f).row();

        // title
        Table title = new Table();
        title.align(Align.left);
        titleLabel = new Label("Smelting", skin);
        title.add(titleLabel);
        header.add(title).growX();

        // close button
        ImageButton closeButton = new ImageButton(skin, "close");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false, null);
            }
        });
        header.add(closeButton);

        // body
        Table body = new Table();
        body.align(Align.top);
        add(body).grow();

        // recipes
        Table recipes = new Table();
        recipes.align(Align.top);
        ScrollPane recipesScrollpane = new ScrollPane(recipes, skin);
        recipesScrollpane.setScrollingDisabled(true, false);
        recipesScrollpane.setFadeScrollBars(false);
        recipesScrollpane.setScrollbarsVisible(true);
        body.add(recipesScrollpane).padRight(16f).grow();

        // loading recipes
        TextureAtlas voxels = VoxelphaliaGame.getInstance().getAssetManager().get("textures/gui/gui_voxels.atlas");
        List<Recipe> recipeDatas = VoxelphaliaGame.getInstance()
            .getRecipeRegistry()
            .getEntries()
            .stream().filter((x) -> x.level() == RecipeWorkbenchLevel.FURNACE)
            .toList();

        recipeButtons = new IconButton[recipeDatas.size()];

        for (int i = 0; i < recipeDatas.size(); i++) {
            Recipe data = recipeDatas.get(i);
            String id = String.valueOf(data.resultId());

            TextureAtlas.AtlasRegion region = voxels.findRegion(id);
            if (region == null) {
                region = voxels.findRegion(String.valueOf(VoxelphaliaGame.getInstance().getVoxelRegistry().getEntryById((byte) 8).getId()));
            }

            IconButton btn = new IconButton(id,
                new Image(region),
                skin
            );

            recipeButtons[i] = btn;

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (btn.isDisabled()) return;
                    showRecipe(data.resultId());
                }
            });
            recipes.add(btn).growX().padBottom(8f).row();
        }

        // product
        Table product = new Table();
        product.align(Align.top);
        body.add(product).grow();

        // product title
        Table productTitle = new Table(skin);
        productTitle.setBackground("window-foreground");
        productTitle.align(Align.left);
        productTitle.pad(8f);
        product.add(productTitle).growX().padBottom(15f).row();

        productImage = new Image();
        productTitle.add(productImage).size(32f, 32f).padRight(16f);

        productLabel = new Label("", skin);
        productTitle.add(productLabel).growX();

        // product ingredients
        productIngredients = new Table(skin);
        productIngredients.setBackground("window-foreground");
        ScrollPane ingredientsScrollpane = new ScrollPane(productIngredients, skin);
        ingredientsScrollpane.setScrollingDisabled(true, false);
        ingredientsScrollpane.setFadeScrollBars(false);
        ingredientsScrollpane.setScrollbarsVisible(true);
        product.add(ingredientsScrollpane).grow().padBottom(15f).row();

        // product creation
        smeltButton = new TextButton("Smelt", skin);
        smeltButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (smeltButton.isDisabled()) return;

                Inventory inventory = playerEntity.getInventory();

                for (int i = 0; i < selectedRecipe.ingredients().length; i++) {
                    byte ingredientId = selectedRecipe.ingredients()[i][0];
                    byte ingredientAmount = selectedRecipe.ingredients()[i][1];

                    inventory.remove(ingredientId, ingredientAmount);
                }

                furnace.setVoxelToSmelt(VoxelphaliaGame.getInstance().getVoxelRegistry().getEntryById(selectedRecipe.resultId()));
                showRecipe(selectedRecipe.resultId());
            }
        });
        product.add(smeltButton).growX();

        showRecipe(VoxelphaliaGame.getInstance().getRecipeRegistry().getEntries().stream().filter((x) -> x.level() == RecipeWorkbenchLevel.FURNACE).findFirst().get().resultId());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            setVisible(!isVisible(), null);
        }

        if (isVisible()) {
            if (furnace.getVoxel() != null && selectedRecipe.resultId() == furnace.getVoxel().getId() && furnace.isVoxelSmelting()) {
                smeltButton.setText("Remaining " + (int) ((furnace.getMaxSmeltTime() - furnace.getSmeltTime()) * 10f) / 10f + "s...");
            } else {
                smeltButton.setText("Smelt");
            }

            if (furnace.isFinished()) {
                Inventory inventory = playerEntity.getInventory();

                Recipe recipe = VoxelphaliaGame.getInstance()
                    .getRecipeRegistry()
                    .getEntries()
                    .stream()
                    .filter((x) -> x.resultId() == furnace.getVoxel().getId())
                    .findFirst().get();

                inventory.add(recipe.resultId(), recipe.resultAmount());
                furnace.setVoxelToSmelt(null);
                showRecipe(recipe.resultId());
            }
        }
    }

    private void showRecipe(byte id) {
        Recipe data = VoxelphaliaGame.getInstance().getRecipeRegistry().getEntryById(id);
        if (data == null || data.level() != RecipeWorkbenchLevel.FURNACE) return;
        this.selectedRecipe = data;

        TextureAtlas atlas = VoxelphaliaGame.getInstance().getAssetManager().get("textures/gui/gui_voxels.atlas");

        // recipe name and icon
        TextureAtlas.AtlasRegion region = atlas.findRegion(String.valueOf(id));
        if (region == null) {
            region = atlas.findRegion(String.valueOf(VoxelphaliaGame.getInstance().getVoxelRegistry().getEntryById((byte) 8).getId()));
        }
        productImage.setDrawable(new TextureRegionDrawable(region));
        productLabel.setText(String.valueOf(id));

        productIngredients.clear();
        productIngredients.layout();

        boolean smeltable = true;

        // ingredients
        for (int i = 0; i < data.ingredients().length; i++) {
            byte ingredientId = data.ingredients()[i][0];
            byte ingredientAmount = data.ingredients()[i][1];

            int totalAmount = playerEntity.getInventory().getTotalVoxelAmount(ingredientId);

            Table ingredient = new Table();
            ingredient.align(Align.center);
            ingredient.pad(8f);

            Image icon = new Image(atlas.findRegion(String.valueOf(ingredientId)));
            ingredient.add(icon).size(32f, 32f).row();

            Label amount = new Label(String.format("%s/%s", totalAmount, ingredientAmount), skin);
            amount.setAlignment(Align.center);
            ingredient.add(amount).row();

            if (totalAmount < ingredientAmount) {
                amount.setColor(Color.SALMON);
                icon.setColor(Color.SALMON);
                smeltable = false;
            } else {
                amount.setColor(Color.WHITE);
                icon.setColor(Color.WHITE);
            }

            ingredient.addListener(new TextTooltip(String.valueOf(ingredientId), skin));

            productIngredients.add(ingredient).grow();

            if (i % 3 == 2) {
                productIngredients.row();
            }
        }

        if (furnace != null && furnace.isVoxelSmelting()) {
            smeltable = false;
        }

        for (IconButton recipeButton : recipeButtons) {
            recipeButton.setDisabled(!smeltable);
        }

        smeltButton.setDisabled(!smeltable);
    }

    public void setVisible(boolean visible, FurnaceVoxel voxel) {
        super.setVisible(visible);
        playerEntity.setFocused(!isVisible());
        showRecipe(selectedRecipe.resultId());

        if (voxel != null) {
            if (voxel.isVoxelSmelting()) {
                showRecipe(voxel.getId());
            } else {
                showRecipe(VoxelphaliaGame.getInstance().getRecipeRegistry().getEntries().stream().filter((x) -> x.level() == RecipeWorkbenchLevel.FURNACE).findFirst().get().resultId());
            }
        } else {
            showRecipe(VoxelphaliaGame.getInstance().getRecipeRegistry().getEntries().stream().filter((x) -> x.level() == RecipeWorkbenchLevel.FURNACE).findFirst().get().resultId());
        }

        if (isVisible()) {
            setPosition(getStage().getWidth() / 2f - getWidth() / 2f, getStage().getHeight() / 2f - getHeight() / 2f);
            furnace = voxel;
        } else {
            furnace = null;
        }
    }
}
