package kz.ilotterytea.voxelphalia.ui.game.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kz.ilotterytea.voxelphalia.VoxelphaliaGame;
import kz.ilotterytea.voxelphalia.entities.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class CraftingWindow extends Window {
    private final PlayerEntity playerEntity;

    public CraftingWindow(Skin skin, PlayerEntity playerEntity) {
        super("", skin);
        setModal(true);
        setMovable(true);
        pad(16f);

        this.playerEntity = playerEntity;
        setSize(600, 450);
        setVisible(false);

        // title
        Table title = new Table();
        title.align(Align.left);
        title.add(new Label("Crafting", skin));
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

        for (int i = 0; i < 10; i++) {
            TextButton btn = new TextButton("recipe " + i, skin);
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

        Image productImage = new Image(skin.getDrawable("heart-full"));
        productTitle.add(productImage).padRight(16f);

        Label productLabel = new Label("recipe name", skin);
        productTitle.add(productLabel).growX();

        // product ingredients
        Table productIngredients = new Table(skin);
        productIngredients.setBackground("window-foreground");
        ScrollPane ingredientsScrollpane = new ScrollPane(productIngredients, skin);
        ingredientsScrollpane.setScrollingDisabled(true, false);
        ingredientsScrollpane.setFadeScrollBars(false);
        ingredientsScrollpane.setScrollbarsVisible(true);
        product.add(ingredientsScrollpane).grow().padBottom(15f).row();

        // FIXME: TEST
        HashMap<Byte, Byte> ingredients = new HashMap<>();
        ingredients.put((byte) 1, (byte) 2);
        ingredients.put((byte) 2, (byte) 6);
        ingredients.put((byte) 3, (byte) 4);
        ingredients.put((byte) 4, (byte) 5);

        TextureAtlas ingredientAtlas = VoxelphaliaGame.getInstance().getAssetManager().get("textures/gui/gui_voxels.atlas");

        int i = 0;

        for (Map.Entry<Byte, Byte> data : ingredients.entrySet()) {
            Table ingredient = new Table();
            ingredient.pad(8f);

            Image icon = new Image(ingredientAtlas.findRegion(String.valueOf(data.getKey())));
            ingredient.add(icon).size(32f, 32f).grow().row();

            Label amount = new Label(String.valueOf(data.getValue()), skin);
            amount.setAlignment(Align.center);
            ingredient.add(amount).growX().row();

            productIngredients.add(ingredient).pad(8f).grow();

            if (i % 3 == 2) {
                productIngredients.row();
            }

            i++;
        }

        // product creation
        TextButton productCreationButton = new TextButton("Craft", skin);
        product.add(productCreationButton).growX();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            setVisible(!isVisible());
            playerEntity.setFocused(!isVisible());

            if (isVisible()) {
                setPosition(getStage().getWidth() / 2f - getWidth() / 2f, getStage().getHeight() / 2f - getHeight() / 2f);
            }
        }
    }
}
