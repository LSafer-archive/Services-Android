package lsafer.services.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * permissions list of what target class needs.
 *
 * @author LSaferSE
 * @version 1
 * @since 27-Jun-19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    /**
     * permissions the target class needs.
     *
     * @return permissions target class needs
     */
    String[] value();
}
