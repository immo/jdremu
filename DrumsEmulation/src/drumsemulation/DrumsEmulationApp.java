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

    float p1,p2,lvl;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        p1 = 0.5f;
        p2 = 0.5f;
        lvl = 1.f;
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
            hitGeneratorSetup.add("TestNoise=asdNoise()");
            hitGeneratorSetup.add("TestMfNoise=asdMfNoise()");
            hitGeneratorSetup.add("TestFmodOsc=fmodOsc(f1=400,f2=100,a=2,d=500,wave=cotri)");
            hitGeneratorSetup.add("TestSnare=Snare()");
            hitGeneratorSetup.add("TestSnare2=Snare(reso=cotri)");
            hitGeneratorSetup.add("TestSnare2=Snare(reso=cosaw)");
            hitGeneratorSetup.add("TestSnare2=Snare(reso=cosquare)");
            hitGeneratorSetup.add("TestSine=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosine)");
            hitGeneratorSetup.add("TestTri=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cotri)");
            hitGeneratorSetup.add("TestSaw=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosaw)");
            hitGeneratorSetup.add("TestSquare=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosquare)");
            
            hitGeneratorSetup.add("TestTomC=Tom(f=52,fo=13,d=200)");
            hitGeneratorSetup.add("TestTomD=Tom(f=60,fo=13,d=195)");
            hitGeneratorSetup.add("TestTomE=Tom(f=69,fo=13,d=190)");
            hitGeneratorSetup.add("TestTomF=Tom(f=72,fo=13,d=185)");
            hitGeneratorSetup.add("TestTomG=Tom(f=85,fo=13,d=180)");
            hitGeneratorSetup.add("TestTomA=Tom(f=97,fo=13,d=175)");
            hitGeneratorSetup.add("TestTomB=Tom(f=110,fo=13,d=170)");
            hitGeneratorSetup.add("TestTomC'=Tom(f=117,fo=13,d=165)");
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

    public void setGeneratorName(int index, String new_name) {
        names.set(index,new_name);
    }

    public hitGenerator getGenerator(int index) {
        return generators.get(index);
    }

     public void setGenerator(int index, hitGenerator new_generator) {
        playback_driver.delGenerator(generators.get(index));
        playback_driver.addGenerator(new_generator);
        generators.set(index,new_generator);
    }

     public void addNamedGenerator(String name, String params) {
         hitGenerator g = hitGenerator.getGeneratorByDesc(params);
         names.add(name);
         generators.add(g);
         playback_driver.addGenerator(g);
     }

     public void delGenerator(int index) {
         names.remove(index);
         hitGenerator g = generators.get(index);
         playback_driver.delGenerator(g);
         generators.remove(index);
     }

     public void levelHitGenerator(int index, float new_lvl) {
         lvl = new_lvl;
         generators.get(index).hit2d(playback_driver.get_elapsed(), lvl, p1, p2);
     }
     public void p2dHitGenerator(int index, float new_p1, float new_p2) {
         p1 = new_p1;
         p2 = new_p2;
         generators.get(index).hit2d(playback_driver.get_elapsed(), lvl, p1, p2);
     }

    public boolean setOn_air(boolean on_air) {
        return playback_driver.setOn_air(on_air);
    }

    public void beep(float lvl) {
        playback_driver.beep.hit(playback_driver.get_elapsed(), lvl);
    }

    public void instrument_hit_button(int i) {
        generators.get(i).hit(playback_driver.get_elapsed(), 1.0f);
    }

    public void instrument_hit_button2(int i) {
        generators.get(i).hit(playback_driver.get_elapsed(), 0.5f);
     
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
