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

import java.util.*;
import java.util.logging.*;
import javax.sound.sampled.*;

/**
 *
 * @author immanuel
 */
public class playbackDriver implements LineListener, Runnable {

    boolean on_air;
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

    public playbackDriver() {
        this.on_air = false;
        this.line_nbr = 0;
        this.channels = 2;
        this.format = new AudioFormat(44100.f, 32, channels, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        lines = AudioSystem.getSourceLineInfo(info);
        this.buffer_frames = 256;
        this.b_buffer = new byte[channels * 4 * buffer_frames];
        this.i_buffer = new int[channels * buffer_frames];
        this.frames_elapsed = 0;
        this.table = functionTables.getObject();
        this.generators = new ArrayList<soundGenerator>(64);
        this.generators_lock = new Object();
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

    public void run() {
        Thread local_writing_thread_copy;
        synchronized (this) {
            local_writing_thread_copy = this.writing_thread;
        }
        int byte_count = (buffer_frames * channels * 4);
        int count = out_line.available() / byte_count;
        int int_count = buffer_frames * channels;

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
            synchronized (generators_lock) {

                Iterator<soundGenerator> it = generators.iterator();
                while (it.hasNext()) {
                    it.next().additiveSynthesis(frames_elapsed, i_buffer,
                            channels, buffer_frames);
                }
            }
            for (int idx = 0; idx < int_count; ++idx) {
                i_buffer[idx] += table.square(frames_elapsed + (idx >> 1), 440) >> 7;
            }
            for (int idx = 0; idx < int_count; ++idx) {
                int b_idx = idx << 2;
                int f = i_buffer[idx];
                b_buffer[b_idx] = (byte) (f >> 24);
                b_buffer[b_idx + 1] = (byte) (f >> 16);
                b_buffer[b_idx + 2] = (byte) (f >> 8);
                b_buffer[b_idx + 3] = (byte) (f);
            }

            frames_elapsed += this.buffer_frames;
            out_line.write(b_buffer, 0, buffer_frames * channels * 4);
            synchronized (this) {
                local_writing_thread_copy = this.writing_thread;
            }
        }

        synchronized (this) {
            out_line.close();
            out_line.removeLineListener(this);
            out_line = null;
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
                        out_line.open(format);
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
