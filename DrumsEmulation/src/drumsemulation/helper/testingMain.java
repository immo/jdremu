/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.helper;

import java.io.*;

/**
 *
 * @author immanuel
 */
public class testingMain {
     public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
            int testing = (123<<8)+22;
            byte test2 = (byte) testing;
            byte test1 = (byte) (testing>>8);
            System.out.println("Int: "+testing);
            System.out.println("Byte: "+test2);
            System.out.println("HiByte: "+test1);
      }
}
