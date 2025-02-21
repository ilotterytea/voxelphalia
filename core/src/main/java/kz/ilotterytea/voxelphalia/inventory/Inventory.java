package kz.ilotterytea.voxelphalia.inventory;

import kz.ilotterytea.voxelphalia.utils.Identifier;

import java.util.Arrays;

public class Inventory implements Cloneable {
    public static class Slot implements Cloneable {
        public Identifier id;
        public byte size;
        public byte quantity;

        public Slot(Identifier id, byte quantity, byte size) {
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

        @Override
        public Slot clone() {
            try {
                Slot clone = (Slot) super.clone();
                clone.id = id;
                clone.size = size;
                clone.quantity = quantity;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    private Slot[] slots;
    private byte stackSize;
    private int size;
    private int currentSlotIndex;

    public Inventory(int size, byte stackSize) {
        this.size = size;
        this.slots = new Slot[size];
        this.stackSize = stackSize;

        for (int i = 0; i < size; i++) {
            this.slots[i] = new Slot(null, (byte) 0, stackSize);
        }
    }

    public byte add(Identifier voxel) {
        return add(voxel, (byte) 1);
    }

    public byte add(Identifier voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            index = getSlotIndex(voxel, quantity);
            if (index < 0) return quantity;

            this.slots[index].id = voxel;
            quantity = this.slots[index].add(quantity);
        }

        return quantity;
    }

    public byte remove(Identifier voxel) {
        return remove(voxel, (byte) 1);
    }

    public byte remove(Identifier voxel, byte quantity) {
        int index;

        while (quantity > 0) {
            if (getCurrentSlot().id != null && getCurrentSlot().id.equals(voxel)) {
                index = currentSlotIndex;
            } else {
                index = getSlotIndex(voxel, quantity);
            }

            Slot slot = this.slots[index];
            if (slot.id == null) return quantity;

            quantity = slot.remove(quantity);
            if (slot.quantity == 0) slot.id = null;
        }

        return quantity;
    }

    public void nextSlotIndex() {
        setSlotIndex(currentSlotIndex + 1);
    }

    public void previousSlotIndex() {
        setSlotIndex(currentSlotIndex - 1);
    }

    public void setSlotIndex(int index) {
        this.currentSlotIndex = (index + this.slots.length) % this.slots.length;
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

    private int getSlotIndex(Identifier voxel, byte quantity) {
        int index = -1;

        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];

            if (slot.id == null && index == -1) {
                index = i;
            } else if (slot.id != null && slot.id.equals(voxel) && (int) slot.quantity + quantity <= stackSize) {
                return i;
            }
        }

        if (getCurrentSlot().id != null && getCurrentSlot().id.equals(voxel)) {
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

    public int getTotalVoxelAmount(Identifier voxel) {
        int amount = 0;

        for (Slot slot : slots) {
            if (slot.id != null && slot.id.equals(voxel)) {
                amount += slot.quantity;
            }
        }

        return amount;
    }

    public void clear() {
        for (Slot slot : slots) {
            slot.id = null;
            slot.quantity = 0;
        }
    }

    @Override
    public String toString() {
        return "Inventory{" +
            "slots=" + Arrays.toString(slots) +
            ", currentSlotIndex=" + currentSlotIndex +
            '}';
    }

    @Override
    public Inventory clone() {
        try {
            Inventory clone = (Inventory) super.clone();
            clone.size = size;
            clone.stackSize = stackSize;
            clone.currentSlotIndex = currentSlotIndex;
            clone.slots = new Slot[size];

            for (int i = 0; i < size; i++) {
                clone.slots[i] = slots[i].clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
