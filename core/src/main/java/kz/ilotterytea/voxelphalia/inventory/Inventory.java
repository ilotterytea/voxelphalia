package kz.ilotterytea.voxelphalia.inventory;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class Inventory {
    public static class Slot {
        public final byte id, size;
        public byte quantity;

        public Slot(byte id, byte quantity, byte size) {
            this.id = id;
            this.size = size;
            this.quantity = quantity;
        }

        public byte add() {
            return add((byte) 1);
        }

        public byte add(byte quantity) {
            int nq = this.quantity + quantity;
            this.quantity = (byte) Math.min(nq, size);
            return (byte) (nq >= size ? nq - size : 0);
        }

        public byte remove() {
            return remove((byte) 1);
        }

        public byte remove(byte quantity) {
            int nq = this.quantity - quantity;
            this.quantity = (byte) Math.max(nq, 0);
            return (byte) (nq < 0 ? Math.abs(nq) : 0);
        }

        @Override
        public String toString() {
            return "Slot{" +
                "id=" + id +
                ", quantity=" + quantity +
                '}';
        }
    }

    private final Slot[] slots;
    private final byte stackSize;
    private final int size;
    private int currentSlotIndex;

    public Inventory(int size, byte stackSize) {
        this.size = size;
        this.slots = new Slot[size];
        this.stackSize = stackSize;
    }

    public byte add(byte voxel) {
        return add(voxel, (byte) 1);
    }

    public byte add(byte voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            index = getSlotIndex(voxel, quantity, false);

            if (index < 0) return quantity;
            if (slots[index] == null) slots[index] = new Slot(voxel, (byte) 0, stackSize);

            quantity = this.slots[index].add(quantity);
        }

        return quantity;
    }

    public byte remove(byte voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            index = getSlotIndex(voxel, quantity, true);

            if (index < 0) return quantity;
            if (slots[index].quantity == 0) slots[index] = null;

            quantity = this.slots[index].remove(quantity);
        }

        return quantity;
    }

    public void nextSlotIndex() {
        setSlotIndex(currentSlotIndex - 1);
    }

    public void previousSlotIndex() {
        setSlotIndex(currentSlotIndex + 1);
    }

    public void setSlotIndex(int index) {
        this.currentSlotIndex = MathUtils.clamp(index - 1, 0, this.slots.length);
    }

    public Slot getCurrentSlot() {
        return slots[currentSlotIndex];
    }

    public Slot[] getSlots() {
        return slots;
    }

    private int getSlotIndex(byte voxel, byte quantity, boolean ignoreNull) {
        int index = -1;

        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];

            if (slot == null) {
                if (index == -1 && !ignoreNull) {
                    index = i;
                }
            } else if (slot.id == voxel && (int) slot.quantity + quantity <= stackSize) {
                return i;
            }
        }

        return index;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Inventory{" +
            "slots=" + Arrays.toString(slots) +
            ", currentSlotIndex=" + currentSlotIndex +
            '}';
    }
}
