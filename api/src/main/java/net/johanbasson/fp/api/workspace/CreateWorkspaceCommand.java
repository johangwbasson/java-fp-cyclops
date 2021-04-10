package net.johanbasson.fp.api.workspace;

import net.johanbasson.fp.api.types.Description;
import net.johanbasson.fp.api.types.Identifier;
import net.johanbasson.fp.api.types.Name;
import net.johanbasson.fp.api.users.Principal;

public record CreateWorkspaceCommand(Principal principal, Name name, Description description, Identifier id) {

}
