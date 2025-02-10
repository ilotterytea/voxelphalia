package kz.ilotterytea.voxelphalia.inventory;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class Inventory {
    public static class Slot {
        public byte id, size;
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

        for (int i = 0; i < size; i++) {
            this.slots[i] = new Slot((byte) 0, (byte) 0, stackSize);
        }
    }

    public byte add(byte voxel) {
        return add(voxel, (byte) 1);
    }

    public byte add(byte voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            index = getSlotIndex(voxel, quantity);
            if (index < 0) return quantity;

            this.slots[index].id = voxel;
            quantity = this.slots[index].add(quantity);
        }

        return quantity;
    }

    public byte remove(byte voxel) {
        return remove(voxel, (byte) 1);
    }

    public byte remove(byte voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            if (getCurrentSlot().id == voxel) {
                index = currentSlotIndex;
            } else {
                index = getSlotIndex(voxel, quantity);
            }

            Slot slot = this.slots[index];
            if (slot.id == 0) return quantity;

            quantity = slot.remove(quantity);
            if (slot.quantity == 0) slot.id = 0;
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

    public Slot getSlot(int index) {
        return slots[index];
    }

    public Slot[] getSlots() {
        return slots;
    }

    private int getSlotIndex(byte voxel, byte quantity) {
        int index = -1;

        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];

            if (slot.id == 0 && index == -1) {
                index = i;
            } else if (slot.id == voxel && (int) slot.quantity + quantity <= stackSize) {
                return i;
            }
        }

        if (getCurrentSlot().id == 0) {
            return currentSlotIndex;
        }

        return index;
    }

    public int getSize() {
        return size;
    }

    public int getSlotIndex() {
        return currentSlotIndex;
    }

    @Override
    public String toString() {
        return "Inventory{" +
            "slots=" + Arrays.toString(slots) +
            ", currentSlotIndex=" + currentSlotIndex +
            '}';
    }
}
