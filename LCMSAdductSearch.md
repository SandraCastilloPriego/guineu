# Adduct Search #

## Description ##

#### Definition of an adduct ion ####

An ion formed by interaction of two species, usually an ion and a molecule, and often within the ion source, to form an ion containing all the constituent atoms of one species as well as an additional atom or atoms.

This method identifies common adducts (selected by the user) in a single peak list. The adducts are identified by 2 conditions: 1) the retention time of the original ion and the adduct ion should be same and 2) the mass difference between the original ion and the adduct must be equal to one of the adducts selected by the user.

Guineu has a built-in list of common adducts and their masses. On top of this list, the user can specify a custom adduct mass.

#### Method parameters ####

_RT tolerance_
> Maximum allowed difference of retention time to set a relationship between peaks
_Adducts_
> List of adducts, each one refers a specific distance in m/z axis between related peaks
_Custom adduct value_
> Specific distance in m/z axis between related peaks for custom adduct
_m/z tolerance_
> Tolerance value of the m/z difference between peaks
_Max adduct peak height_
> Maximum height of the recognized adduct peak, relative to the main peak