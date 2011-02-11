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

/**
 *
 * @author immanuel
 */
public class functionTables {

    private static functionTables theObject;
    public final double d_sample_rate;
    public final int i_sample_rate;
    public final long l_sample_rate;
    public final int sine_freq_hz;
    public final long sine_period_length;
    public final long sine_p1;
    public final long sine_p2;
    public final long sine_p3;
    public final int[] sine_table;
    public final int[] tooth_table;
    public final int tooth_freq_hz;
    public final int tooth_period_length;
    public final int tri_p1;
    public final int tri_p3;

    protected functionTables() {
        d_sample_rate = 44100.;
        i_sample_rate = 44100;
        l_sample_rate = 44100;
        sine_freq_hz = 1;
        tooth_freq_hz = 1;

        int sine_len = i_sample_rate / (4 * sine_freq_hz);
        sine_table = new int[sine_len];

        for (int i = 0; i < sine_len; ++i) {
            double t = (i * Math.PI + 0.5) / (2.0 * sine_len);
            Double sin_t = Math.sin(t) * Integer.MAX_VALUE;
            sine_table[i] = sin_t.intValue();
        }

        int tooth_length = i_sample_rate / (2 * tooth_freq_hz);

        tooth_table = new int[tooth_length];

        for (int i = 0; i < tooth_length; ++i) {
            Double t = ((0.5 + i) / (tooth_length)) * Integer.MAX_VALUE;
            tooth_table[i] = t.intValue();
        }

        tooth_period_length = 2 * tooth_length;
        tri_p1 = tooth_length / 2;
        tri_p3 = (3 * tooth_length) / 2;


        sine_period_length = 4 * sine_len;
        sine_p1 = sine_len;
        sine_p2 = sine_len * 2;
        sine_p3 = sine_len * 3;


    }

    public final int sine(long t, long freq_hz) {
        int period_t;
        if (t >= 0) {
            period_t = (int) (((t * freq_hz) / sine_freq_hz) % sine_period_length);
        } else {
            period_t = (int) (((t * freq_hz) / sine_freq_hz) % sine_period_length);
            if (period_t < 0) {
                period_t += (int) sine_period_length;
            }
        }
        int position = period_t % sine_table.length;
        if (period_t >= sine_p2) {
            if (period_t >= sine_p3) {
                return -sine_table[sine_table.length - 1 - position];
            } else {
                return -sine_table[position];
            }
        } else {
            if (period_t >= sine_p1) {
                return sine_table[sine_table.length - 1 - position];
            } else {
                return sine_table[position];
            }
        }
    }

    public final int cosine(long t, long freq_hz) {
        int period_t;
        if (t >= 0) {
            period_t = (int) (((t * freq_hz) / sine_freq_hz) % sine_period_length);
        } else {
            period_t = (int) (((t * freq_hz) / sine_freq_hz) % sine_period_length);
            if (period_t < 0) {
                period_t += (int) sine_period_length;
            }
        }
        int position = period_t % sine_table.length;
        if (period_t >= sine_p2) {
            if (period_t >= sine_p3) {
                return sine_table[position];
            } else {
                return -sine_table[sine_table.length - 1 - position];
            }
        } else {
            if (period_t >= sine_p1) {
                return -sine_table[position];
            } else {
                return sine_table[sine_table.length - 1 - position];

            }
        }
    }

    public final int saw(long t, long freq_hz) {
        int period_t = (int) (((t * freq_hz) / tooth_freq_hz) % tooth_period_length);
        if (period_t < 0) {
            period_t += (int) l_sample_rate;
        }
        int position = period_t % tooth_table.length;
        if (period_t >= tooth_table.length) {
            return -tooth_table[tooth_table.length - position - 1];
        } else {
            return tooth_table[position];
        }
    }

    public final int tri(long t, long freq_hz) {
        int period_t = (int) (((t * freq_hz) / tooth_freq_hz) % tooth_period_length);
        if (period_t < 0) {
            period_t += (int) l_sample_rate;
        }
        int position = (period_t % tri_p1) << 1;
        if (period_t >= tooth_table.length) {
            if (period_t >= tri_p3) {
                return -tooth_table[tooth_table.length - position - 1];
            } else {
                return -tooth_table[position];
            }
        } else {
            if (period_t >= tri_p1) {
                return tooth_table[tooth_table.length - position - 1];
            } else {
                return tooth_table[position];
            }
        }
    }

    public final int square(long t, long freq_hz) {
        int period_t = (int) (((t * freq_hz * 2) / l_sample_rate));
        if (t >= 0) {
            if ((period_t & 1) == 0) {
                return Integer.MAX_VALUE;
            } else {
                return -Integer.MAX_VALUE;
            }
        } else {
            if ((period_t & 1) == 1) {
                return Integer.MAX_VALUE;
            } else {
                return -Integer.MAX_VALUE;
            }

        }
    }

    static final functionTables getObject() {
        if (theObject == null) {
            theObject = new functionTables();
        }
        return theObject;
    }

    public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
        final functionTables table = functionTables.getObject();

        System.out.println(table.saw(0, 1));

        System.out.println(table.saw(22049, 1));
        System.out.println(table.saw(22050, 1));
        System.out.println(table.saw(44099, 1));

        System.out.println(table.square(0, 1));
        System.out.println(table.square(-1, 1));
        System.out.println(table.square(22049, 1));
        System.out.println(table.square(22050, 1));
    }
}
