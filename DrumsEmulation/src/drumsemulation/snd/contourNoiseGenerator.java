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
package drumsemulation.snd;

import java.util.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author immanuel
 */
public class contourNoiseGenerator extends hitGenerator {

    Random rnd;
    long[] hit_times;
    long[] hit_amplitude31;

    public contourNoiseGenerator(String parms) {
        super();

        this.description = "asdNoise("+parms+")";
    }

    public contourNoiseGenerator() {
        this("");
    }



    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        
    }

    @Override
    public void hit(long when, float level) {
        
    }



}
