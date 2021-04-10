package net.johanbasson.fp.api.system.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import net.johanbasson.fp.api.workspace.WorkspaceCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPort {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(EventPort.class);

    public EventPort(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Subscribe
    private void handle(WorkspaceCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            eventRepository.append(event.principal(), event.getClass().getName(), json);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize event", e);
        }
    }
}
