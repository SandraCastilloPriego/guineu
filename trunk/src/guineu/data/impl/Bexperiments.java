/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data.impl;

/**
 *
 * @author scsandra
 */
public class Bexperiments {

    public String Name,  TYPE,  PROJECT,  REPLICATE,  PERSON,  Amount,  Unit,  Method,  Sample,  EDATE;
    public boolean state = false;

    public Bexperiments(String[] data) {
        this.Name = data[0];
        this.TYPE = data[1].toUpperCase();
        this.PROJECT = data[2].toUpperCase();
        this.REPLICATE = data[3].toUpperCase();
        this.PERSON = data[4].toUpperCase();
        this.Amount = data[5];
        this.Unit = data[6];

        String[] moreData = this.Name.split("_");
        if (moreData.length == 5) {
            this.Method = moreData[0].toUpperCase();
            this.Sample = moreData[1].toUpperCase();
            this.EDATE = moreData[3];
            state = true;
        }
    }
}
