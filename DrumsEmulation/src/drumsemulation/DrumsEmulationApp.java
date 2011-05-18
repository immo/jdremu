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

import drumsemulation.abstraction.abstractData;
import drumsemulation.abstraction.instrumentMode;
import drumsemulation.abstraction.joist;
import drumsemulation.abstraction.noInstrumentMode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import drumsemulation.snd.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * The main class of the application.
 */
public class DrumsEmulationApp extends SingleFrameApplication {

    private playbackDriver playback_driver;
    private ArrayList<String> hitGeneratorSetup;
    private ArrayList<String> names;
    private ArrayList<hitGenerator> generators;
    private ArrayList<String> instrumentModeSetup;
    private ArrayList<String> modenames;
    private ArrayList<instrumentMode> instrumentmodes;
    private abstractData data;
    private instrumentMode pauseMode;
    float p1, p2, lvl;

    public abstractData getData() {
        return data;
    }

    public Set<String> allKnownModes() {
        return new TreeSet<String>(modenames);
    }

    static public String getStringFromConfFile(String fname) {
        String setupFile = System.getProperty("user.home") + "/." + fname;
        File f = new File(setupFile);
        try {
            FileReader fr = new FileReader(f);
            StringBuffer buf = new StringBuffer((int) f.length());
            int num_read = 0;
            char[] cb = new char[1024];
            while ((num_read = fr.read(cb)) != -1) {
                buf.append(String.valueOf(cb, 0, num_read));
            }
            fr.close();
            return buf.toString();
        } catch (IOException ex) {
            return "";
        }

    }

