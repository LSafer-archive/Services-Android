package lsafer.services.io;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

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
final public class Task extends FolderStructure {

    /**
     * tag to print errors at.
     */
    final public String $TAG = "Services Task";

    /**
     * solved stems.
     */
    final public SparseArray<TaskPart> $parts = new SparseArray<>();

    /**
     * the indexing file to run this.
     */
    public Configuration configuration = new Configuration();

    @Override
    public void load() {
        super.load();

        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof TaskPart) {
                TaskPart part = ((TaskPart) value).clone(Reflect.getClass(((TaskPart) value).class_name, ((TaskPart) value).apk_path));
                part.$index = configuration.indexing.indexOf(key);
                part.$task = this;

                if (part.$index != -1){
                    this.$parts.put(part.$index, part);
                }

                this.put(key, part);
            }
        });

        int index = 0;
        for (String name : this.configuration.indexing){
            TaskPart part = this.get(name);
            if (part == null) {
                Log.e($TAG, "load: task part is indexed but not exist part_name=" + name + " part_index=" + index + " task.$remote=" + this.$remote);
            } else {
                part.$task = this;
                part.$index = index;
                this.$parts.put(index, part);
                index++;
            }
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
