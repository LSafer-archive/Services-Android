package lsafer.services.annotation;

import java.lang.annotation.*;

/**
 * Annotate that the target is a controller that can contains an {@link Entry entries} and {@link Invokable invokables}.
 *
 * @author LSaferSE
 * @version 2 alpha (06-Sep-2019)
 * @since 27-Aug-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Controller {
    /**
     * The absolute name of the field that contains the description Id for this.
     *
     * @return the absolute description ID name for this
     */
    String descriptionId() default "";
}
