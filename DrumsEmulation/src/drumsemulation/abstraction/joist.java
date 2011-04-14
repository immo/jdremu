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

    public boolean tick(long when, float t) { ///called every tick during the joist duration, also called with t=0.f after enter().
        return t<_duration;
    }
    
}
