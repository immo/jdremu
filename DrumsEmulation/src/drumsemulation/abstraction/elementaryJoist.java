/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.abstraction;

/**
 *
 * @author immanuel
 */
public class elementaryJoist extends joist {
    
    float level;
    String trigger;
    instrumentMode mode;

    public elementaryJoist(String trigger, float level, float length) {
        _duration = length;
        mode = drumsemulation.DrumsEmulationApp.getApplication().getModeByName(trigger);
        this.level = level;
        this.trigger = trigger;
    }

    @Override
    public String toString() {
        return trigger +"!"+level+"@"+_duration;
    }

}
