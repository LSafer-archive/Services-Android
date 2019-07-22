package lsafer.services.io;

import lsafer.io.FolderStructure;

/**
 * a tasks folder structure.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
final public class Tasks extends FolderStructure {

    /**
     * call a method in the first {@link TaskPart tast-part}
     * foreach {@link Task task} in this.
     *
     * @param name      of the method
     * @param arguments to pass
     */
    final public void run(String name, Object... arguments) {
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof Task)
                ((Task) value).run(0, name, null, arguments);
        });
    }

    @Override
    public void load() {
        super.load();
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof Task)
                if (!((Task) value).configuration.activated)
                    this.remove(value);
        });
    }

    @Override
    public Class<Task> folder_structure() {
        return Task.class;

    }

}
