package net.johanbasson.fp.api.workspace;

import net.johanbasson.fp.api.system.commandbus.CommandHandler;
import net.johanbasson.fp.api.system.queue.EventDispatcher;

public class WorkspaceCommandHandlers {

    private final WorkspaceRepository workspaceRepository;
    private final EventDispatcher eventDispatcher;

    public WorkspaceCommandHandlers(WorkspaceRepository workspaceRepository, EventDispatcher eventDispatcher) {
        this.workspaceRepository = workspaceRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @CommandHandler
    public void handle(CreateWorkspaceCommand command) {
        workspaceRepository.add(command.principal(), command.id(), command.name(), command.description());
        eventDispatcher.dispatch(new WorkspaceCreatedEvent(command.principal(), command.id(), command.name(), command.description()));
    }
}
