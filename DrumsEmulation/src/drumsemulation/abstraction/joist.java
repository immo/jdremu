/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.abstraction;

/**
 *
 * @author immanuel
 */
public class joist {

    float _duration;

    public joist() {
        this(1.f);
    }

    public joist(float duration) {
        this._duration = duration;
    }

    public float duration() {
        return _duration;
    }

    public void enter(long when) { ///called upon entering the joist

    }

    public void tick(long when, float t, float next_t) { ///called every tick during the joist duration, may called with t=0.f after enter().
        
    }

    public void prepareLayout() { ///called before the joist is used the first time

    }
    
}
