package net.johanbasson.fp.api.system.events;

import net.johanbasson.fp.api.users.Principal;

public interface EventRepository {

    void append(Principal principal, String type, String payload);

}
