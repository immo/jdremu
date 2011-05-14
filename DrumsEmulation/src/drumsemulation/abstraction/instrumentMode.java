/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drumsemulation.abstraction;

import drumsemulation.DrumsEmulationApp;
import drumsemulation.snd.hitGenerator;
import java.util.Random;
import java.util.Scanner;
import drumsemulation.helper.*;

/**
 *
 * @author immanuel
 */
public class instrumentMode {

    String parameter;
    hitGenerator generator;
    String instrumentname;
    float mu_1, mu_2;
    float sigma_1, sigma_2;
    float alpha_lvl, beta_lvl;
    float sigma_lvl;
    float sigma_t;
    Random rnd;
    long last_hit;
    float strength_reduction_quotient;
    float reduction_amount;

    static public float clamp(float f) {
        if (f < 0.f) {
            return 0.f;
        }
        if (f > 1.f) {
            return 1.f;
        }
        return f;
    }

    public instrumentMode(String parameter) {
        this.parameter = parameter.trim();

        this.last_hit = 0;
        this.strength_reduction_quotient = DrumsEmulationApp.getApplication().getSampleRate() / 14.f;
        this.reduction_amount = 0.3f;
        this.alpha_lvl = 0.f;
        this.beta_lvl = 1.f;
        this.mu_1 = 0.5f;
        this.sigma_1 = 0.02f;
        this.mu_2 = 0.5f;
        this.sigma_2 = 0.02f;
        this.instrumentname = "Snare";
        this.sigma_t = (0.5f * DrumsEmulationApp.getApplication().getSampleRate()) / 1000.f;

        this.sigma_lvl = 0.005f;

        this.rnd = new Random();

        Scanner scan = new Scanner(parameter);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("hit")) {
                    this.instrumentname = pval;
                } else if (pname.equals("a")) {
                    this.alpha_lvl = poorInputParser.parseFloat(pval);
                } else if (pname.equals("b")) {
                    this.beta_lvl = poorInputParser.parseFloat(pval);
                } else if (pname.equals("p1")) {
                    this.mu_1 = poorInputParser.parseFloat(pval);
                } else if (pname.equals("p2")) {
                    this.mu_2 = poorInputParser.parseFloat(pval);
                } else if (pname.equals("2sig1")) {
                    this.sigma_1 = poorInputParser.parseFloat(pval) / 2.f;
                } else if (pname.equals("2sig2")) {
                    this.sigma_2 = poorInputParser.parseFloat(pval) / 2.f;
                } else if (pname.equals("2sig")) {
                    this.sigma_lvl = poorInputParser.parseFloat(pval) / 2.f;
                } else if (pname.equals("4sig")) {
                    this.sigma_t = (poorInputParser.parseFloat(pval) / 4.f) * DrumsEmulationApp.getApplication().getSampleRate() / 1000.f;
                } else if (pname.equals("rd")) {
                    this.strength_reduction_quotient = (poorInputParser.parseFloat(pval)) * DrumsEmulationApp.getApplication().getSampleRate();
                }  else if (pname.equals("r")) {
                    this.reduction_amount = poorInputParser.parseFloat(pval);
                }
            }
        }


        findInstrument();
    }

    public void findInstrument() {
        generator = DrumsEmulationApp.getApplication().getGeneratorByName(instrumentname);
    }

    public void hit(long when, float level) {
        float p1 = clamp(((float) rnd.nextGaussian()) * sigma_1 + mu_1);
        float p2 = clamp(((float) rnd.nextGaussian()) * sigma_2 + mu_2);
        float lvl = clamp((float) rnd.nextGaussian() * sigma_lvl + alpha_lvl + beta_lvl * level);
        long t_offset = Math.round(rnd.nextGaussian() * sigma_t);
        float reduction = (float) (Math.PI * 0.5
                - Math.atan((when + t_offset - last_hit) / strength_reduction_quotient));
        reduction *= reduction_amount;
        generator.hit2d(when + t_offset, lvl - reduction, p1, p2);
        last_hit = when + t_offset;
    }

    public String getDescription() {
        return parameter;
    }
}
