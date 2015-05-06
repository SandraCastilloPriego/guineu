# [PubChem](http://pubchem.ncbi.nlm.nih.gov/) Filter #

## Description ##

The algorithm opens a file which should contain the pubChem ID of different compounds and assigns each ID to the correspondent compound in the peak list.

The file can have also other information like CAS number or common name that will be also assing to the correspondent compound.

These files should contain these columns:

| **CAS** | **PubChemID** | **Common name** | **Name** |
|:--------|:--------------|:----------------|:---------|
|0-00-0||(-)-(S)-3-Acetoxy-1-methyl-2,5-pyrrolidindione|(-)-(S)-3-Acetoxy-1-methyl-2,5-pyrrolidindione|
|0-00-0|532030|N-(4-methoxyphenyl)cyclopropanecarboxamide|(+)-(1R,2S)-2-(4-Methoxyphenyl)cyclopropanecarboxamide|
|64566-18-3|556973|14-Methyl-8-hexadecyn-1-ol|(R)-(-)-14-Methyl-8-hexadecyn-1-ol|

#### Method parameters ####

_Suffix_
> This text will be added to the name of the filtered version of the data set so that the user will be able to distinguish between both.
_Filter File_
> Path where the file should be found.