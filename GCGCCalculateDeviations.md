# Calculate Deviations Filter #

## Description ##

This module can work with two different types of files. The basic one is a text file which has "\t" as a separator of each field.

This file should contain these fields:

|  | Name1 | Name2 | Name3 | Name4 | RT | RI |
|:-|:------|:------|:------|:------|:---|:---|
|Alanine, 2TMS|l-Alanine, N-(trimethylsilyl)-, trimethylsilyl ester|N,O-Bis-(trimethylsilyl)alanine|Alanine, 2TMS|  |400 , 1.35|1117,7|
|Arginine (monohydrochlorid), TMS|L-ARGININMONOHYDROCHLORID TMS|Arginine, TMS|  |  |1005 , 1.78|1634,8|
|Aspartic acid, 3TMS|L-Aspartic acid, N-(trimethylsilyl)-, bis(trimethylsilyl) ester|ASPARTIC ACID-TRITMS|N,N,o'-Tris-(trimethylsilyl)aspartic acid|Aspartic acid, 3TMS|900 , 1.69|1539,9|

The second type of file is in XML format:

```
<?xml version="1.0" encoding="utf-8"?>
<Data>
    <Metabolite>
        <Name>Cyclohexanecarboxylic acid</Name>
        <Name>trimethylsilyl ester</Name>
        <CAS>69435-89-8</CAS>
        <RI>1188.0</RI>
        <Column Type="Capillary"/>
    </Metabolite>
    <Metabolite>
        <Name>D-Glucose</Name>
        <Name>2,3,4,5,6-pentakis-O-(trimethylsilyl)-</Name>
        <Name>O-methyloxime</Name>
        <CAS>34152-44-8</CAS>
        <RI>2173.0</RI>
        <Column Type=""/>
    </Metabolite>
    <Metabolite>
        <Name>*4-Hydroxyphenylpropionic acid</Name>
        <Name>TMS</Name>
        <CAS>27750-62-5</CAS>
        <RI>1755.0</RI>
        <Column Type="Packed"/>
    </Metabolite>
    <Metabolite>
        <Name>Benzoic acid trimethylsilyl ester</Name>
        <CAS>2078-12-8</CAS>
        <RI>1253.0</RI>
        <Column Type="Capillary"/>
    </Metabolite>
</Data>
```

The algorithm uses the information of the above files to find the right compounds and calculate the deviation in the retention time index.

#### Method parameters ####

_File Name_
> Path where the file should be found.