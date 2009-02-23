/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data.parser;

import guineu.data.Dataset;

/**
 *
 * @author scsandra
 */
public interface Parser {

    public String getDatasetName();

    public void fillData();

    public Dataset getDataset();

    public float getProgress();
}
