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
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    /**
     * description of the target class.
     *
     * @return the description of targeted class
     */
    String value();
}
