# Name Filter #

## Description ##

This module deletes all compounds whose name is on a list of names created by the user.

The list of names must be in a text format file and each name must be in different rows.

Example of the content of the file:
```
Tetracosane
Tetradecane
Tetratriacontane
Triacontane
Tricosane
Tridecane
Tritriacontane
Undecane
*Benzoic acid, TMS
*3-Phenylpropionic acid, TMS
*4-Hydroxybenzoic acid, TMS
*3-Hydroxyphenylacetic acid, TMS
*3-Hydroxybenzoic acid, TMS
*2-Hydroxyphenylpropionic acid, TMS
*3-Hydroxyphenylpropionic acid, TMS
*4-Hydroxyphenylpropionic acid, TMS
*3,4-Dihydroxybenzoic acid, TMS
*3,4-Dihydroxyphenylacetic acid, TMS
```

#### Method parameters ####

_Suffix_
> This text will be added to the name of the filtered version of the data set so that the user will be able to distinguish between both.
_File Name_
> Path of the file which contains the names of the compounds that will be delete.