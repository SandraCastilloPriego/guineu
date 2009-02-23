/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data;

import guineu.data.impl.DatasetType;

/**
 *
 * @author SCSANDRA
 */
public interface Dataset {

    public String getDatasetName();

    public Iterable<String> getNameExperiments();

    public int getNumberCols();

    public int getNumberRows();
    
    public void setDatasetName(String name);
    
    public DatasetType getType();
	
	public void setType(DatasetType type);
    
    public PeakListRow getRow(int row);
}
