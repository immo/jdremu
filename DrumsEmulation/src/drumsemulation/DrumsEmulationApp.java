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

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import drumsemulation.snd.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * The main class of the application.
 */
public class DrumsEmulationApp extends SingleFrameApplication {

    private playbackDriver playback_driver;
    private ArrayList<String> hitGeneratorSetup;
    private ArrayList<String> names;
    private ArrayList<hitGenerator> generators;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        playback_driver = new playbackDriver();
        hitGeneratorSetup = new ArrayList<String>();
        names = new ArrayList<String>();
        generators = new ArrayList<hitGenerator>();

        String setupFile = System.getProperty("user.home") + "/.jdremu.conf";
        File f = new File(setupFile);
        boolean default_setup = true;
        if (f.exists()) {
            FileReader fr;
            try {
                fr = new FileReader(f);
                Scanner lines = new Scanner(fr);

                while (lines.hasNextLine()) {
                    hitGeneratorSetup.add(lines.nextLine());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DrumsEmulationApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (default_setup) {
            hitGeneratorSetup.add("Beep=Beep()");
            hitGeneratorSetup.add("Test1=swOsc()");
        }

        Iterator<String> it = hitGeneratorSetup.iterator();

        while (it.hasNext()) {
            String s = it.next();
            if (s.contains("=")) {
                int idx = s.indexOf("=");
                names.add(s.substring(0, idx));
                hitGenerator generator = hitGenerator.getGeneratorByDesc(s.substring(idx+1));
                generators.add(generator);
                playback_driver.addGenerator(generator);
            }
        }



        show(new DrumsEmulationView(this));
    }

    public int getGeneratorsCount() {
        return names.size();
    }

    public String getGeneratorName(int index) {
        return names.get(index);
    }

    public hitGenerator getGenerator(int index) {
        return generators.get(index);
    }

    public boolean setOn_air(boolean on_air) {
        return playback_driver.setOn_air(on_air);
    }

    public void beep(int lvl) {
        playback_driver.beep.hit(playback_driver.get_elapsed(), lvl);
    }

    public void instrument_hit_button(int i) {
        generators.get(i).hit(playback_driver.get_elapsed(), 127);
    }

    public int getSampleRate() {
        return 44100;
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
