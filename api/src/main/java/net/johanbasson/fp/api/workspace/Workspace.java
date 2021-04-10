package net.johanbasson.fp.api.workspace;

import java.time.LocalDateTime;
import java.util.UUID;

public record Workspace(UUID id, String name, String description, LocalDateTime created, LocalDateTime modified) {
}
