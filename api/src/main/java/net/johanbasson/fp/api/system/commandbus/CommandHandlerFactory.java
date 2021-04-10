package net.johanbasson.fp.api.system.commandbus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

final class CommandHandlerFactory {

    private CommandHandlerFactory() {
    }

    static List<Tuple<Class, CommandBusBuilder.CommandHandler>> create(Object candidate, Map<Class, Map<String, CommandBusBuilder.ValueProvider>> valueProvidersMapping) {
        return extractCommandHandlingMethods(candidate).stream()
                .map(method -> {
                    List<CommandBusBuilder.TypeDescription> params = extractParams(method);
                    return new Tuple<>(params.get(0).type, new CommandBusBuilder.CommandHandler(
                            candidate,
                            method,
                            getValueProvidersForParams(
                                    candidate.getClass(),
                                    valueProvidersMapping,
                                    params)));
                })
                .collect(toList());
    }

    private static List<CommandBusBuilder.TypeDescription> extractParams(Method m) {
        return Arrays.stream(m.getParameters())
                .map(param -> new CommandBusBuilder.TypeDescription(param.getType(), param.getName()))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private static Set<Method> extractCommandHandlingMethods(Object commandHandler) {
        Set<Method> handlingMethods = getAllMethods(commandHandler.getClass(), withAnnotation(CommandHandler.class));
        if (handlingMethods.stream()
                .anyMatch(m -> m.getParameterCount() == 0)) {

            throw new IllegalStateException(format("Handle method of command handler %s should have at least 1 parameter - command to handle", commandHandler.getClass().getName()));
        }

        return handlingMethods;
    }

    private static List<CommandBusBuilder.ValueProvider> getValueProvidersForParams(Class commandHandlerClass, Map<Class, Map<String, CommandBusBuilder.ValueProvider>> providersMapping, List<CommandBusBuilder.TypeDescription> params) {

        return params.stream()
                .skip(1)
                .map(description -> {
                    Map<String, CommandBusBuilder.ValueProvider> possibleProviders = providersMapping.get(description.type);

                    if (possibleProviders == null || possibleProviders.isEmpty()) {
                        throw new IllegalStateException(format("Command handler %s required value of type %s. Value provider not found.", commandHandlerClass.getName(), description.type));
                    }

                    if (possibleProviders.size() == 1) {
                        return possibleProviders.values().iterator().next();
                    }

                    CommandBusBuilder.ValueProvider valueProvider = possibleProviders.get(description.name);
                    if (valueProvider == null) {
                        throw new IllegalStateException(format("Command handler %s required value of type %s with name %s. Value provider not found.",
                                commandHandlerClass.getName(),
                                description.type,
                                description.name));
                    }

                    return valueProvider;
                })
                .collect(toList());
    }

    static final class Tuple<T, Q> {
        private T first;
        private Q second;

        Tuple(T first, Q second) {
            this.first = first;
            this.second = second;
        }

        T getFirst() {
            return first;
        }

        Q getSecond() {
            return second;
        }
    }
}
