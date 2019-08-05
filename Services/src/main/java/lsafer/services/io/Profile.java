package lsafer.services.io;

import android.content.Context;

import lsafer.io.FileStructure;
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
@SuppressWarnings("WeakerAccess")
public class Profile extends FolderStructure {

    /**
     * the public status of this profile.
     */
    public String $status = "init";

    /**
     * whether this profile is initialized or not.
     */
    public boolean $initialized = false;

    @Override
    public Class<? extends FileStructure> file_structure() {
        return JSONFileStructure.class;
    }

    /**
     * initialize tasks of this.
     *
     * @param context of application
     * @param <P> this
     * @return this
     */
    public <P extends Profile> P initialize(Context context) {
        this.replaceAll((Object key, JSONFileStructure value) ->
                value.<Boolean>get("activated") ?
                        value.<Task>clone(AndroidReflect.getClass(context,
                                value.get("class_name", (k) -> Task.class.getName()),
                                value.get("apk_path", (k) -> "")))
                                .initialize(context, this)
                        : value);
        this.$initialized = true;
        return (P) this;
    }

    @Override
    public <S extends Structure> S reset() {
        $initialized = false;
        return super.reset();
    }

    /**
     * call a method in the first {@link Task.Part tast-part}
     * foreach {@link Task task} in this.
     *
     * @param name      of the method
     * @param arguments to pass
     */
    public void run(String name, Object... arguments) {
        this.$status = name;
        this.forEach((Object key, Object value) -> {
            if (value instanceof Task)
                ((Task) value).invoke(0, name, null, arguments);
        });
    }

}
