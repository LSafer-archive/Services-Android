package lsafer.services.util;

import android.content.res.Resources;
import lsafer.services.annotation.Controller;
import lsafer.util.HashStructure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A structure contains properties of a {@link Controller}.
 *
 * <ul>
 * <li>
 * <b>Class Description</b> [_<font color="blue">MyClass</font>__description]
 * <br>
 * {@link #Properties(Resources, Class, Object)}
 * </li>
 * <li>
 * <b>Entry Description</b> [_<font color="blue">MyClass</font>__<font color="blue">MyNode</font>__description]
 * <br>
 * {@link Entry#Entry(Resources, Class, Object, Field)}
 * </li>
 * <li>
 * <b>Entry Value Description</b> [_<font color="blue">MyClass</font>__<font color="blue">MyNode</font>__values__description]
 * <br>
 * {@link Entry#Entry(Resources, Class, Object, Field)}
 * </li>
 * <li>
 * <b>Invokable Description</b> [_<font color="blue">MyClass</font>__<font color="blue">MyInvokable</font>__description]
 * <br>
 * {@link Invokable#Invokable(Resources, Class, Object, Method)}
 * </li>
 * <li>
 * <b>Invokable Parameter Description</b> [_<font color="blue">MyClass</font>__<font color="blue">MyInvokable</font>__parameters__description]
 * <br>
 * {@link Invokable#Invokable(Resources, Class, Object, Method)}
 * </li>
 * </ul>
 *
 * @author LSaferSE
 * @version 6 release (07-Sep-2019)
 * @since 11-Jun-2019
 */
@SuppressWarnings("WeakerAccess")
public class Properties extends HashStructure {
    /**
     * Controller's description.
     */
    public String description = "";

    /**
     * Available {@link lsafer.services.annotation.Entry entries} in the targeted {@link Controller}.
     */
    public Map<String, Entry> entries = new HashMap<>();

    /**
     * Available {@link lsafer.services.annotation.Invokable invokables} in the targeted {@link Controller}.
     */
    public Map<String, Invokable> invokables = new HashMap<>();

    /**
     * The Name of the targeted {@link Controller}.
     */
    public String name = "";

    /**
     * Default constructor.
     */
    public Properties() {
    }

    /**
     * Load data from the given {@link Controller}.
     *
     * <ul>
     * <li>note: the given object SHOULD be annotated with {@link Controller}.</li>
     * </ul>
     *
     * @param resources to get strings from
     * @param R_string  to find strings IDs from
     * @param object    to load data of
     */
    public Properties(Resources resources, Class<?> R_string, Object object) {
        Controller annotation = object.getClass().getAnnotation(Controller.class);
        assert annotation != null;

        String descResId = annotation.descriptionId();

        String[] descriptions = {};

        this.name = object.getClass().getName();

        try {
            this.description = resources.getString((int) R_string.getField(
                    descResId.equals("") ? "_" + object.getClass().getSimpleName() + "__description" : descResId
            ).get(null));
        } catch (Exception ignored) {
        }

        //declaring fields
        for (Field field : object.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(lsafer.services.annotation.Entry.class))
                this.entries.put(field.getName(), new Entry(resources, R_string, object, field));

        //declaring methods
        for (Method method : object.getClass().getDeclaredMethods())
            if (method.isAnnotationPresent(lsafer.services.annotation.Invokable.class))
                this.invokables.put(method.getName(), new Invokable(resources, R_string, object, method));
    }

    /**
     * A structure contains properties of an {@link lsafer.services.annotation.Entry}.
     */
    public static class Entry extends HashStructure {
        /**
         * The default value of the targeted {@link lsafer.services.annotation.Entry}.
         */
        public Object defaultValue = "";

        /**
         * The description for the targeted {@link lsafer.services.annotation.Entry}.
         */
        public String description = "";

        /**
         * The name of the targeted {@link lsafer.services.annotation.Entry}.
         */
        public String name = "";

        /**
         * The type of this entry's value.
         */
        public Class type = Object.class;

        /**
         * Proper values for the targeted {@link lsafer.services.annotation.Entry}.
         */
        public List<Value> values = new ArrayList<>();

        /**
         * Default constructor.
         */
        public Entry() {
        }

        /**
         * Load data from the given {@link lsafer.services.annotation.Entry}.
         *
         * @param resources to get resources from
         * @param R_string  to navigate Ids from
         * @param object    to get default value from
         * @param field     to get data from
         */
        public Entry(Resources resources, Class<?> R_string, Object object, Field field) {
            lsafer.services.annotation.Entry entry = field.getAnnotation(lsafer.services.annotation.Entry.class);
            assert entry != null;

            String[] values = entry.value();
            String descResId = entry.descriptionId();
            String valuesDescResId = entry.valuesDescriptionId();

            String[] descriptions = {};

            this.name = field.getName();
            this.type = field.getType();

            try {
                this.defaultValue = field.get(object);
            } catch (IllegalAccessException ignored) {
            }
            try {
                this.description = resources.getString((int) R_string.getField(
                        descResId.equals("") ? "_" + object.getClass().getSimpleName() + "__" + field.getName() +
                                               "__description" : descResId
                ).get(null));
            } catch (Exception ignored) {
            }
            try {
                descriptions = resources.getStringArray((int) R_string.getField(
                        valuesDescResId.equals("") ? "_" + object.getClass().getSimpleName() + "__" + field.getName() +
                                                     "__values__description" : valuesDescResId
                ).get(null));
            } catch (Exception ignored) {
            }

            for (int i = 0; i < values.length; i++)
                this.values.add(new Value(values[i], descriptions.length > i ? descriptions[i] : ""));
        }

        /**
         * A structure contains properties of an {@link lsafer.services.annotation.Entry}'s proper value.
         */
        public static class Value extends HashStructure {
            /**
             * The description for this {@link Value}.
             */
            public String description = "";

            /**
             * The name ("value") of this {@link Value}.
             */
            public String name = "";

            /**
             * Default constructor.
             */
            public Value() {
            }

            /**
             * Load this from the given data.
             *
             * @param description of this value
             * @param name        to get data from
             */
            public Value(String name, String description) {
                this.name = name;
                this.description = description;
            }
        }
    }

    /**
     * A structure contains properties of an {@link lsafer.services.annotation.Invokable}.
     */
    public static class Invokable extends HashStructure {
        /**
         * Invokable's description.
         */
        public String description = "";

        /**
         * The name of the targeted {@link lsafer.services.annotation.Invokable}.
         */
        public String name = "";

        /**
         * Parameters of the targeted {@link lsafer.services.annotation.Invokable}.
         */
        public List<Parameter> parameters = new ArrayList<>();

        /**
         * Method names to be redirected to the targeted invokable method.
         */
        public List<String> redirect = new ArrayList<>();

        /**
         * Default constructor.
         */
        public Invokable() {
        }

        /**
         * Load data from the given {@link lsafer.services.annotation.Invokable}.
         *
         * @param resources to get strings from
         * @param R_string  to find strings IDs from
         * @param object    to load data of
         * @param method    to get data from
         */
        public Invokable(Resources resources, Class<?> R_string, Object object, Method method) {
            lsafer.services.annotation.Invokable annotation = method.getAnnotation(lsafer.services.annotation.Invokable.class);
            assert annotation != null;

            String[] parameters = annotation.value();
            String[] defaults = annotation.defaults();
            String descResId = annotation.DescriptionId();
            String paramsDescResId = annotation.paramsDescriptionId();

            Class[] types = method.getParameterTypes();

            String[] descriptions = {};

            this.name = method.getName();
            this.redirect.addAll(Arrays.asList(annotation.redirect()));

            try {
                this.description = resources.getString((int) R_string.getField(
                        descResId.equals("") ? "_" + object.getClass().getSimpleName() + "__" + method.getName() +
                                               "__description" : descResId
                ).get(null));
            } catch (Exception ignored) {
            }

            try {
                descriptions = resources.getStringArray((int) R_string.getField(
                        paramsDescResId.equals("") ? "_" + object.getClass().getSimpleName() + "__" + method.getName() +
                                                     "__parameters__description" : paramsDescResId
                ).get(null));
            } catch (Exception ignored) {
            }

            for (int i = 0; i < types.length; i++)
                this.parameters.add(new Parameter(types[i],
                        parameters.length > i ? parameters[i] : "",
                        defaults.length > i ? defaults[i] : "",
                        descriptions.length > i ? descriptions[i] : ""));
        }

        /**
         * A structure contains properties of an {@link lsafer.services.annotation.Invokable}'s Parameter.
         */
        public static class Parameter extends HashStructure {
            /**
             * The default value of this {@link Parameter}.
             */
            public String defaultValue = "";

            /**
             * The Description for this {@link Parameter}.
             */
            public String description = "";

            /**
             * The targeted key from this {@link Parameter}.
             */
            public String key = "";

            /**
             * The type of this parameter's value.
             */
            public Class type = Object.class;

            /**
             * Default constructor.
             */
            public Parameter() {
            }

            /**
             * Load this from the given data.
             *
             * @param type         of the value of this parameter
             * @param key          of this parameter
             * @param defaultValue to set this parameter to (case no value passed)
             * @param description  of this parameter
             */
            public Parameter(Class type, String key, String defaultValue, String description) {
                this.defaultValue = defaultValue;
                this.key = key;
                this.type = type;
                this.description = description;
            }
        }
    }
}
