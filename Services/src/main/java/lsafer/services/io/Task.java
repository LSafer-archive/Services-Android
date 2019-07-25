package lsafer.services.io;

import java.util.ArrayList;
import java.util.List;

import lsafer.io.FolderStructure;
import lsafer.io.JSONFileStructure;
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
     * solved stems.
     */
    final public List<TaskPart> $parts = new ArrayList<>();
    /**
     * the indexing file to run this.
     */
    public Configuration configuration = new Configuration();

    /**
     * init this.
     *
     * @param arguments to init with
     */
    public Task(Object... arguments) {
        super(arguments);
    }

    @Override
    public void load() {
        super.load();
        int index = 0;
        for (String name : this.configuration.indexing)
            if (this.typeOf(name) == TaskPart.class)
                this.$parts.add((TaskPart) this.put(name, this.<TaskPart>get(name).initialize(this, index)));
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
    public static class Configuration extends JSONFileStructure {

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

        /**
         * init this.
         *
         * @param arguments to init with
         */
        public Configuration(Object... arguments) {
            super(arguments);
        }
    }

}
