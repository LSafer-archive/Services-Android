package lsafer.services.annotation;

import androidx.annotation.StringRes;

import java.lang.annotation.*;

/**
 * Annotate that the target is a controller that can contains an {@link Entry entries} and {@link Invokable invokables}.
 *
 * @author LSaferSE
 * @version 3 alpha (07-Sep-2019)
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
    @StringRes
    int description() default 0;
}
