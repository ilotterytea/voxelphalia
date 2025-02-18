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
import kz.ilotterytea.voxelphalia.recipes.RecipeData;
import kz.ilotterytea.voxelphalia.recipes.RecipeWorkbenchLevel;
import kz.ilotterytea.voxelphalia.ui.IconButton;

public class CraftingWindow extends Window {
    private final PlayerEntity playerEntity;
    private final Skin skin;

    private final Image productImage;
    private final Label productLabel, titleLabel;
    private final Table productIngredients;
    private final TextButton craftButton;

    private RecipeData selectedRecipe;

    public CraftingWindow(Skin skin, PlayerEntity playerEntity) {
        super("", skin);
        setModal(true);
        setMovable(true);
        pad(16f);

        this.playerEntity = playerEntity;
        this.skin = skin;
        setSize(600, 450);
        setVisible(false);

        // title
        Table title = new Table();
        title.align(Align.left);
        titleLabel = new Label("Crafting", skin);
        title.add(titleLabel);
        add(title).growX().padBottom(15f).row();

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

        for (RecipeData data : VoxelphaliaGame.getInstance().getRecipeRegistry().getEntries()) {
            String id = String.valueOf(data.resultId());

            TextureAtlas.AtlasRegion region = voxels.findRegion(id);
            if (region == null) {
                region = voxels.findRegion(String.valueOf(VoxelphaliaGame.getInstance().getVoxelRegistry().getEntryById((byte) 8).getId()));
            }

            IconButton btn = new IconButton(id,
                new Image(region),
                skin
            );

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
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
        craftButton = new TextButton("Craft", skin);
        craftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (craftButton.isDisabled()) return;

                Inventory inventory = playerEntity.getInventory();

                for (int i = 0; i < selectedRecipe.ingredients().length; i++) {
                    byte ingredientId = selectedRecipe.ingredients()[i][0];
                    byte ingredientAmount = selectedRecipe.ingredients()[i][1];

                    inventory.remove(ingredientId, ingredientAmount);
                }

                inventory.add(selectedRecipe.resultId(), selectedRecipe.resultAmount());
                showRecipe(selectedRecipe.resultId());
            }
        });
        product.add(craftButton).growX();

        showRecipe(VoxelphaliaGame.getInstance().getRecipeRegistry().getEntries().getFirst().resultId());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            setVisible(!isVisible(), RecipeWorkbenchLevel.HANDS);
        }
    }

    private void showRecipe(byte id) {
        RecipeData data = VoxelphaliaGame.getInstance().getRecipeRegistry().getEntryById(id);
        if (data == null) return;
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

        boolean craftable = true;

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
                craftable = false;
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

        craftButton.setDisabled(!craftable);
    }

    public void setVisible(boolean visible, RecipeWorkbenchLevel level) {
        super.setVisible(visible);
        playerEntity.setFocused(!isVisible());
        showRecipe(selectedRecipe.resultId());

        if (level == RecipeWorkbenchLevel.HANDS) {
            titleLabel.setText("Hand-made crafting");
        } else {
            titleLabel.setText("Basic crafting");
        }

        if (isVisible()) {
            setPosition(getStage().getWidth() / 2f - getWidth() / 2f, getStage().getHeight() / 2f - getHeight() / 2f);
        }
    }
}
