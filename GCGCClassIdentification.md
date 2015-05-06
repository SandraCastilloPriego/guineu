# Class Identification #

## Description ##

the algorithm identifies the class of each compound based on its spectra and using some rules that the user must define in advance.

The format of the file which contains the rules should be CVS file, and it should be like this:

```
Alkanes,(Ordinal(57)<=2)&(Ordinal(71)<=2)&(Retention(2)>=1)&(Retention(2)<=5.5)
Alkenes and cyloalkanes,((Ordinal(55)=1)|(Ordinal(69)=1))&(Intensity(55)>0)&(Intensity(69)>0)&(((Relative(56)>15)+(Relative(57)>15)+(Relative(70)>15)+(Relative(83)>15)+(Relative(97)>15))>=3)&(Retention(2)>=1)&(Retention(2)<2)
n-Akane acids,(Ordinal(60)=1)&(Ordinal(73)=2)
Alkyl-substituted benzenes,(Relative(91)>15)&(Relative(77)>5)&((Retention(2)>2)|(Retention(1)<28.33))|((Relative(77)>25)&(Retention(2)<2)&(Retention(1)<28.33))
Polar benzenes,(Relative(77)>25)&(Retention(2)>2)
Partly hydrated naphthalenes and alkanyl-substituted benzenes,(Relative(91)>15)&(Relative(77)>5)&(Relative(128)>10)&(Retention(2)>2)
Naphthalene and alkyl-substituted naphthalenes,(((Relative(128)>15)&(Relative(77)>5))|((Relative(141)>50)|(Relative(155)>50)|(Relative(169)>50)))&(Retention(2)>2)
2,3-Butaneidol,(Ordinal(45)<3)
Decane,(Ordinal(57)<3)&(Retention(1)<2.3)
Undecane ,(Ordinal(57)<3)&(Retention(1)<2.3)&(Retention(2)<1.8)
```

### Theory ###

**"Computer language for identifying chemicals with comprehensive two-dimensional gas chromatography and mass spectrometry"** Stephen E. Reichenbacha, Visweswara Kottapallib, Mingtian Nia and Arvind Visvanathanb

This paper describes a language for expressing criteria for chemical identification with comprehensive two-dimensional gas chromatography paired with mass spectrometry (GC× GC–MS) and presents computer-based tools implementing the language. The Computer Language for Indentifying Chemicals (CLIC) allows expressions that describe rules (or constraints) for selecting chemical peaks or data points based on multi-dimensional chromatographic properties and mass spectral characteristics. CLIC offers chromatographic functions of retention times, functions of mass spectra, numbers for quantitative and relational evaluation, and logical and arithmetic operators. The language is demonstrated with the compound-class selection rules described by Welthagen et al. [W. Welthagen, J. Schnelle-Kreis, R. Zimmermann, J. Chromatogr. A 1019 (2003) 233–249]. A software implementation of CLIC provides a calculator-like graphical user-interface (GUI) for building and applying selection expressions. From the selection calculator, expressions can be used to select chromatographic peaks that meet the criteria or create selection chromatograms that mask data points inconsistent with the criteria. Selection expressions can be combined with graphical, geometric constraints in the retention-time plane as a powerful component for chemical identification with template matching or used to speed and improve mass spectrum library searches.

### Selection language functions ###

| Selection mode | Description |
|:---------------|:------------|
| Retention (dimension) | Returns the retention time of the current object (either pixel or blob) with respect to the chromatographic column indicated by the dimension parameter (either 1 or 2 for GC × GC). For both blob-peak and blob integration modes, the function returns the retention time of the peak pixel. Retention time for dimension = 1 is expressed in minutes and retention time for dimension = 2 is expressed in seconds. |
| Intensity (channel) | Returns the intensity value of the indicated channel (m/z in a mass spectrum) in the multi-channel intensity array of the current object (either pixel or blob). If the indicated channel = 0 (or null), the function returns the total intensity. |
| Ordinal (channel) | Returns the ordinal position of the indicated channel (m/z in a mass spectrum) in the intensity-ordered multi-channel array of the current object (either pixel or blob). |
| Percent (channel) | Returns the intensity value of the indicated channel (m/z in a mass spectrum) in the multi-channel intensity array of the current object (either pixel or blob) as a percentage of the total intensity of the array. |
| Relative (channel) | Returns the intensity value of the indicated channel (m/z in a mass spectrum) in the multi-channel intensity array of the current object (either pixel or blob) as a relative percentage of the largest intensity value of the array. |

The functions are used in expressions with comparative operators to express the selection criteria. The relational operators are: less than, less than or equal to, greater than, greater than or equal to, equal to, and not equal to. For example, in blob-peak expression mode, Retention(2)<1.0 selects all blobs for which the second-column retention time of the blob pixel with peak total intensity is less than 1.0 s. In GC× GC data with a 5 s modulation period, this would select all blobs for which the peak is in the first 20% of the secondary chromatograms. In pixel expression mode, Ordinal(14)=1 would select all pixels for which the intensity value of the pixel’s mass spectrum at m/z=14 is the largest intensity value in that mass spectrum.

CLIC provides for addition (+), subtraction (–), and arithmetic negation (–) of values and parentheses for grouping arithmetic terms. For example, in blob-peak expression mode, (Percent(14) + Percent(28) + Percent(42)) ¡ 50 selects all blobs for which the sum of the intensities at m/z=14, 28, and 42 is less than 50% of the total intensity for the blob peak pixel.

CLIC provides logical-and (&), logical-or (|), and logical-negation (!) operations and parentheses for grouping logical elements. For example, in pixel expression mode, (Relative(57)>20) & (Retention(2)>2.0) selects all pixels for which the intensity at m/z=57 is greater than 20% of the largest intensity value in the pixel mass spectrum and the second-column retention time is greater than 2 s.

### Expression language grammar ###

```
<Expression> ::= <AndExpression>
    |   <Expression>  | <AndExpression>
<AndExpression> ::= <EqualityExpression>
    |   <AndExpression> & <EqualityExpression>
<EqualityExpression> ::= <RelationalExpression>
    |   <EqualityExpression><EqualityOperator><RelationalExpression>
<EqualityOperator> ::= = | !=
<RelationalExpression> ::= <AdditiveExpression>
    |   <RelationalExpresssion><RelationalOperator><AdditiveOperator>
<RelationalOperator> ::= < | <= | > | >=
<AdditiveExpression> ::= <MultiplicativeExpression>
    |   <AdditiveExpression><AdditiveOperator><MultiplicativeExpression>
<AdditiveOperator> ::= + | -
<MultiplicativeExpression> ::= <ValueExpression>
    |   <MultiplicativeExpression><MultiplicativeOperator><ValueExpression>
<MultiplicativeOperator> ::= * | /
<ValueExpression> ::= ( <Expression> )
    |   <UnaryOperator><ValueExpression>
    |   <Function>
    |   <Number>
<UnaryOperator> ::= + | - | !
<Function> ::= Retention | Intensity | Ordinal | Percent | Relative
<Number> ::= <Integer>
    |   <Integer>.
    |   <Integer>.<Integer>
    |   .<Integer>
<Integer> ::= <Digit>
    |   <Integer><Digit>
<Digit> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 
```

#### Method parameters ####

_Suffix_
> This text will be added to the name of the filtered version of the data set so that the user will be able to distinguish between both.
_File Name_
> Path of the file which contains the rules.