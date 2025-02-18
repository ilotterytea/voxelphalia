package kz.ilotterytea.voxelphalia.recipes;

public record RecipeData(byte[][] ingredients, byte resultAmount, byte resultId, RecipeWorkbenchLevel level) {
}
