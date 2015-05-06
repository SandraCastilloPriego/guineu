# Similarity Filter #

## Description ##

This algorithm filters all compounds that have less similarity than the user-defined similarity.

The user can choose between two kinds of similarity (maximum and mean) and the filter can delete the compounds or rename them to "unknown".

#### Method parameters ####

_Suffix_
> This text will be added to the name of the filtered version of the data set so that the user will be able to distinguish between both.
_Similarity used_
> There are two options: maximum similarity (by defect) and mean similarity. One of these two similarities is used by the algorithm to filter the compounds.
_Action_
> Compounds that meet the filtering criteria may be deleted or renamed to "unknown" by the algorithm.
_Minimum similarity required_
> The user has to set the similarity threshold.