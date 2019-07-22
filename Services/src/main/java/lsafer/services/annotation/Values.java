package lsafer.services.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * simple description of the target class.
 *
 * @author LSaferSE
 * @version 1
 * @since 27-Jun-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Values {
    /**
     * allowed values of the target field.
     *
     * @return the allowed values of targeted field
     */
    String[] value();
}
