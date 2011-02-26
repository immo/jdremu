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
public class snareGenerator extends hitGenerator {

    swingOscillator b1, b2;
    swingOscillator p1, p2, p3;
    swingOscillator q1, q2, q3;
    swingOscillator click;
    int p_base, q_base, p_shift, q_shift;
    int db2, dp2, dp3, dq2, dq3;

    public snareGenerator(String parms) {
        super();
        this.description = "Snare(" + parms + ")";

        drumsemulation.DrumsEmulationApp app = drumsemulation.DrumsEmulationApp.getApplication();
        int rate = app.getSampleRate();

        p_base = 111;
        q_base = 111;
        p_shift = 175;
        q_shift = 224;
        db2 = (rate * 5 * 31) / (180 * 1000);
        dp2 = (rate * 5 * 31) / ((p_base + p_shift) * 1000);
        dq2 = (rate * 5 * 31) / ((q_base + q_shift) * 1000);
        dp3 = (rate * 7 * 31) / ((p_base + p_shift) * 1000);
        dq3 = (rate * 7 * 31) / ((q_base + q_shift) * 1000);

        b1 = new swingOscillator("f=180,a=2,d=240,g=0.3 0.3");
        b2 = new swingOscillator("f=330,a=2,d=120,g=0.3 0.3");
        p1 = new swingOscillator("f=" + (p_base + p_shift) + ",a=2,d=80,g=0.15 0.15");
        p2 = new swingOscillator("f=" + (2 * p_base + p_shift) + ",a=2,d=60,g=0.15 0.15");
        p3 = new swingOscillator("f=" + (3 * p_base + p_shift) + ",a=2,d=40,g=0.15 0.15");
        q1 = new swingOscillator("f=" + (q_base + q_shift) + ",a=2,d=75,g=0.15 0.15");
        q2 = new swingOscillator("f=" + (2 * q_base + q_shift) + ",a=2,d=55,g=0.15 0.15");
        q3 = new swingOscillator("f=" + (3 * q_base + q_shift) + ",a=2,d=35,g=0.15 0.15");
        click = new swingOscillator("f=5200,a=2,ar=0.5,d=1,g=0.4 0.4");
    }

    public snareGenerator() {
        this("");

    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
            b1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            b2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            p3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q1.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q2.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            q3.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
            click.additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        }
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            b1.hit(when, level);
            b2.hit(when + db2, level);
            p1.hit(when, level);
            p2.hit(when + dp2, level);
            p3.hit(when + dp2 + dp3, level);
            q1.hit(when, level);
            q2.hit(when + dq2, level);
            q3.hit(when + dq2 + dq3, level);
            click.hit(when, level);
        }
    }
}
