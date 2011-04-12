/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.abstraction;

import drumsemulation.DrumsEmulationApp;
import drumsemulation.snd.hitGenerator;
import java.util.Random;

/**
 *
 * @author immanuel
 */
public class instrumentMode {

    String parameter;
    hitGenerator generator;
    String instrumentname;
    float mu_1, mu_2;
    float sigma_1,sigma_2;
    float alpha_lvl,beta_lvl;
    float sigma_lvl;
    Random rnd;


    static public float clamp(float f) {
        if (f<0.f) {
            return 0.f;
        }
        if (f>1.f) {
            return 1.f;
        }
        return f;
    }

    public instrumentMode(String parameter) {

        this.parameter = parameter;

        this.alpha_lvl = 0.f;
        this.beta_lvl = 1.f;
        this.mu_1 = 0.5f;
        this.sigma_1 = 0.2f;
        this.mu_2 = 0.5f;
        this.sigma_2 = 0.2f;
        this.instrumentname="Snare";
        
        this.sigma_lvl = 0.005f;

        this.rnd = new Random();

        findInstrument();
    }
    
    void findInstrument() {
        generator = DrumsEmulationApp.getApplication().getGeneratorByName(instrumentname);
    }

    public void hit(long when, float level) {
        float p1 = clamp(((float)rnd.nextGaussian())*sigma_1 + mu_1);
        float p2 = clamp(((float)rnd.nextGaussian())*sigma_2 + mu_2);
        float lvl = clamp((float)rnd.nextGaussian()*sigma_lvl + alpha_lvl + beta_lvl*level);
        generator.hit2d(when, lvl, p1, p2);
    }


}
