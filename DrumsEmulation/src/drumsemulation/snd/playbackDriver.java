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

import drumsemulation.abstraction.abstractData;
import java.util.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author immanuel
 */
public class playbackDriver implements LineListener, Runnable {

    boolean on_air;
    boolean realtime_hack; //hack to improve realtime performance with resetT0 (!)
    boolean apply_hack; //hack to be applied(!)
    int line_nbr;
    AudioFormat format;
    Line.Info[] lines;
    SourceDataLine out_line;
    int buffer_frames;
    int channels; // 1 frame consists of _channels_ samples
    byte[] b_buffer;
    int[] i_buffer;
    Thread writing_thread;
    long frames_elapsed;
    final Object generators_lock;
    ArrayList<soundGenerator> generators;
    final functionTables table;
    long total_lvl31;
    Random random_generator;
    public hitGenerator beep;
    public long tick_offset;
    public abstractData data;
    boolean playback;
    long current_t0;
    public float frame_rate;
    public float bps;
    public int request_buffer_size;
    public boolean mono_output;

    public playbackDriver() {
        this.realtime_hack = true;
        this.apply_hack = false;
        this.mono_output = false;

        this.on_air = false;
        this.line_nbr = 0;
        this.channels = 2;
        this.format = new AudioFormat(new Float(drumsemulation.DrumsEmulationApp.getApplication().getSampleRate()), 32, channels, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        lines = AudioSystem.getSourceLineInfo(info);
        this.request_buffer_size = 2 * 1024;

        this.buffer_frames = 256;
        this.b_buffer = new byte[channels * 4 * buffer_frames];
        this.i_buffer = new int[channels * buffer_frames];
        this.frames_elapsed = 0;
        this.table = functionTables.getObject();
        this.generators = new ArrayList<soundGenerator>(64);
        this.generators_lock = new Object();
        this.random_generator = new Random();
        this.beep = new beepGenerator();
        this.total_lvl31 = (1l << 29); /* 1l <<31 equals 0dB */

        this.playback = false;
        this.current_t0 = 0;
        this.data = null;
        this.frame_rate = 44100;
        this.tick_offset = 512;//4410;
        this.bps = 144 / 60.f;

        addGenerator(beep);
    }

    public boolean isPlayback() {
        return this.playback;
    }

    public void setPlayback(boolean on) {

        synchronized (this) {
            this.playback = on;
        }

    }

    public void useRealtimeHack(boolean use) {
        this.realtime_hack = use;
    }

    public void resetT0() {
        if (realtime_hack) {

            synchronized (this) {
                this.current_t0 = this.frames_elapsed
                        - this.tick_offset
                        - this.request_buffer_size; // !!
                this.apply_hack = true;
            }

        } else {

            this.current_t0 = this.frames_elapsed + this.tick_offset;
        }

    }

    public void update(LineEvent le) {
        if (le.getType() == LineEvent.Type.STOP) {
            System.out.println("X-RUN! @" + frames_elapsed);
        }
    }

    public int addGenerator(soundGenerator g) {
        synchronized (generators_lock) {
            if (generators.indexOf(g) == -1) {
                generators.add(g);
            }
            return generators.indexOf(g);
        }
    }

    public boolean delGenerator(soundGenerator g) {
        synchronized (generators_lock) {
            return generators.remove(g);
        }
    }

    public long get_total_lvl() {
        synchronized (this) {
            return total_lvl31;
        }
    }

    public void set_total_lvl(long lvl31) {
        synchronized (this) {
            total_lvl31 = lvl31;
        }
    }

    public long get_elapsed() {
        return frames_elapsed;
    }

    public void set_bps(float new_bps) {

        synchronized (this) {
            long frames_el = this.frames_elapsed;
            float elapsed = frames_el - this.current_t0;
            float beat_rate = frame_rate / this.bps;
            float t = elapsed / beat_rate;

            this.bps = new_bps;
            beat_rate = frame_rate / this.bps;

            this.current_t0 = frames_el - (long) (t * beat_rate);
        }
    }

    public void run() {
        Thread local_writing_thread_copy;
        synchronized (this) {
            local_writing_thread_copy = this.writing_thread;
        }
        int byte_count = (buffer_frames * channels * 4);
        int count = out_line.available() / byte_count;
        int int_count = buffer_frames * channels;
        long local_lvl31 = 0;
        long local_t0 = 0;
        long rel_elapsed = 0;
        long local_tickoffset = 0;
        float local_bps = 1.f;

        boolean local_playback = false;
        boolean local_apply_hack = false;
        boolean local_mono = false;

        for (int idx = 0; idx < int_count; ++idx) {
            i_buffer[idx] = 0;
            int b_idx = idx << 2;
            b_buffer[b_idx] = 0;
            b_buffer[b_idx + 1] = 0;
            b_buffer[b_idx + 2] = 0;
            b_buffer[b_idx + 3] = 0;
        }

        for (int i = 0; i < count; ++i) {

            out_line.write(b_buffer, 0, byte_count);
        }

        out_line.start();
        while (local_writing_thread_copy != null) {
            for (int idx = 0; idx < int_count; ++idx) {
                i_buffer[idx] = 0;
            }

            if (local_playback) {
                rel_elapsed = frames_elapsed - local_t0;
                float beat_rate = frame_rate / local_bps;
                float t = rel_elapsed / beat_rate;
                float next_t = (rel_elapsed + buffer_frames) / beat_rate;
                float prev_t = (rel_elapsed - buffer_frames) / beat_rate;

                if (local_apply_hack) {
                    data.tick(frames_elapsed + local_tickoffset, t, next_t, -1.f);
                } else {
                    data.tick(frames_elapsed + local_tickoffset, t, next_t, prev_t);
                }
            }

            synchronized (generators_lock) {
                Iterator<soundGenerator> it = generators.iterator();
                while (it.hasNext()) {
                    it.next().additiveSynthesis(frames_elapsed, i_buffer,
                            channels, buffer_frames, local_lvl31);
                }
            }

            if (local_mono) {
                for (int idx = 0; idx < int_count; idx+=2) {
                    int b_idx = idx << 2;
                    int f = (i_buffer[idx]>>1)+(i_buffer[idx+1]>>1);
                    b_buffer[b_idx] = (byte) (f >> 24);
                    b_buffer[b_idx + 1] = (byte) (f >> 16);
                    b_buffer[b_idx + 2] = (byte) (f >> 8);
                    b_buffer[b_idx + 3] = (byte) (f);
                    b_buffer[b_idx + 4] = (byte) (f >> 24);
                    b_buffer[b_idx + 5] = (byte) (f >> 16);
                    b_buffer[b_idx + 6] = (byte) (f >> 8);
                    b_buffer[b_idx + 7] = (byte) (f);
                }
            } else {
                for (int idx = 0; idx < int_count; ++idx) {
                    int b_idx = idx << 2;
                    int f = i_buffer[idx];
                    b_buffer[b_idx] = (byte) (f >> 24);
                    b_buffer[b_idx + 1] = (byte) (f >> 16);
                    b_buffer[b_idx + 2] = (byte) (f >> 8);
                    b_buffer[b_idx + 3] = (byte) (f);
                }
            }

            frames_elapsed += this.buffer_frames;
            out_line.write(b_buffer, 0, buffer_frames * channels * 4);
            synchronized (this) {
                local_writing_thread_copy = this.writing_thread;
                local_lvl31 = this.total_lvl31;

                if (!local_playback) {
                    current_t0 += buffer_frames;
                }

                local_playback = this.playback;
                local_t0 = this.current_t0;
                local_tickoffset = this.tick_offset;
                local_bps = this.bps;
                local_apply_hack = this.apply_hack;
                this.apply_hack = false;
                local_mono = this.mono_output;
            }
        }

        synchronized (this) {
            out_line.close();
            out_line.removeLineListener(this);
            out_line = null;
        }
    }

    public void setMono(boolean m) {
        synchronized (this) {
            this.mono_output = m;
        }
    }

    public boolean isOn_air() {
        return on_air;
    }

    public boolean setOn_air(boolean on_air) {
        if (on_air != this.on_air) {

            if (on_air) { //start
                try {
                    //starts
                    synchronized (this) {
                        this.out_line = (SourceDataLine) AudioSystem.getLine(lines[line_nbr]);

                        out_line.addLineListener(this);
                        out_line.open(format, request_buffer_size * 8);
                        System.out.println("Outline buffer size=" + out_line.getBufferSize());
                        System.out.println("In samples=" + out_line.getBufferSize() / 8);

                        writing_thread = new Thread(this);
                        writing_thread.setName("WritingThread");
                        writing_thread.start();
                        this.on_air = true;
                        System.out.println("Went On-Air @" + frames_elapsed);
                    }

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(playbackDriver.class.getName()).log(Level.SEVERE, null, ex);
                    synchronized (this) {
                        this.on_air = false;
                    }
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    System.out.println("No lines found!");
                    System.out.println(lines);
                }
            } else { //stop
                synchronized (this) {
                    writing_thread = null;
                    this.on_air = false;
                    System.out.println("Went Off-Air @" + frames_elapsed);
                }
            }
        }
        return this.on_air;
    }
}
