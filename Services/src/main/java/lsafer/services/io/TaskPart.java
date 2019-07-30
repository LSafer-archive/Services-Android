package lsafer.services.io;

import android.util.Log;

import java.util.Map;

import lsafer.io.File;
import lsafer.io.JSONFileStructure;
import lsafer.json.JSON;
import lsafer.services.annotation.Description;
import lsafer.services.annotation.Parameters;
import lsafer.services.annotation.Permissions;
import lsafer.services.annotation.Values;
import lsafer.services.lang.Reflect;
import lsafer.services.util.Arguments;
import lsafer.util.AbstractStructure;
import lsafer.util.Arrays;

/**
 * a structure to be overridden and used as a part of a {@link Task task}.
 * structure linked with {@link Map} as a secondary container
 * and {@link File JSON file} as a third IO container.
 * depends on {@link File#readJSON(Map)} (Object)} and {@link File#writeJSON(Map)}
 * <p>
 * make sure your {@link TaskPart task-part} matches all {@link JSONFileStructure json-file-structures} rules
 * <p>
 *
 * @author LSaferSE
 * @version 6
 * @since 10 Jun 2019
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class TaskPart extends JSONFileStructure {

    /*
    //your task part should be like :
    //you need to add the modifier "static"
    //to your class if it's an inner-class
    //don't forget to add your class to your lsafer.services.Index class
    @Description("a description for this task part")
    @Permissions({"permission 1", "permission 2"})
    public class MyTaskPart extends TaskPart {

        //this field WELL be stored when method save() get called
        //also it'll be loaded from the storage when method load() get called
        @Description("a description for what this field would be used for")
        @Values({"value1|description", "value2|description", "%|custom values allowed"})
        public String stored_data_1 = "default value";

        //this field will be ignored from Structure
        //interface that this class is inhering (all because it have the character '$' on it's name)
        //so it wouldn't be stored or cloned (even if it's a public field)
        //and no one can get it unless YOU allow that by your code
        private String $cache_data = null;

        //this method will be called when the previous task-part calls lsafer.services.text.Run.get
        @Description("a description for what this method would do when it get called")
        @Parameters(input="string", output="boolean")
        public Boolean get(Arguments arguments){
            return arguments.string == stored_data_1 && $cache_data != null;
        }

    }
    */

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
    protected Integer $index;

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
            Arguments args = Arguments.parse(arguments);
            java.lang.reflect.Method method = getClass().getMethod(name, Arguments.class);
            Parameters parameters = method.getAnnotation(Parameters.class);

            if (parameters == null)
                Log.e("Services Task Part", "run: no Parameters annotation on " + name + " method in " + getClass());
            else {
                for (String param : parameters.input())
                    if (!args.containsKey(param))
                        Log.e("Services Task Part", "run: " + param + " parameter is needed to invoke " + name);

                if (expected != null && !Arrays.all(parameters.output(), expected))
                    Log.e("Services Task Part", "run: method " + name + " don't returns all of " + java.util.Arrays.toString(expected), null);
            }

            return Arguments.parse(method.invoke(this, args));
        } catch (Exception e) {
            Log.e("Services Task Part", "run: an error occurred while invoking " + name + " in the task part " + this.getClass() + " index=" + $index + " task=" + $task, e);
        }

        return new Arguments();
    }

    /**
     * initialize this by cloning it to a class
     * with a name of {@link #class_name} and setting
     * the index of the clone with the given index also
     * it's parent task with the given task.
     *
     * @param parent the parent task of this part
     * @param index of this part
     * @return this with the class on {@link #class_name}
     */
    public TaskPart initialize(Task parent, int index){
        TaskPart part = this.clone(Reflect.getClass(class_name, apk_path));
        part.$task = parent;
        part.$index = index;
        return part;
    }

    /**
     * run a method in the next stem of this.
     *
     * @param name      of the method
     * @param expected  arguments to be returned
     * @param arguments to pass
     * @return arguments that have returned from the method
     */
    protected Arguments next(String name, String[] expected, Object... arguments) {
        return $task.run($index + 1, name, expected, arguments);

    }

    /**
     * a structure get data about custom stems.
     *
     * @author LSaferSE
     * @version 2
     * @since 11-Jun-2019
     */
    public static class Properties extends AbstractStructure {

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
         * load data from the given class.
         *
         * @param part to load data of
         */
        public Properties(TaskPart part) {
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

            //declaring fields
            this.fields = new Field[fields.length];
            for (int i = 0; i < fields.length; i++)
                this.fields[i] = new Field(fields[i], part);

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
             * fill this with the given field's properties.
             *
             * @param field  to get data from
             * @param object to get default value from
             */
            public Field(java.lang.reflect.Field field, Object object) {
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
            public static class Value extends AbstractStructure {

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
                public Value(String source) {
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
        public static class Method extends AbstractStructure {

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
            public Method(java.lang.reflect.Method method) {
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
