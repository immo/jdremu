/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drumsemulation.snd;

/**
 *
 * @author immanuel
 */
public class functionTables {
    private static functionTables theObject;
    public final int data;
    protected functionTables() {
        data = 12;
    }
    static final functionTables getObject(){
        if (theObject == null) {
            theObject = new functionTables();
        }
        return theObject;
    }



     public static void main(String args[])
            throws java.io.IOException, java.io.FileNotFoundException {
            final functionTables table = functionTables.getObject();
            
            System.out.println(table.data);
      }
}
