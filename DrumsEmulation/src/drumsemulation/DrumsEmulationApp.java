/*
 * DrumsEmulationApp.java
 */

package drumsemulation;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import drumsemulation.snd.*;

/**
 * The main class of the application.
 */
public class DrumsEmulationApp extends SingleFrameApplication {

    private PlaybackDriver playback_driver;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        playback_driver = new PlaybackDriver();

        show(new DrumsEmulationView(this));
    }

    public boolean setOn_air(boolean on_air) {
        return playback_driver.setOn_air(on_air);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DrumsEmulationApp
     */
    public static DrumsEmulationApp getApplication() {
        return Application.getInstance(DrumsEmulationApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(DrumsEmulationApp.class, args);
    }
}
