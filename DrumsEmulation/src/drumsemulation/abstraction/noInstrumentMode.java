/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.abstraction;

/**
 *
 * @author immanuel
 */
public class noInstrumentMode extends instrumentMode {

    public noInstrumentMode() {
        super("");
    }

    @Override
    public void findInstrument() {

    }

    @Override
    public void hit(long when, float level) {

    }
    
}
