# Score Alignment #

## Description ##

This algorithm aligns the compounds of different peak lists, creating a new peak list with multiple samples.

### Algorithm ###

The alignment algorithm first goes through all files, creating a list of peaks for each of the files. These lists are then used to construct alignment paths using the algorithm presented below. A list of alignment paths A is the constructed and sorted based on scoring of each individual alignment path. Starting with the path of lowest score, alignment paths are added to the list of actual alignments L. As an alignment path P is added to this list L, it is removed from A, and A is then purged of alignment paths that contain peaks found in P. Peaks in P are then also removed from list of peaks in all files.

### Pseudocode ###

```
 1:procedure CREATEPATH(row, col, files, params)
 2:      path <- emptyAlignmentPath
 3:      add files[row][col] to path
 4:      for i <- cols + 1 to ncols(files) do
 5:          best <- nil, scorebest <- ∞
 6:          for all compounds c in column i of files do
 7:              score <- ∞
 8:              ∆RT1 <- |c.RT1 - path.RT1|
 9:              ∆RT2 <- |c.RT2 - path.RT2|
 10:             ∆RTI <- |c.RTI - path.RTI|
 11:             if All ∆RTIx < params.maxRTx then
 12:                 sim <- ƒ(path,c) (see equation after the pseudocode)
 13:                 if sim < params.minsimilarity then
 14:                    score <- ∆RT1 + ∆RT2 + ∆RTI
 15:                 end if
 16:             end if
 17:             if score < score,,best,, then
 18:                 best <- c
 19:                 scorebest <- score
 20:             end if
 21:         end for
 22:         add best to path
 23:     end for
 24:     return path
 25:end procedure
```

Equation to calculate the similarity between one peak and the current alignment path:

ƒ(_s<sub>1</sub>_, _s<sub>2</sub>_) = [∑ ((_m<sub>1</sub>__m<sub>2</sub>_)<sup>j</sup>(_I<sub>1</sub>_, _I<sub>2</sub>_)<sup>k</sup>)<sup>2</sup>] / [∑ _m<sub>1</sub><sup>2j</sup>I<sub>1</sub>_<sup>2k</sup> ∑ _m<sub>2</sub><sup>2j</sup>I<sub>2</sub>_<sup>2k</sup>]

#### Method parameters ####

_RT Lax_
> Maximum difference allowed between the retention time of the path and the retention time of the candidate peak.
_RT2 Lax_
> Maximum difference allowed between the second retention time of the path and the second retention time of the candidate peak.
_RTI Lax_
> Maximum difference allowed between the retention time index of the path and the retention time index of the candidate peak.
_RT penalty_
> This value is used to calculate the path score.
**_score_ = _∆<sub>RT1</sub>_  _RT penalty_ + _∆<sub>RT2</sub>_  _RT2 penalty_ + _∆<sub>RTI</sub>_ _RTI penalty_**

_RT2 penalty_
> This value is used to calculate the path score.
**_score_ = _∆<sub>RT1</sub>_  _RT penalty_ + _∆<sub>RT2</sub>_  _RT2 penalty_ + _∆<sub>RTI</sub>_  _RTI penalty_**

_RTI penalty_
> This value is used to calculate the path score.
**_score_ = _∆<sub>RT1</sub>_  _RT penalty_ + _∆<sub>RT2</sub>_  _RT2 penalty_ + _∆<sub>RTI</sub>_  _RTI penalty_**

_Minimum Spectrum Match_
> Minimum similarity value between path and candidate peak spectra.
_Bonus for matching names_
> When the name of the path and the name of the peak are equal the score gets a bonus.
_Drop peaks with similarity less than_
> The algorithm doesn't take into account the peaks with less similarity than this value.
_Use Concentrations_
> Use concentrations instead of areas.