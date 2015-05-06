# Linear Normalizer #

## Description ##

This algorithm normalizes the intensities of the compounds using one or more standard compounds.

The normalization algorithm will be different depending on the number of standards selected by the user. If there is only one selected standard, its amount will be fixed to 100 and the rest of the intensities will be changed with reference to the standard:

`[100 * Compound intensity]/[Standard intensity] `

If more than one standard have been selected, the algorithm will select the most appropriate standard for each row and it will be used to do the normalization as is explained above. The selection of the standard is based on the similarity of the first retention time.