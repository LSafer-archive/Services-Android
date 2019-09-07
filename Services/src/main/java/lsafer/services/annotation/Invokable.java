package lsafer.services.annotation;

import lsafer.services.util.Process;

import java.lang.annotation.*;

/**
 * Annotate that the target is an Invokable.
 *
 * @author LSaferSE
 * @version 2 (06-Sep-2019)
 * @since 27-Jun-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Invokable {
    /**
     * Usually used to get data from the next {@link Process}.
     * Also usually the next process will callback it's
     * previous {@link Process process} with the requested
     * data using {@link #results} method.
     */
    String get = "get";

    /**
     * Usually used to pass the previously requested data.
     */
    String results = "results";

    /**
     * Usually used to start the main command of the targeted {@link Process process}.
     */
    String start = "start";

    /**
     * Usually used to stop any operations of the targeted {@link Process process}.
     */
    String stop = "stop";

    /**
     * Usually used to update/refresh the targeted {@link Process process}.
     */
    String update = "update";

    /**
     * The absolute name of the field that contains the description Id for this.
     *
     * @return the absolute description ID name for this
     */
    String DescriptionId() default "";

    /**
     * The default parameter values for this invokable.
     *
     * @return the default parameter values for this
     */
    String[] defaults() default {""};

    /**
     * The absolute name of the field that contains the description Id for this invokable's params.
     *
     * @return the absolute description ID name for this invokable's params
     */
    String paramsDescriptionId() default "";

    /**
     * The targeted key for each parameter of the targeted method.
     *
     * @return the keys of the parameters of the targeted method
     */
    String[] value() default {""};
}