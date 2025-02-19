package kz.ilotterytea.voxelphalia.recipes;

import kz.ilotterytea.voxelphalia.utils.Identifiable;
import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.Map;

public record Recipe(Map<Identifier, Byte> ingredients, byte resultAmount, Identifier resultId, float craftingTime,
                     RecipeWorkbenchLevel level) implements Identifiable {
    @Override
    public Identifier getId() {
        return resultId;
    }
}
