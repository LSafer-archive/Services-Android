package lsafer.services.io;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lsafer.io.FolderStructure;
import lsafer.io.JSONFileStructure;
import lsafer.services.lang.Reflect;
import lsafer.services.util.Arguments;

/**
 * a stem task manager.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class Task extends FolderStructure {

    /**
     * tag to print errors at.
     */
    final public String $TAG = "Services Task";

    /**
     * solved stems.
     */
    final public List<TaskPart> $parts = new ArrayList<>();

    /**
     * the indexing file to run this.
     */
    public Configuration configuration = new Configuration();

    @Override
    public void load() {
        super.load();
        int index = 0;
        for (String name : this.configuration.indexing)
            if (this.typeOf(name) == TaskPart.class){
                TaskPart part = this.get(name);
                part = part.clone(Reflect.getClass(part.class_name, part.apk_path));
                part.$task = this;
                part.$index = index++;
                this.$parts.add(part);
            } else {
                Log.e("TAG", "load: task-part ["+name+"] not found, it's index ["+index+"] well be replaced with the next task-part");
            }
    }

    @Override
    public Class<TaskPart> file_structure() {
        return TaskPart.class;

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
    public Arguments run(int index, String name, String[] expected, Object... arguments) {
        if (index == -1 || index >= $parts.size())
            return new Arguments();
        else
            return this.$parts.get(index).run(name, expected, arguments);
    }

    /**
     * the indexing file to run this.
     */
    final public static class Configuration extends JSONFileStructure {

        /**
         * whether this task is activated or not.
         */
        public Boolean activated = false;

        /**
         * stems list to run accordingly.
         */
        public ArrayList<String> indexing = new ArrayList<>();

        /**
         * name of the task.
         */
        public String name = "task";

    }

}
