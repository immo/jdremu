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
