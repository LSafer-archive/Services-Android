package lsafer.services.launcher;

import android.os.Looper;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import lsafer.services.Services;
import lsafer.services.io.Task;
import lsafer.services.io.TaskPart;

/**
 * custom qs tiles.
 *
 * @author LSaferSE
 * @version 1
 * @since 16-Jun-19
 */
public abstract class Tiles extends TileService {

    /**
     * tiles tasks name.
     */
    final public static String Directory = "Tiles";

    /**
     * agent to use on bind.
     */
    public Task task = null;

    @Override
    public void onClick() {
        super.onClick();
        new Thread(() -> {
            Looper.prepare();
            Services.initialize(this);

            if (task == null)
                task = Task.load(Task.class, Services.Storage.child(Directory + "/" + this.position()));

            Tile tile = getQsTile();
            if (task.configuration.activated) {
                boolean state = tile.getState() != Tile.STATE_ACTIVE;

                task.run(0, state ? TaskPart.$START : TaskPart.$STOP, null, this, tile);
                tile.setState(state ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
                tile.setLabel(task.configuration.name + (state ? " (on)" : " (off)"));
                tile.updateTile();
            } else {
                tile.setState(Tile.STATE_UNAVAILABLE);
            }
        }).start();
    }

    /**
     * get a name of the copy of this.
     *
     * @return the name to use.
     */
    abstract String position();

    /**
     * custom tile 1.
     */
    final public static class Tile1 extends Tiles {

        @Override
        String position() {
            return "1";
        }
    }

    /**
     * custom tile 10.
     */
    final public static class Tile10 extends Tiles {

        @Override
        String position() {
            return "10";
        }

    }

    /**
     * custom tile 2.
     */
    final public static class Tile2 extends Tiles {

        @Override
        String position() {
            return "2";
        }

    }

    /**
     * custom tile 3.
     */
    final public static class Tile3 extends Tiles {

        @Override
        String position() {
            return "3";
        }

    }

    /**
     * custom tile 4.
     */
    final public static class Tile4 extends Tiles {

        @Override
        String position() {
            return "4";
        }

    }

    /**
     * custom tile 5.
     */
    final public static class Tile5 extends Tiles {

        @Override
        String position() {
            return "5";
        }

    }

    /**
     * custom tile 6.
     */
    final public static class Tile6 extends Tiles {

        @Override
        String position() {
            return "6";
        }

    }

    /**
     * custom tile 7.
     */
    final public static class Tile7 extends Tiles {

        @Override
        String position() {
            return "7";
        }

    }

    /**
     * custom tile 8.
     */
    final public static class Tile8 extends Tiles {

        @Override
        String position() {
            return "8";
        }

    }

    /**
     * custom tile 9.
     */
    final public static class Tile9 extends Tiles {

        @Override
        String position() {
            return "9";
        }

    }

}
