package lsafer.services.io;

import android.util.Log;

import java.util.Map;
import java.lang.Object;

import lsafer.io.File;
import lsafer.io.JSONFileStructure;
import lsafer.lang.JSON;
import lsafer.services.annotation.Description;
import lsafer.services.annotation.Parameters;
import lsafer.services.annotation.Permissions;
import lsafer.services.annotation.Values;
import lsafer.services.util.Arguments;
import lsafer.util.Arrays;
import lsafer.util.Structure;

/**
 * a structure to be overridden and used as a part of a {@link Task task}.
 * structure linked with {@link Map} as a secondary container
 * and {@link File JSON file} as a third IO container.
 * depends on {@link File#readJSON(Object)} (Object)} and {@link File#writeJSON(Object)}
 * <p>
 * make sure your {@link TaskPart task-part} matches all {@link JSONFileStructure json-file-structures} rules
 * <p>
 *
 *
 * @author LSaferSE
 * @version 6
 * @since 10 Jun 2019
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class TaskPart extends JSONFileStructure {

    /**
     * get data.
     */
    final public static String $GET = "get";

    /**
     * run the main script.
     */
    final public static String $START = "start";

    /**
     * revers run effect.
     */
    final public static String $STOP = "stop";

    /**
     * update this.
     */
    final public static String $UPDATE = "update";

    /**
     * to print errors at.
     */
    final public static String $TAG = "Services Task Part";

    /**
     * the path of the apk that contains this class.
     */
    public String apk_path;

    /**
     * the name of the class of this to be generated.
     */
    public String class_name;

    /**
     * this stem's index.
     */
    protected int $index;

    /**
     * collection that have started this.
     */
    protected Task $task;

    /**
     * init this.
     *
     * @param args to init with
     */
    public TaskPart(Object... args) {
        super(args);

    }

    /**
     * get the properties of this class.
     *
     * @return the properties of this class
     */
    public Properties properties() {
        return new Properties(this);

    }

    /**
     * run a method inside this.
     *
     * @param name      of method
     * @param expected  arguments to be returned | use it link "value, text, context"
     * @param arguments to pass
     * @return arguments from the method
     */
    public Arguments run(String name, String[] expected, Object... arguments) {
        try {
            Arguments args = Arguments.newInstance(arguments);
            java.lang.reflect.Method method = getClass().getMethod(name, Arguments.class);
            Parameters parameters = method.getAnnotation(Parameters.class);

            if (parameters == null)
                Log.e($TAG, "run: no Parameters annotation on " + name + " method in " + getClass());
            else {
                for (String param : parameters.input())
                    if (!args.containsKey(param))
                        Log.e($TAG, "run: " + param + " parameter is needed to invoke " + name);

                if (expected != null && !Arrays.all(parameters.output(), expected))
                    Log.e($TAG, "run: method " + name + " don't returns all of " + java.util.Arrays.toString(expected), null);
            }

            Arguments output = (Arguments) method.invoke(this, args);
            return output == null ? new Arguments() : output;
        } catch (Exception e) {
            Log.e($TAG, "run: an error occurred while invoking " + name + " in the task part " + this.getClass() + " index=" + $index + " task=" + $task, e);
        }

        return new Arguments();
    }

    /**
     * run a method in the next stem of this.
     *
     * @param name      of the method
     * @param expected  arguments to be returned
     * @param arguments to pass
     * @return arguments that have returned from the method
     */
    public Arguments next(String name, String[] expected, Object... arguments) {
        return $task.run($index + 1, name, expected, arguments);

    }

    /**
     * a structure get data about custom stems.
     *
     * @author LSaferSE
     * @version 2
     * @since 11-Jun-2019
     */
    final public static class Properties extends Structure {

        /**
         * class's description.
         */
        public String description = "";

        /**
         * fields of the class.
         */
        public Field[] fields = {};

        /**
         * methods to invoke inside the class.
         */
        public Method[] methods = {};

        /**
         * name of the class.
         */
        public String name = "";

        /**
         * permission the class needs.
         */
        public String[] permissions = {};

        /**
         * for {@link Structure}.
         *
         * @param arguments to pass to super.
         */
        public Properties(Object... arguments) {
            super(arguments);

        }

        /**
         * load data from the given class.
         *
         * @param part to load data of
         */
        private Properties(TaskPart part) {
            Class<? extends TaskPart> klass = part.getClass();
            Description description = klass.getAnnotation(Description.class);
            Permissions permissions = klass.getAnnotation(Permissions.class);
            java.lang.reflect.Field[] fields = klass.getDeclaredFields();
            java.lang.reflect.Method[] methods = klass.getDeclaredMethods();

            assert permissions != null;
            assert description != null;

            this.name = klass.getName();
            this.permissions = permissions.value();
            this.description = description.value();

            this.fields = new Field[fields.length];
            for (int i = 0; i < fields.length; i++)
                this.fields[i] = new Field(fields[i], part);

            this.methods = new Method[methods.length];
            for (int i = 0; i < methods.length; i++)
                this.methods[i] = new Method(methods[i]);
        }

        /**
         * a structure to review data about task part's field.
         */
        final public static class Field extends Structure {

            /**
             * default value of the field.
             */
            public Object defaultValue = null;

            /**
             * description of the field.
             */
            public String description = "";

            /**
             * name of the field.
             */
            public String name = "";

            /**
             * allowed values of the field.
             */
            public Value[] values = {};

            /**
             * to init this.
             *
             * @param arguments to init with
             */
            public Field(Object... arguments) {
                super(arguments);

            }

            /**
             * fill this with the given field's properties.
             *
             * @param field  to get data from
             * @param object to get default value from
             */
            private Field(java.lang.reflect.Field field, Object object) {
                Description description = field.getAnnotation(Description.class);
                Values values = field.getAnnotation(Values.class);

                assert values != null;
                assert description != null;

                this.name = field.getName();
                this.description = description.value();

                try {
                    this.defaultValue = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                String[] v = values.value();
                this.values = new Value[v.length];
                for (int i = 0; i < v.length; i++)
                    this.values[i] = new Value(v[i]);
            }

            /**
             * a structure to review data about task part field's value.
             */
            final public static class Value extends Structure {

                /**
                 * description of the value.
                 */
                public String description = "";

                /**
                 * name of the value.
                 */
                public Object name = "";

                /**
                 * fill this with the given value source.
                 *
                 * @param source to get data from
                 */
                private Value(String source) {
                    String[] s = source.split("[|]");

                    this.name = JSON.parse(s[0]);
                    this.description = s[1];
                }

                /**
                 * init this.
                 *
                 * @param arguments to init with
                 */
                public Value(Object... arguments) {
                    super(arguments);

                }

            }

        }

        /**
         * a structure to review data about task part's method.
         */
        final public static class Method extends Structure {

            /**
             * description of the method.
             */
            public String description = "";

            /**
             * arguments that must be passed to the method.
             */
            public String[] input = {};

            /**
             * name of the method.
             */
            public String name = "";

            /**
             * arguments that well be returned from the method.
             */
            public String[] output = {};

            /**
             * init this.
             *
             * @param arguments to init with
             */
            public Method(Object... arguments) {
                super(arguments);

            }

            /**
             * fill this with the given method's properties.
             *
             * @param method to get data from
             */
            private Method(java.lang.reflect.Method method) {
                Parameters parameters = method.getAnnotation(Parameters.class);
                Description description = method.getAnnotation(Description.class);

                assert parameters != null;
                assert description != null;

                this.name = method.getName();
                this.input = parameters.input();
                this.output = parameters.output();
                this.description = description.value();
            }

        }

    }

}
