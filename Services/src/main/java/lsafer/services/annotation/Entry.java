package lsafer.services.annotation;

import java.lang.annotation.*;

/**
 * Annotate that the target is an Entry.
 *
 * @author LSaferSE
 * @version 2 (06-Sep-2019)
 * @since 27-Jun-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Entry {
    /**
     * The absolute name of the field that contains the description Id for this.
     *
     * @return the absolute description ID name for this
     */
    String descriptionId() default "";

    /**
     * The type of the editor that suppose to edit the targeted entry.
     *
     * @return the targeted entry's editor type
     */
    String editorType() default "JSON";

    /**
     * An array of proper values for this entry.
     *
     * @return the proper values for this entry
     */
    String[] value() default {};

    /**
     * The absolute name of the field that contains the description Id for this entry's values.
     *
     * @return the absolute description ID name for this entry's values
     */
    String valuesDescriptionId() default "";
}
