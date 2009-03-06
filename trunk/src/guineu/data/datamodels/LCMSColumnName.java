/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guineu.data.datamodels;

/**
 *
 * @author bicha
 */
public enum LCMSColumnName {
    SELECTION ("Selection"),
    ID ("Id"),
    MZ ("m/z"),
    RT ("Retention time"),
    NAME ("Name"),
    ALLNAMES("All names"),
    Class ("Lipid class"),
    NFOUND ("Num found"),
    STANDARD ("Standard"),
    FA ("FA Composition"),
    ALIGNMENT ("Alignment");

    private final String columnName;
    
    LCMSColumnName(String columnName){
        this.columnName = columnName;
    }

    public String getColumnName(){
        return this.columnName;
    }
}