    static public void setConfFile(String fname, String contents) {
        String setupFile = System.getProperty("user.home") + "/." + fname;
        File f = new File(setupFile);

        BufferedWriter output=null;
        
        try {
            output = new BufferedWriter(new FileWriter(f));
            output.write(contents);
        } catch (IOException ex) {
            Logger.getLogger(DrumsEmulationApp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(DrumsEmulationApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    public void initializeMe() {
        data = new abstractData();
        p1 = 0.5f;
        p2 = 0.5f;
        lvl = 1.f;
        playback_driver = new playbackDriver();
        playback_driver.data = data;
        playback_driver.frame_rate = getSampleRate();
        hitGeneratorSetup = new ArrayList<String>();
        names = new ArrayList<String>();
        generators = new ArrayList<hitGenerator>();
        instrumentModeSetup = new ArrayList<String>();
        modenames = new ArrayList<String>();
        instrumentmodes = new ArrayList<instrumentMode>();
        pauseMode = new noInstrumentMode();

        String setupFile = System.getProperty("user.home") + "/.jdremu-generators.conf";
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
            hitGeneratorSetup.add("Snare=Snare(g=2.4 2.4,f=160,click=0.0,db=1/3,dp=2/3,d=180)");
            hitGeneratorSetup.add("DeadSnare=Snare(g=2.4 2.4,f=160,click=0.0,db=1/3,dp=2/3,snare=0.1,d=180)");
            hitGeneratorSetup.add("Kick=Snare(g=0.6 0.6,d=400,f=31,fp=43,fpo=12,fq=38,fqo=22,snare=0.05,click=1.9,fc=7900,wave=cotri,dp=1/3,db=1/3,dq=1/2)");
            hitGeneratorSetup.add("Floor=Snare(g=1 1,d=200,f=80,fp=83,fpo=12,fq=123,fqo=14,snare=0.05,click=0.05,fc=5000,g=0.7 1.1)");
            hitGeneratorSetup.add("Mid=Snare(g=1 1,d=180,f=90,fp=93,fpo=12,fq=143,fqo=14,snare=0.05,click=0.05,fc=5200,g=1.0 0.8)");
            hitGeneratorSetup.add("High=Snare(g=1 1,d=160,f=100,fp=103,fpo=12,fq=163,fqo=14,snare=0.05,click=0.05,fc=5400,g=1.2 0.7)");
            hitGeneratorSetup.add("HiCrash=Cymbal(no=lowbellhirim,d=300,gf=0.3,f=2000,fb=5000,fh1=14000,fl1=3000,dl2=1/3,gb=0.3,gw=1.2,gz=1.4,gr=0.4,gb=0.4,g=0.35 0.5)");
            hitGeneratorSetup.add("LoCrash=Cymbal(no=lowbellhirim,d=450,gf=0.3,f=1400,fb=4000,fh1=12000,fl1=2600,dl2=1/3,gb=0.3,gw=1.2,gz=1.4,gr=0.4,gb=0.4,g=0.45 0.4)");
            //hitGeneratorSetup.add("Ride=Cymbal(no=uiwhrimlo2,d=650,gf=0.15,f=2400,fb=6300,fh2=12000,fl2=2600,gw=0.7,gz=0.4,gr=0.2,gb=0.05,g=2.7 2.1)");
            hitGeneratorSetup.add("Ride=Cymbal(no=uiwhrimlo2,d=650,gf=0.15,f=2400,fb=3500,fh2=4500,fl2=2600,gw=0.7,gz=0.4,gr=0.2,gb=0.05,dlo=1/3,dhr=1/12,dbe=1/6,dzs=1/8,dbi=1/9,g=4.7 4.1)");

            //hitGeneratorSetup.add("RideBell=Cymbal(no=uiwhlo2zsh,d=650,gf=0.15,f=2400,fb=2000,fh2=12000,fl2=2600,gw=0.7,gz=0.4,gr=0.2,gb=0.3,g=1.0 0.8)");
            hitGeneratorSetup.add("RideBell=Cymbal(no=uiwhlo2zsh,d=650,gf=0.15,dhr=1/3,dri=1/4,dlo=1/4,dbe=1/12,f=1800,fb=2000,fh2=5000,fl2=2600,gw=0.7,gz=0.4,gr=0.2,gb=0.3,g=2.3 2.0)");
            hitGeneratorSetup.add("TestCymbal=Cymbal()");
            hitGeneratorSetup.add("HiTestCymbal=Cymbal(d=300,gf=0.3,f=2000,fb=5000,fh1=14000,fl1=3000,gb=0.3,gw=1.2,gz=1.4,gr=0.4,gb=0.4)");
            hitGeneratorSetup.add("LoTestCymbal=Cymbal(d=450,gf=0.3,f=1400,fb=4000,fh1=12000,fl1=2600,gb=0.3,gw=1.2,gz=1.4,gr=0.4,gb=0.4)");

//            hitGeneratorSetup.add("Beep=Beep()");
//            hitGeneratorSetup.add("TestNet=delayNet()");
//            hitGeneratorSetup.add("TestNoise=asdNoise()");
//            hitGeneratorSetup.add("TestMfNoise=asdMfNoise()");
//            hitGeneratorSetup.add("TestCymbals=Cymbal()");
//            hitGeneratorSetup.add("TestCymbalBell=CymbalBell()");
//            hitGeneratorSetup.add("TestCymbalRim=CymbalRim()");
//
//            hitGeneratorSetup.add("TestSnare=Snare()");
//            hitGeneratorSetup.add("TestSnare2=Snare(reso=cotri)");
//            hitGeneratorSetup.add("TestSnare2=Snare(reso=cosaw)");
//            hitGeneratorSetup.add("TestSnare2=Snare(reso=cosquare)");
//            hitGeneratorSetup.add("TestSine=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosine)");
//            hitGeneratorSetup.add("TestTri=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cotri)");
//            hitGeneratorSetup.add("TestSaw=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosaw)");
//            hitGeneratorSetup.add("TestSquare=swOsc(f=1000,a=2,d=20,g=0.9 0.9,wave=cosquare)");
//
//            hitGeneratorSetup.add("TestTomC=Tom(f=52,fo=13,d=200)");
//            hitGeneratorSetup.add("TestTomD=Tom(f=60,fo=13,d=195)");
//            hitGeneratorSetup.add("TestTomE=Tom(f=69,fo=13,d=190)");
//            hitGeneratorSetup.add("TestTomF=Tom(f=72,fo=13,d=185)");
//            hitGeneratorSetup.add("TestTomG=Tom(f=85,fo=13,d=180)");
//            hitGeneratorSetup.add("TestTomA=Tom(f=97,fo=13,d=175)");
//            hitGeneratorSetup.add("TestTomB=Tom(f=110,fo=13,d=170)");
//            hitGeneratorSetup.add("TestTomC'=Tom(f=117,fo=13,d=165)");
        }

        setupFile = System.getProperty("user.home") + "/.jdremu-modes.conf";
        f = new File(setupFile);
        default_setup = true;
        if (f.exists()) {
            FileReader fr;
            try {
                fr = new FileReader(f);
                Scanner lines = new Scanner(fr);

                while (lines.hasNextLine()) {
                    instrumentModeSetup.add(lines.nextLine());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DrumsEmulationApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (default_setup) {
            instrumentModeSetup.add("default=");
            instrumentModeSetup.add("kick1=hit=Kick,2sig1=0.01,2sig2=0.01,2sig=0,p1=0.93877554,p2=0.9183673,r=0.05,rd=1/12");
            instrumentModeSetup.add("kick2=hit=Kick,2sig1=0.01,2sig2=0.01,2sig=0,p1=0.93877554,p2=0.24489796,r=0.05,rd=1/12");
            instrumentModeSetup.add("snare=hit=Snare,p1=0.20408164,p2=0.1632653");
            instrumentModeSetup.add("snarerim=hit=Snare,p1=0.9591837,p2=0.9591837");
            instrumentModeSetup.add("snaredead=hit=DeadSnare,p1=0.20408164,p2=0.1632653");
            instrumentModeSetup.add("floortom=hit=Floor,p1=0.9591837,p2=0.08163265");
            instrumentModeSetup.add("floortomrim=hit=Floor,p1=0.06122449,p2=0.9591837");
            instrumentModeSetup.add("tomtom=hit=Mid,p1=0.9591837,p2=0.08163265");
            instrumentModeSetup.add("tomtomrim=hit=Mid,p1=0.06122449,p2=0.9591837");
            instrumentModeSetup.add("hightom=hit=High,p1=0.9591837,p2=0.24489796");
            instrumentModeSetup.add("hightomrim=hit=High,p1=0.06122449,p2=0.9591837");
            instrumentModeSetup.add("crash=hit=LoCrash,p1=0.5102041,p2=0.9591837,2sig1=0.4,2sig=0.01");
            instrumentModeSetup.add("hicrash=hit=HiCrash,p1=0.5102041,p2=0.9591837,2sig1=0.4,2sig=0.01");
            instrumentModeSetup.add("ride=hit=Ride,2sig=0.03,p1=0.85714287,p2=0.24489796");
            instrumentModeSetup.add("ridebell=hit=RideBell,2sig=0.03,p1=0.8979592,p2=0.26530612");
        }




        Iterator<String> it = hitGeneratorSetup.iterator();

        while (it.hasNext()) {
            String s = it.next();
            if (s.contains("=")) {
                int idx = s.indexOf("=");
                names.add(s.substring(0, idx));
                hitGenerator generator = hitGenerator.getGeneratorByDesc(s.substring(idx + 1));
                generators.add(generator);
                playback_driver.addGenerator(generator);
            }
        }

        it = instrumentModeSetup.iterator();

        while (it.hasNext()) {
            String s = it.next();
            if (s.contains("=")) {
                int idx = s.indexOf("=");
                modenames.add(s.substring(0, idx));
                instrumentMode mode = new instrumentMode(s.substring(idx + 1));
                instrumentmodes.add(mode);
            }
        }


        data.build(getStringFromConfFile("scaffoldings"));
        data.buildVars(getStringFromConfFile("scaffoldings-bindings"));


    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        initializeMe();

        show(new DrumsEmulationView(this));
    }

    public void setRealtimeHack(boolean use) {
        System.out.println("Realtime-hack="+use);
        playback_driver.useRealtimeHack(use);
    }

    public int getGeneratorsCount() {
        return names.size();
    }

    public String getGeneratorName(int index) {
        return names.get(index);
    }

    public hitGenerator getGeneratorByName(String name) {
        for (int i = 0; i < names.size(); ++i) {
            if (names.get(i).equals(name)) {
                return generators.get(i);
            }
        }
        return new beepGenerator();
    }

    public void setGeneratorName(int index, String new_name) {
        names.set(index, new_name);

        findInstrumentModes();
    }

    public hitGenerator getGenerator(int index) {
        return generators.get(index);
    }

    public void setGenerator(int index, hitGenerator new_generator) {
        playback_driver.delGenerator(generators.get(index));
        playback_driver.addGenerator(new_generator);
        generators.set(index, new_generator);

        findInstrumentModes();
    }

    public void addNamedGenerator(String name, String params) {
        hitGenerator g = hitGenerator.getGeneratorByDesc(params);
        names.add(name);
        generators.add(g);
        playback_driver.addGenerator(g);

        findInstrumentModes();
    }

    public void delGenerator(int index) {
        names.remove(index);
        hitGenerator g = generators.get(index);
        playback_driver.delGenerator(g);
        generators.remove(index);

        findInstrumentModes();
    }

    public void findInstrumentModes() {
        for (Iterator<instrumentMode> it = instrumentmodes.iterator(); it.hasNext();) {
            it.next().findInstrument();
        }
    }

    public int getModesCount() {
        return modenames.size();
    }

    public String getModeName(int index) {
        return modenames.get(index);
    }

    public instrumentMode getModeByName(String name) {
        for (int i = 0; i < modenames.size(); ++i) {
            if (modenames.get(i).equals(name)) {
                return instrumentmodes.get(i);
            }
        }
        return pauseMode;
    }

    public void setModeName(int index, String new_name) {
        modenames.set(index, new_name);
    }

    public instrumentMode getMode(int index) {
        return instrumentmodes.get(index);
    }

    public void setMode(int index, instrumentMode new_Mode) {
        instrumentmodes.set(index, new_Mode);
    }

    public void addNamedMode(String name, String params) {
        instrumentMode g = new instrumentMode(params);
        modenames.add(name);
        instrumentmodes.add(g);
    }

    public void delMode(int index) {
        modenames.remove(index);
        instrumentmodes.remove(index);
    }

    public void levelHitGenerator(int index, float new_lvl) {
        lvl = new_lvl;
        generators.get(index).hit2d(playback_driver.get_elapsed(), lvl, p1, p2);
    }

    public void levelHitMode(int index, float new_lvl) {

        instrumentmodes.get(index).hit(playback_driver.get_elapsed(), new_lvl);
    }

    public void p2dHitGenerator(int index, float new_p1, float new_p2) {
        p1 = new_p1;
        p2 = new_p2;
        generators.get(index).hit2d(playback_driver.get_elapsed(), lvl, p1, p2);
    }

    public void setRequestBufferSize(int size)  {
        playback_driver.request_buffer_size = size;
    }

    public boolean setOn_air(boolean on_air) {
        return playback_driver.setOn_air(on_air);
    }
    
    public void set_total_lvl(float dB) {
        playback_driver.set_total_lvl((long) (Math.exp(dB)*(double)(1l << 31)));
    }

    public void setPlayback(boolean on) {
        playback_driver.setPlayback(on);
    }

    public void setPlaybackSpeed(float speed_factor) {
        playback_driver.set_bps(speed_factor*144.f/60.f);
    }

    public void reSetMaster(joist j) {

        data.setMaster(j);
        playback_driver.resetT0();
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

    public void mode_hit_button(int i) {
        instrumentmodes.get(i).hit(playback_driver.get_elapsed(), 1.0f);
    }

    public void mode_hit_button2(int i) {
        instrumentmodes.get(i).hit(playback_driver.get_elapsed(), 0.5f);

    }

    public int getSampleRate() {
        return 44100;
    }

    public void keyDOWN(char key) {

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
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
