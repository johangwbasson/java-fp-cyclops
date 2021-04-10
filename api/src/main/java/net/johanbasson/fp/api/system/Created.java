package net.johanbasson.fp.api.system;

import net.johanbasson.fp.api.types.Identifier;

public class Created {
    private final String id;

    public Created(Identifier identifier) {
        this.id = identifier.asString();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Created{" +
                "id=" + id +
                '}';
    }
}
