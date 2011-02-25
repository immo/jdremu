/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.snd;

/**
 *
 * @author immanuel
 */
public abstract class soundGenerator {

    public abstract void additiveSynthesis(long start_frame, int[] buffer, int channels, int frames, long lvl31);
    
}
