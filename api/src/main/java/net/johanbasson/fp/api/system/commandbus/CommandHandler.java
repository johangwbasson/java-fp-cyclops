package net.johanbasson.fp.api.system.commandbus;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;


/**
 * Is used to mark method as <em>CommandHandler</em>
 * which then can be registered in BusBuilder with {@link CommandBusBuilder#registerCommandHandler(Object)}
 *
 * <p> Each method annotated with {@link CommandHandler}
 * should have at least one parameter, which is <em>Command</em>
 * that will be handled by this method(<em>CommandHandler</em>).
 * If there are more than one param, then others params
 * will be supplied by {@link Provider}.
 *
 * <p>It is possible for a class to have more than one method
 * annotated with {@link CommandHandler}. But there should be
 * <b>exactly one</b> <em>CommandHandler</em> for each <em>>Command</em>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = METHOD)
public @interface CommandHandler {
}
