package kz.ilotterytea.voxelphalia.l10n;

public enum LineId {
    LOADING_TITLE,
    LOADING_TERRAIN_GENERATING,
    LOADING_TERRAIN_BUILDING,
    LOADING_MINERAL,
    LOADING_TREES,
    LOADING_LIFE,
    LOADING_SIMULATION,
    LOADING_FINISHED,

    PAUSE_TITLE,
    PAUSE_RETURN,
    PAUSE_QUIT,

    DEATH_TITLE,
    DEATH_RESPAWN,

    SMELTING_TITLE,
    SMELTING_SMELT,

    CRAFTING_HANDMADE_TITLE,
    CRAFTING_BASIC_TITLE,
    CRAFTING_CRAFT,
    CRAFTING_REMAINING,
    CRAFTING_INGREDIENTS,

    CHEST_TITLE,
    INVENTORY_TITLE;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace('_', '.');
    }
}
