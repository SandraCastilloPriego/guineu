# Singling Filter #

## Description ##

The algorithm chooses between the compounds with the same name the one which contains the largest peak and also the peak nearest to the ideal based on its similarity. This two characteristics can correspond to the same peak and in that case the algorithm will leave only this peak and filter out the rest with the same name.

#### Method parameters ####

_Suffix_
> This text will be added to the name of the filtered version of the data set so that the user will be able to distinguish between both.
_Filter Unknowns Peaks_
> Remove the compounds identify as unknown.
_Minimun similarity required_
> Minimum value of similarity below which compounds will be deleted.