package lsafer.services.io;

import android.content.Context;

import java.util.Map;

import lsafer.io.FolderStructure;
import lsafer.io.JSONFileStructure;
import lsafer.lang.AndroidReflect;
import lsafer.util.Structure;

/**
 * a tasks folder structure.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
public class Profile extends FolderStructure {

    /**
     * call a method in the first {@link Task.Part tast-part}
     * foreach {@link Task task} in this.
     *
     * @param name      of the method
     * @param arguments to pass
     */
    final public void run(String name, Object... arguments) {
        this.map().forEach((key, value) -> {
            if (value instanceof Task)
                ((Task) value).invoke(0, name, null, arguments);
        });
    }

    /**
     * initialize tasks of this.
     *
     * @param context of application
     */
    public void initialize(Context context) {
        this.map().forEach((key, value) -> {
            if (value instanceof JSONFileStructure) {
                Map<Object, Object> task_map = ((JSONFileStructure) value).map();

                if ((boolean) task_map.get("activated")) {
                    Class<? extends Task> task_class = AndroidReflect.getClass(context, (String) task_map.get("class_name"), (String) task_map.get("apk_path"));
                    Task task = Structure.newInstance(task_class);
                    task.initialize(context, this);
                    this.put(key, task);
                }
            }
        });
    }

}
