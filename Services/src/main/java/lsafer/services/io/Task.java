package lsafer.services.io;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lsafer.io.File;
import lsafer.io.JSONFileStructure;
import lsafer.lang.AndroidReflect;
import lsafer.services.annotation.Description;
import lsafer.services.annotation.Parameters;
import lsafer.services.annotation.Permissions;
import lsafer.services.annotation.Values;
import lsafer.services.util.Arguments;
import lsafer.services.util.Properties;
import lsafer.util.AbstractStructure;
import lsafer.util.Arrays;
import lsafer.util.Structure;

/**
 * a stem task manager.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "FieldCanBeLocal"})
@Description("default task")
@Permissions({""})
public class Task extends JSONFileStructure {

    /**
     * whether this task is activated or not.
     */
    @Description("whether this task is activated or not")
    @Values({"true|task activated", "false|task deactivated"})
    public Boolean activated = false;

    /**
     * file were the class of the task is stored at.
     * <p>
     * null if it's custom
     */
    @Description("the path were the task-part's class's apk file is stored at")
    @Values({"%file|custom"})
    public String apk_path = "";

    /**
     * task's class name.
     * <p>
     * null if it's custom
     */
    @Description("the name of the task's class")
    @Values({"null|custom", "%class%task|pre-made"})
    public String class_name = Task.class.getName();

    /**
     * stems list to run accordingly.
     */
    @Description("the parts of this task")
    @Values({"%list%task-part"})
    public List<Part> parts = new ArrayList<>();

    /**
     * parent profile of this task.
     */
    protected Profile $profile;

    /**
     * initialize Tasks in this.
     *
     * @param context the context of application
     * @param parent  the profile of this task
     */
    public void initialize(Context context, Profile parent) {
        for (int i = 0; i < this.parts.size(); i++) {
            Map<String, String> part_map = (Map<String, String>) this.parts.get(i);
            Class<? extends Part> part_class = AndroidReflect.getClass(context, part_map.get("class_name"), part_map.get("class_apk"));
            Part part = Structure.newInstance(part_class);
            part.initialize(context, parent, this, i);
            this.parts.set(i, part);
        }
    }

    /**
     * run a part of this task.
     *
     * @param index     of the part
     * @param name      of the method
     * @param expected  arguments to be returned
     * @param arguments to pass to the part
     * @return arguments that have returned from the method
     */
    public Arguments invoke(int index, String name, String[] expected, Object... arguments) {
        return this.parts.get(index).invoke(name, expected, arguments);
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
     * a structure to be overridden and used as a part of a {@link Task task}.
     * structure linked with {@link Map} as a secondary container
     * and {@link File JSON file} as a third IO container.
     * depends on {@link File#readJSON(Map)} (Object)} and {@link File#writeJSON(Map)}
     * <p>
     * make sure your {@link Part task-part} matches all {@link JSONFileStructure json-file-structures} rules
     * <p>
     *
     * @author LSaferSE
     * @version 6
     * @since 10 Jun 2019
     */
    @Description("default task-part")
    @Permissions({""})
    public class Part extends AbstractStructure {
        //
        //    your task part should be like :
        //    you need to add the modifier "static"
        //    to your class if it's an inner-class
        //    don't forget to add your class to your lsafer.services.Index class
        //
        //    @Description("a description for this task part")
        //    @Permissions({"permission 1", "permission 2"})
        //    public class MyTaskPart extends TaskPart {
        //
        //        //this field WELL be stored when method save() get called
        //        //also it'll be loaded from the storage when method load() get called
        //        @Description("a description for what this field would be used for")
        //        @Values({"value1|description", "value2|description", "%|custom values allowed"})
        //        public String stored_data_1 = "default value";
        //
        //        //this field will be ignored from Structure
        //        //interface that this class is inhering (all because it have the character '$' on it's name)
        //        //so it wouldn't be stored or cloned (even if it's a public field)
        //        //and no one can get it unless YOU allow that by your code
        //        private String $cache_data = null;
        //
        //        //this method will be called when the previous task-part calls lsafer.services.text.Run.get
        //        @Description("a description for what this method would do when it get called")
        //        @Parameters(input="string", output="boolean")
        //        public Boolean get(Arguments arguments){
        //            return arguments.string == stored_data_1 && $cache_data != null;
        //        }
        //
        //    }

        /**
         * the path of the apk that contains this class.
         */
        @Description("the path were the task-part's class's apk file is stored at")
        @Values({"%file|custom"})
        public String apk_path = "";

        /**
         * the name of the class of this to be generated.
         */
        @Description("the name of the task-part's class")
        @Values({"%class%task-part|custom"})
        public String class_name = Part.class.getName();

        /**
         * this stem's index.
         */
        protected Integer $index;

        /**
         * the profile of the task of this task-part.
         */
        protected Profile $profile;

        /**
         * collection that have started this.
         */
        protected Task $task;

        /**
         * initialize this by cloning it to a class
         * with a name of {@link #class_name} and setting
         * the index of the clone with the given index also
         * it's parent task with the given task.
         *
         * @param context to be able to invoke {@link AndroidReflect#getClass(Context, String, String)}
         * @param profile the profile of the parent task of this
         * @param task    the parent task of this part
         * @param index   of this part
         */
        public void initialize(Context context, Profile profile, Task task, int index) {
            this.$profile = profile;
            this.$task = task;
            this.$index = index;
        }

        /**
         * run a method inside this.
         *
         * @param name      of method
         * @param expected  arguments to be returned | use it link "value, text, context"
         * @param arguments to pass
         * @return arguments from the method
         */
        public Arguments invoke(String name, String[] expected, Object... arguments) {
            try {
                Arguments args = new Arguments(arguments);
                java.lang.reflect.Method method = this.getClass().getMethod(name, Arguments.class);
                Parameters parameters = method.getAnnotation(Parameters.class);

                if (parameters == null)
                    Log.e("Services Task Part", "invoke: no Parameters annotation on " + name + " method in " + getClass());
                else {
                    for (String param : parameters.input())
                        if (!args.containsKey(param))
                            Log.e("Services Task Part", "invoke: " + param + " parameter is needed to invoke " + name);

                    if (expected != null && !Arrays.all(parameters.output(), expected))
                        Log.e("Services Task Part", "invoke: method " + name + " don't returns all of " + java.util.Arrays.toString(expected), null);
                }

                return new Arguments(method.invoke(this, args));
            } catch (Exception e) {
                Log.e("Services Task Part", "invoke: an error occurred while invoking " + name + " in the task part " + this.getClass() + " index=" + $index + " task=" + $task, e);
            }

            return new Arguments();
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
         * run a method in the next stem of this.
         *
         * @param name      of the method
         * @param expected  arguments to be returned
         * @param arguments to pass
         * @return arguments that have returned from the method
         */
        protected Arguments next(String name, String[] expected, Object... arguments) {
            return $task.invoke($index + 1, name, expected, arguments);

        }

    }

}
