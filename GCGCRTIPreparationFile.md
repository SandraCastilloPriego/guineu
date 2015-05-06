# RTI Preparation File #

## Description ##

This module opens a text format file which contains information about the retention time index of some compounds and transform it in a XML format file. The XML file can be used by the "Calculate deviations" filter.

Source file example:

```
Name: Cyclohexanecarboxylic acid, trimethylsilyl ester
Formula: C10H20O2Si
MW: 200 CAS#: 69435-89-8 NIST#: 79301 ID#: 32968 DB: mainlib
Other DBs: None
Contributor: O A MAMER, MCGILL UNIVERSITY, MONTRE9  10
largest peaks:   73 999 | 75 587 | 185 426 | 41 109 | 74 95 | 55 90 | 117 84 | 186 70 | 45 69 | 200 57 |
Synonyms:
1.Trimethylsilyl cyclohexanecarboxylate #

Estimated Kovats RI:
Value: 1156 iu
Confidence interval (Diverse functional groups): 89(50%) 382(95%) iu

Kovats index
1. Value: 1188 iu
Column Type: Capillary  Column
Class: Standard non-polar
Active Phase: Methyl Silicone
Data Type: Kovats RI
Program Type: Ramp
Source: Peng, C.T.; Yang, Z.C.; Maltby, D. Prediction of retention indexes.
III. Silylated derivatives of polar compounds J. Chromatogr., 586, 1991, 113-129.
```

#### Method parameters ####

_Input file_
> Path where the input file should be found.
_Output file_
> Path where the output file should be saved.