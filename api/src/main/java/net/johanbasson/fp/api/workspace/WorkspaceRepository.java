package net.johanbasson.fp.api.workspace;

import cyclops.control.Maybe;
import cyclops.reactive.IO;
import net.johanbasson.fp.api.types.Description;
import net.johanbasson.fp.api.types.Identifier;
import net.johanbasson.fp.api.types.Name;
import net.johanbasson.fp.api.users.Principal;

import java.util.List;

public interface WorkspaceRepository {

    IO<Maybe<Workspace>> findByName(Principal principal, Name name);

    IO<Integer> add(Principal principal, Identifier id, Name name, Description description);

    IO<List<Workspace>> list(Principal principal);
}
