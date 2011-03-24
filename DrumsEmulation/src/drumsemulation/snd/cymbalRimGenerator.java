/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.snd;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author immanuel
 */
public class cymbalRimGenerator extends hitGenerator  {

    int frequency;
    float decay;
    resoOscillator[] rim_modes;


     public cymbalRimGenerator() {
        this("");
    }

    public cymbalRimGenerator(String parms) {
        super();
        this.description = "CymbalRim(" + parms + ")";
        frequency = 200;
        decay = 200;

        String wave="sine";

        ArrayList<Float> gain_factors = new ArrayList<Float>();
        gain_factors.add(1.f);
        gain_factors.add(1.f);
        Scanner scan = new Scanner(parms);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();
                if (pname.equals("g")) {
                    Scanner gvals = new Scanner(pval);
                    gvals.useDelimiter(" ");
                    gain_factors.clear();
                    while (gvals.hasNext()) {
                        float f = Float.parseFloat(gvals.next().trim());
                        gain_factors.add(f);
                    }
                } else if (pname.equals("f")) {
                    frequency = Integer.parseInt(pval);

                }else if (pname.equals("d")) {
                    decay = Float.parseFloat(pval);

                }else if (pname.equals("wave")) {
                    wave = pval;
                }
            }
        }


        float[] factors ={1.f, 1.3f, 1.8f, 2.f, 2.3f, 3.0f, 3.3f, 4.f, 4.5f, 5.0f, 6.f, 8.2f};

        rim_modes = new resoOscillator[factors.length];
        for (int i=0;i<factors.length;++i) {
            rim_modes[i] = new resoOscillator("a=40,wave="+wave+",f="+((int)(frequency*factors[i]))
                    +",d="+(decay+10.f-(10.f*factors[i]))
                    +",g="+utils.multiplyGainList(gain_factors, 1.f/12.f));
        }



    }

    @Override
    public void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31) {
        synchronized (sync_token) {
        for (int i=0;i<rim_modes.length;++i) {
            rim_modes[i].additiveSynthesis(start_frame, buffer, channels, frames, lvl31);
        }

        }
    }

    @Override
    public void hit(long when, float level) {
        synchronized (sync_token) {
            for (int i=0;i<rim_modes.length;++i) {
                rim_modes[i].hit(when, level);
            }

        }
    
    }



}
