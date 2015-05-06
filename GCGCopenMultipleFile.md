# Open multiple sample file #

## Description ##

this module opens files which have been already aligned with Guineu and contain the imformation of more than one sample.

CVS or Excel formats are supported by this algorithm. The order of the columns corresponding to the parameters is not important.

These files should contain these columns:

<font size='1'>
<table><thead><th> <b>RT1</b> </th><th> <b>RT2</b> </th><th> <b>RTI</b> </th><th> <b>N Found</b> </th><th> <b>Cas number</b> </th><th> <b>Max similarity</b> </th><th> <b>Mean similarity</b> </th><th> <b>Similarity std dev</b> </th><th> <b>Metabolite name</b> </th><th> <b>Metabolite all names</b> </th><th> <b>Class</b> </th><th> <b>Pubchem ID</b> </th><th> <b>Mass</b> </th><th> <b>Difference</b> </th><th> <b>Spectrum</b> </th><th> <b>Sample 1</b> </th><th> <b>Sample 2</b> </th><th> <b>...</b> </th></thead><tbody>
<tr><td>920</td><td>1.77</td><td>1570.2</td><td>6 </td><td>0-00-0</td><td>942</td><td>933.33</td><td>4.84</td><td>Creatinine, TMS</td><td>Creatinine, TMS \\ Creatinine, TMS \\ Creatinine, TMS \\ Creatinine, TMS \\ Creatinine, TMS \\ Creatinine, TMS</td><td>null</td><td>null</td><td>115</td><td>0 </td><td><code>[ 73:999 , 115:716 , 45:343 , 143:305 , 100:285 , 171:98 , 59:91 , 116:83 , 74:82 , 114:36 , 75:8 ]</code></td><td>393448</td><td>392641</td><td>...</td></tr>
</font></tbody></table>


#### Method parameters ####

_File Name_
> The path where the file should be found.
_Number of Columns for Parameters_
> Number of columns before the columns corresponding to the samples