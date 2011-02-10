/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.snd;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

/**
 *
 * @author immanuel
 */
public class PlaybackDriver implements LineListener {

    boolean on_air;
    int line_nbr;
    AudioFormat format;
    Line.Info[] lines;
    SourceDataLine out_line;
    int buffer_samples;
    int channels;
    byte[] buffer;

    public PlaybackDriver() {
        this.on_air = false;
        this.line_nbr = 0;
        this.channels = 2;
        this.format = new AudioFormat(44100.f, 32, channels, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        lines = AudioSystem.getSourceLineInfo(info);
        this.buffer_samples = 256;
        this.buffer = new byte[channels*4*buffer_samples];
    }

    public void update(LineEvent le) {
        System.out.println(le+" "+out_line.available());
    }



    public boolean isOn_air() {
        return on_air;
    }

    public boolean setOn_air(boolean on_air) {
        if (on_air != this.on_air) {
            this.on_air = on_air;
            if (on_air) { //start
                try {
                    //start
                    this.out_line = (SourceDataLine) AudioSystem.getLine(lines[line_nbr]);
                    out_line.addLineListener(this);
                    out_line.open(format);
                    System.out.println("Available: "+out_line.available());
                    for (int i=0;i<20;++i)
                        out_line.write(buffer, 0, channels*4*buffer_samples);
                    
                    
                    out_line.start();
                    
                    
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(PlaybackDriver.class.getName()).log(Level.SEVERE, null, ex);
                    this.on_air = false;
                }
            } else { //stop
                out_line.close();
            }
        }
        return this.on_air;
    }



}
