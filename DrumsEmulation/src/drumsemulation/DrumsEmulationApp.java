/*
 *  DrumsEmulation - drum emulator & sythesizer
 *  Copyright (C) 2011 C.D.Immanuel Albrecht
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

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

    private playbackDriver playback_driver;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        playback_driver = new playbackDriver();

        show(new DrumsEmulationView(this));
    }

    public boolean setOn_air(boolean on_air) {
        return playback_driver.setOn_air(on_air);
    }

    public void beep(int lvl) {
        playback_driver.beep.hit(playback_driver.get_elapsed(), lvl);
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
