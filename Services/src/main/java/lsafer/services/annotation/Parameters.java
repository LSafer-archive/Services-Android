package lsafer.services.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * parameters that the targeted method needs
 * and the parameters that the targeted methods
 * will return.
 *
 * @author LSaferSE
 * @version 2
 * @since 05-Jul-19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    /**
     * arguments that the linked method well need.
     *
     * @return arguments that the linked method well need
     */
    String[] input();

    /**
     * arguments that the linked method well return.
     *
     * @return arguments that the linked method well send
     */
    String[] output();
}
