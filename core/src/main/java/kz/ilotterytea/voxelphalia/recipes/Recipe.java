package kz.ilotterytea.voxelphalia.recipes;

public record Recipe(byte[][] ingredients, byte resultAmount, byte resultId, float craftingTime,
                     RecipeWorkbenchLevel level) {
}
