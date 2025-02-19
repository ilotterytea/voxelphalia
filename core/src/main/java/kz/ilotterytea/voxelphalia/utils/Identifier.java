package kz.ilotterytea.voxelphalia.utils;

import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;

public class Identifier implements Identifiable {
    private final String namespace, name;

    public Identifier(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public Identifier(String name) {
        this(VoxelphaliaConstants.Metadata.APP_ID, name);
    }

    public static Identifier of(String value) {
        try {
            String[] parts = value.split(":");
            String namespace = parts[0];
            String name = value.substring(namespace.length() + 1);

            return new Identifier(namespace, name);
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFullName() {
        return this.namespace + ":" + this.name;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        switch (obj) {
            case Identifier i -> {
                return i.name.equals(name) && i.namespace.equals(namespace);
            }
            case String s -> {
                return s.equals(getFullName());
            }
            case null, default -> {
                return false;
            }
        }
    }

    @Override
    public Identifier getId() {
        return this;
    }
}
