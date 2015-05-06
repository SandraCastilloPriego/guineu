# Custom Database Search #

## Description ##

This method assigns identity to peaks according to their m/z and retention time values. The user provides a database of m/z values and retention times in CSV format (see below).

#### Method parameters ####

Database fileName of file that contains information for peak identificationField separatorCharacter(s) used to separate fields in the database fileField orderOrder of items in which they are read from database file. Order may be changed by dragging the items with mouse.Ignore first lineCheck to ignore the first line of database filem/z toleranceMaximum allowed m/z difference to set an identification to a peakRT toleranceMaximum allowed retention time difference to set an identification to a peak

## Database file ##

Database file has to be provided in CSV format (Comma-Separated Values). Such files can be exported from a spreadsheet software such as MS Excel, or edited manually using a text editor. The following examples shows the structure of the database file:

```

ID,m/z,Retention time (min),Identity,Formula
1,175.121,24.5,Arginine,C6H14N4O2
2,133.063,11.9,Asparagine,C4H8N2O3
3,134.047,11.7,Aspartate,C4H7NO4

```