package net.johanbasson.fp.api.system.commandbus;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

final class ValueProviderFactory {

    private ValueProviderFactory() {
    }

    static List<CommandBusBuilder.ValueProvider> create(Object candidate) {
        return extractValueProviderMethods(candidate).stream()
                .map(method -> new CommandBusBuilder.ValueProvider(candidate, method, extractProvidedValueDescription(method)))
                .collect(toList());
    }

    private static CommandBusBuilder.TypeDescription extractProvidedValueDescription(Method m) {
        return new CommandBusBuilder.TypeDescription(m.getReturnType(), m.getName());
    }

    @SuppressWarnings("unchecked")
    private static Set<Method> extractValueProviderMethods(Object valueProvider) {
        Set<Method> handlingMethods = getAllMethods(valueProvider.getClass(), withAnnotation(Provider.class));
        if (handlingMethods.stream()
                .anyMatch(m -> m.getParameterCount() > 0)) {

            throw new IllegalStateException(format("Provider method of value provider %s should have 0 parameters", valueProvider.getClass().getName()));
        }

        return handlingMethods;
    }
}
