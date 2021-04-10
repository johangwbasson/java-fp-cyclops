package net.johanbasson.fp.api.system.commandbus;

import java.util.function.Function;

/**
 * Bus routes Commands(POJO) to appropriate <em>CommandHandler</em>(class methods annotated with {@link CommandHandler}).
 * If CommandHandler has additional parameters, it will be populated
 * with value provided by <em>ValueProvider</em>(class methods annotated with {@link Provider})
 *
 * <p>Every Command will first go through chain of {@link Middleware}.
 */
public class CommandBus {

    private final Function<Object, Object> commandProcessor;

    CommandBus(Function<Object, Object> commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    /**
     * Synchronous command execution.
     * @param command Command to be sent for execution
     * @return R which is the result of CommandHandler method execution (See {@link CommandHandler})
     * @throws IllegalStateException containing original cause
     */
    public <R> R execute(Object command) {
        //noinspection unchecked
        return (R) commandProcessor.apply(command);
    }

}
