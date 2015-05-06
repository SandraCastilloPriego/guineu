# Open Mass Lynx Files #

## Description ##

This module opens files from Mass Lynx software changing its format.

The original file should be like this:

```

Quantify Compound Summary Report

Printed Wed Jun 16 12:18:56 2010

Compound 1:  9,10-EpOME

	Name                                Type	RT	Area	IS Area	Height	Height/Area	S/N LOQ	Primary Flags
1	050510_1000ppbSTD_IS_042212_a_mthA		10.51	0.651		20	30.722          NO	db
2	050510_400ppbSTD_IS_042210_a_mthA		10.29	1.429		25	17.495          NO	bb
3	050510_160ppbSTD_IS_042210_a_mthA
4	050510_64ppbSTD_IS_042210_a_mthA		10.78	0.997		28	28.084          NO	bd
5	050510_26ppbSTD_IS_042210_a_mthA		10.41	0.660		19	28.788          NO	bb
6	050510_10ppbSTD_IS_042210_a_mthA		10.32	1.750		29	16.571          NO	bd

Compound 2:  13(R,S)-HODE

	Name                                Type	RT	Area	IS Area	Height	Height/Area	S/N LOQ	Primary Flags
1	050510_1000ppbSTD_IS_042212_a_mthA		8.53	15.102		432	28.605          NO	bd
2	050510_400ppbSTD_IS_042210_a_mthA		8.23	34.815		813	23.352          YES	dd
3	050510_160ppbSTD_IS_042210_a_mthA		8.26	5.699		149	26.145          NO	db
4	050510_64ppbSTD_IS_042210_a_mthA		8.41	18.438		675	36.609          NO	bb
5	050510_26ppbSTD_IS_042210_a_mthA		8.38	14.095		508	36.041          YES	bb
6	050510_10ppbSTD_IS_042210_a_mthA		8.62	12.733		440	34.556          YES	db
Compound 3:  12,13-EpOME

	Name                                Type	RT	Area	IS Area	Height	Height/Area	S/N LOQ	Primary Flags
1	050510_1000ppbSTD_IS_042212_a_mthA		10.07	4.592		158	34.408          NO	bd
2	050510_400ppbSTD_IS_042210_a_mthA		10.26	2.324		53	22.806          NO	db
3	050510_160ppbSTD_IS_042210_a_mthA		10.56	1.417		28	19.760          NO	db
4	050510_64ppbSTD_IS_042210_a_mthA		10.34	3.482		98	28.145          NO	dd
5	050510_26ppbSTD_IS_042210_a_mthA		10.09	7.298		265	36.311          NO	bb
6	050510_10ppbSTD_IS_042210_a_mthA		10.10	3.115		69	22.151          NO	db

```

The output file will look like this:

```

Name                                9,10-EpOME - Type	9,10-EpOME - RT     9,10-EpOME - Area	9,10-EpOME - IS Area	9,10-EpOME - Height	9,10-EpOME - Height/Area	9,10-EpOME - S/N LOQ	9,10-EpOME - Primary Flags	13(R,S)-HODE - Type	13(R,S)-HODE - RT	13(R,S)-HODE - Area	13(R,S)-HODE - IS Area	13(R,S)-HODE - Height	13(R,S)-HODE - Height/Area	13(R,S)-HODE - S/N LOQ	13(R,S)-HODE - Primary Flags	12,13-EpOME - Type	12,13-EpOME - RT	12,13-EpOME - Area	12,13-EpOME - IS Area	12,13-EpOME - Height	12,13-EpOME - Height/Area	12,13-EpOME - S/N LOQ	12,13-EpOME - Primary Flags
050510_1000ppbSTD_IS_042212_a_mthA                      10.51               0.65                                        20                      30.72                           NO                      db                                                      8.53                    15.1                                            432                     28.61                           NO                      bd                                                      10.07                   4.59                                            158                     34.41                           NO                      bd
050510_400ppbSTD_IS_042210_a_mthA                       10.29               1.43                                        25                      17.5                            NO                      bb                                                      8.23                    34.82                                           813                     23.35                           YES                     dd                                                      10.26                   2.32                                            53                      22.81                           NO                      db
050510_160ppbSTD_IS_042210_a_mthA                                                                                                                                                                                                                               8.26                    5.7                                             149                     26.15                           NO                      db                                                      10.56                   1.42                                            28                      19.76                           NO                      db
050510_64ppbSTD_IS_042210_a_mthA                        10.78               1                                           28                      28.08                           NO                      bd                                                      8.41                    18.44                                           675                     36.61                           NO                      bb                                                      10.34                   3.48                                            98                      28.15                           NO                      dd
050510_26ppbSTD_IS_042210_a_mthA                        10.41               0.66                                        19                      28.79                           NO                      bb                                                      8.38                    14.1                                            508                     36.04                           YES                     bb                                                      10.09                   7.3                                             265                     36.31                           NO                      bb
050510_10ppbSTD_IS_042210_a_mthA                        10.32               1.75                                        29                      16.57                           NO                      bd                                                      8.62                    12.73                                           440                     34.56                           YES                     db                                                      10.1                    3.12                                            69                      22.15                           NO                      db
050510_4ppbSTD_IS_042210_a_mthA                         10.25               1.38                                        32                      23.17                           NO                      bd                                                      8.3                     1.08                                            22                      20.3                            NO                      db                                                      10.26                   7.18                                            162                     22.57                           NO                      bd

```