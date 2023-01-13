# P2M2 Ammonite Mass Spectrometry library 
## Scala Ammonite script

 - [Ammonite](https://github.com/com-lihaoyi/Ammonite)
 - arguments script : `<project> <experimentation> <input_file>`

### mgf-csv-mzmine-filter.sc

```shell
./mgf-csv-mzmine-filter.sc MetGem_Pos_Feuilles.mgf Brassimet_Leaves\ after_trKNN_imp_table_10.csv POS
```
output `Filter_th_50.0_MetGem_Pos_Feuilles.mgf`

```shell
./mgf-csv-mzmine-filter.sc MetGem_Neg_Feuilles.mgf Brassimet_Leaves\ after_trKNN_imp_table_10.csv NEG
```
output `Filter_th_50.0_MetGem_Neg_Feuilles.mgf`

```shell
./mgf-csv-mzmine-filter.sc RacineMetgem\ Pos.mgf Brassimet_Roots\ after_trKNN_imp_table_10.csv POS
csv NEG
```
output 