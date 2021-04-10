CREATE TABLE workspaces (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id),
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(512),
    created     TIMESTAMP,
    modified    TIMESTAMP,
    deleted     BOOLEAN
);

CREATE UNIQUE INDEX workspaces_user_name_idx ON workspaces(user_id, name);
CREATE INDEX workspaces_user_deleted_idx ON workspaces(user_id, deleted);