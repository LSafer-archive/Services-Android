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
 * @author LSaferSE
 * @version 6 release (07-Sep-2019)
 * @since 11-Jun-2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
     * @param object    to load data of
     */
    public Properties(Resources resources, Object object) {
        Controller annotation = object.getClass().getAnnotation(Controller.class);
        assert annotation != null;

        int descRes = annotation.description();

        this.name = object.getClass().getName();
        this.description = descRes == 0 ? "" : resources.getString(descRes);

        //declaring fields
        for (Field field : object.getClass().getDeclaredFields())
            if (field.isAnnotationPresent(lsafer.services.annotation.Entry.class))
                this.entries.put(field.getName(), new Entry(resources, object, field));

        //declaring methods
        for (Method method : object.getClass().getDeclaredMethods())
            if (method.isAnnotationPresent(lsafer.services.annotation.Invokable.class))
                this.invokables.put(method.getName(), new Invokable(resources, object, method));
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
         * What editor should edit this entry. And how should the editor do it.
         */
        public String editor = "";

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
         * @param object    to get default value from
         * @param field     to get data from
         */
        public Entry(Resources resources, Object object, Field field) {
            lsafer.services.annotation.Entry annotation = field.getAnnotation(lsafer.services.annotation.Entry.class);
            assert annotation != null;

            int valuesDescRes = annotation.values_description();
            int descRes = annotation.description();

            String[] values = annotation.value();
            String[] descriptions = valuesDescRes == 0 ? new String[0] : resources.getStringArray(valuesDescRes);

            this.name = field.getName();
            this.type = field.getType();
            this.editor = annotation.editor();
            this.description = descRes == 0 ? "" : resources.getString(descRes);

            try {
                this.defaultValue = field.get(object);
            } catch (IllegalAccessException ignored) {
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
         * @param object    to load data of
         * @param method    to get data from
         */
        public Invokable(Resources resources, Object object, Method method) {
            lsafer.services.annotation.Invokable annotation = method.getAnnotation(lsafer.services.annotation.Invokable.class);
            assert annotation != null;

            int paramsDescRes = annotation.params_description();
            int descRes = annotation.description();

            String[] parameters = annotation.value();
            String[] defaults = annotation.defaults();
            Class[] types = method.getParameterTypes();
            String[] descriptions = paramsDescRes == 0 ? new String[0] : resources.getStringArray(paramsDescRes);

            this.name = method.getName();
            this.redirect.addAll(Arrays.asList(annotation.redirect()));
            this.description = descRes == 0 ? "" : resources.getString(descRes);

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
