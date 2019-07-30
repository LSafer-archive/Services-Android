package lsafer.services.io;

import lsafer.io.FolderStructure;
import lsafer.io.IOStructure;

/**
 * a tasks folder structure.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
public class Profile extends FolderStructure {

    /**
     * init this.
     *
     * @param arguments to init with
     */
    public Profile(Object... arguments){
        super(arguments);
    }

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
    public <I extends IOStructure> I load() {
        super.load();
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof Task)
                if (!((Task) value).configuration.activated)
                    this.remove(value);
        });
        return (I) this;
    }

    @Override
    public Class<Task> folder_structure() {
        return Task.class;

    }

}
