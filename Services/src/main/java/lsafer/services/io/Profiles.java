package lsafer.services.io;

import android.content.Context;

import lsafer.io.FolderStructure;
import lsafer.util.Structure;

/**
 * @author LSaferSE
 * @version 1 alpha (05-Aug-19)
 * @since 05-Aug-19
 */
@SuppressWarnings("WeakerAccess")
public class Profiles extends FolderStructure {

    /**
     * whether this profiles object have been initialized or not.
     */
    public boolean $initialized = false;

    @Override
    public Class<? extends FolderStructure> folder_structure() {
        return Profile.class;
    }

    /**
     * public global instance.
     */
    final public static Profiles $ = new Profiles();

    /**
     * invoke {@link Profile#initialize(Context)} on all profiles in this.
     *
     * @param context to initialize with
     */
    public <P extends Profiles> P initialize(Context context){
        this.forEach((Object object, Profile profile) -> {
            if (!profile.$initialized)
                profile.initialize(context);
        });
        this.$initialized = true;
        return (P) this;
    }

    @Override
    public <S extends Structure> S reset() {
        this.$initialized = false;
        return super.reset();
    }

    /**
     * @param name
     * @param arguments
     */
    public void run(String name, Object...arguments){
        this.forEach((Object key, Profile profile) -> profile.run(name, arguments));
    }
}
