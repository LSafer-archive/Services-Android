package lsafer.services.util;


import lsafer.json.JSON;
import lsafer.services.annotation.Description;
import lsafer.services.annotation.Parameters;
import lsafer.services.annotation.Permissions;
import lsafer.services.annotation.Values;
import lsafer.util.AbstractStructure;

/**
 * a structure get data about custom stems.
 *
 * @author LSaferSE
 * @version 2
 * @since 11-Jun-2019
 */
@SuppressWarnings("WeakerAccess")
public class Properties extends AbstractStructure {

    /**
     * class's description.
     */
    public String description;

    /**
     * fields of the class.
     */
    public Field[] fields;

    /**
     * methods to invoke inside the class.
     */
    public Method[] methods;

    /**
     * name of the class.
     */
    public String name;

    /**
     * permission the class needs.
     */
    public String[] permissions;

    /**
     * default constructor.
     */
    public Properties() {

    }

    /**
     * load data from the given class.
     *
     * @param object to load data of
     */
    public Properties(Object object) {
        Class<?> klass = object.getClass();
        Description description = klass.getAnnotation(Description.class);
        Permissions permissions = klass.getAnnotation(Permissions.class);
        java.lang.reflect.Field[] fields = klass.getDeclaredFields();
        java.lang.reflect.Method[] methods = klass.getDeclaredMethods();

        this.name = klass.getName();

        if (permissions != null) {
            this.permissions = permissions.value();
        }

        if (description != null) {
            this.description = description.value();
        }

        //declaring fields
        this.fields = new Field[fields.length];
        for (int i = 0; i < fields.length; i++)
            this.fields[i] = new Field(fields[i], object);

        //declaring methods
        this.methods = new Method[methods.length];
        for (int i = 0; i < methods.length; i++)
            this.methods[i] = new Method(methods[i]);
    }

    /**
     * a structure to review data about task part's field.
     */
    public static class Field extends AbstractStructure {

        /**
         * default value of the field.
         */
        public Object defaultValue = null;

        /**
         * description of the field.
         */
        public String description;

        /**
         * name of the field.
         */
        public String name;

        /**
         * allowed values of the field.
         */
        public Value[] values;

        /**
         * default constructor.
         */
        public Field() {

        }

        /**
         * fill this with the given field's properties.
         *
         * @param field  to get data from
         * @param object to get default value from
         */
        public Field(java.lang.reflect.Field field, Object object) {
            Description description = field.getAnnotation(Description.class);
            Values values = field.getAnnotation(Values.class);

            this.name = field.getName();

            try {
                this.defaultValue = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (description != null) {
                this.description = description.value();
            }

            if (values != null) {
                String[] v = values.value();
                this.values = new Value[v.length];
                for (int i = 0; i < v.length; i++)
                    this.values[i] = new Value(v[i]);
            }
        }

        /**
         * a structure to review data about task part field's value.
         */
        public static class Value extends AbstractStructure {

            /**
             * description of the value.
             */
            public String description;

            /**
             * name of the value.
             */
            public Object name;

            /**
             * default constructor.
             */
            public Value() {
            }

            /**
             * fill this with the given value source.
             *
             * @param source to get data from
             */
            public Value(String source) {
                String[] s = source.split("[|]");

                if (s.length > 0) {
                    this.name = JSON.parse(s[0]);
                }

                if (s.length > 1) {
                    this.description = s[1];
                }
            }
        }

    }

    /**
     * a structure to review data about task part's method.
     */
    public static class Method extends AbstractStructure {

        /**
         * description of the method.
         */
        public String description;

        /**
         * arguments that must be passed to the method.
         */
        public String[] input;

        /**
         * name of the method.
         */
        public String name;

        /**
         * arguments that well be returned from the method.
         */
        public String[] output;

        /**
         * init this.
         */
        public Method() {
        }

        /**
         * fill this with the given method's properties.
         *
         * @param method to get data from
         */
        public Method(java.lang.reflect.Method method) {
            Parameters parameters = method.getAnnotation(Parameters.class);
            Description description = method.getAnnotation(Description.class);

            this.name = method.getName();

            if (description != null) {
                this.description = description.value();
            }

            if (parameters != null) {
                this.input = parameters.input();
                this.output = parameters.output();
            }
        }

    }

}