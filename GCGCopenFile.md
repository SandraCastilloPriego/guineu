# Open one sample file #

## Description ##

This module opens text files which contain GCxGC-MS data.

The order of the columns is not important but their name should be always the same.

The text files should contain these columns:

| **Name** | **R.T (s)** | **Retention Index** | **CAS** | **Sample Concentration** | **Quant Masses** | **Unique Mass** | **Quant S/N** | **Area** | **Similarity** | **Weight** | **Classifications** | **Type** | **Spectra** |
|:---------|:------------|:--------------------|:--------|:-------------------------|:-----------------|:----------------|:--------------|:---------|:---------------|:-----------|:--------------------|:---------|:------------|
| cis-3-Hexenal | 270 , 1.690 |996.11 | 6789-80-6 |  | Dt | 55 | 100.93 | 267525 | 755 | 98 |  | Unknown |55:999 57:319 69:276 ..|
| Unknown 1 | 275 , 0.110 | 999.08 | 16587-45-4 |  | Dt | 175 | 52.77 | 163655 | 423 | 190 | Class - 1 | Unknown | 74:999 175:590 50:552 .. |

## Method parameters ##

_Separator_
> The files can have different separators between its fields. This separator can be "," , "space" or "tab" ("\t"). "\t" is the separator used by default.
_Filter out peaks with classification_
> When this parameter is checked, all the compounds with anything written in the column "Classification" will be ignored.
_GCGC files_
> Selection of all the files that should be opened