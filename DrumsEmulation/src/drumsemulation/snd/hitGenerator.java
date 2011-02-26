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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.snd;

import java.util.*;

/**
 *
 * @author immanuel
 */
public abstract class hitGenerator extends soundGenerator {

    final Object sync_token;
    String description;
    static functionTables ft = functionTables.getObject();

    public hitGenerator() {
        this.sync_token = new Object();
        
        this.description = "!!!";
        
    }

    public hitGenerator(String description) {
        this();
        this.description = "!!!("+description+")";
    }


    public abstract void hit(long when, float level);

    public void hit1d(long when, float level, float p1) {
        hit(when,level);
    }

    public void hit2d(long when, float level, float p1, float p2) {
        hit(when,level);
    }

    

    public static hitGenerator getGeneratorByDesc(String desc) {
        hitGenerator error = new beepGenerator();
        error.description = desc + "!";
        desc = desc.trim();


        if (desc.contains("(")) {
            int idx = desc.indexOf("(");
            String type = desc.substring(0,idx).trim();
            String parms = desc.substring(idx+1);
            if (parms.substring(parms.length()-1).equals(")")) {
                parms = parms.substring(0,parms.length()-1).trim();
                
                if (type.equals("Beep")) {
                    return new beepGenerator(parms);
                } else if (type.equals("swOsc")) {
                    return new swingOscillator(parms);
                } else if (type.equals("Tom")) {
                    return new tomGenerator(parms);
                } else if (type.equals("Snare")) {
                    return new snareGenerator(parms);
                }

            }
        }


        return error;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }



    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        hitGenerator h = new beepGenerator();

        float flt = 0.9f;
        System.out.println("(long)0.9*(1l<<31)="+((long)(0.9*(1l<<31))));

        h.hit(100, 1);
        h.hit(30, 1);
        h.hit(1030, 1);
        h.hit(677, 1);
        h.additiveSynthesis(300, null, 0, 100, 1l << 31);
        
    }
}
