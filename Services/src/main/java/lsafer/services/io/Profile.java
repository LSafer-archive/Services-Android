package lsafer.services.io;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lsafer.io.FileStructure;
import lsafer.io.FolderStructure;
import lsafer.io.JSONFileStructure;
import lsafer.lang.AndroidReflect;

/**
 * a tasks folder structure.
 *
 * @author LSaferSE
 * @version 1
 * @since 14-Jul-19
 */
public class Profile extends FolderStructure {

    /**
     * all profiles instances map.
     */
    final private static Map<String, Profile> $profiles = new HashMap<>();

    /**
     * get a profile instance.
     *
     * the main purpose of this is to manage
     * instancing profiles.
     *
     * @param key of the profile
     * @param defaultValue case not found
     * @return the linked profile instance of the given key, or the given default value case the profile not found
     */
    public static Profile getInstance(String key, Function<?, Profile> defaultValue){
        Profile profile = Profile.$profiles.get(key);
        if (profile == null){
            profile = defaultValue.apply(null);
            Profile.$profiles.put(key, profile);
        }
        return profile;
    }

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
                        : null);
        return (P) this;
    }

    /**
     * call a method in the first {@link Task.Part tast-part}
     * foreach {@link Task task} in this.
     *
     * @param name      of the method
     * @param arguments to pass
     */
    public void run(String name, Object... arguments) {
        this.forEach((Object key, Task value) -> value.invoke(0, name, null, arguments));
    }

}
