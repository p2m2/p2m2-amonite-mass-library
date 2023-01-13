# P2M2 Ammonite Mass Spectrometry library 

## Scala Ammonite script

 - [Ammonite](https://github.com/com-lihaoyi/Ammonite)
 - arguments script : `<project> <experimentation> <input_file>`

### mgf-csv-mzmine-filter.sc

Filter the ions of an MGF according to the header of an associated CSV and the mode of ionization.

The identifier n => for the negative mode p for the positive mode


### Example n9_115.004_0.69

n negative
mass:=> 115.004 , RT: 0.69 (min)

so 0.69 * 60 * 60 => 2484 ms

In the MGF, corresponds to:
PEPMASS=115.0037
RTINSECONDS=41.1311
so 41.1311 * 60 => 2467 ms

## criterion 50 ms
2484-2467 = 17ms < 50ms

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
csv POS
```
output Filter_th_50.0_RacineMetgem Pos.mgf

```shell
./mgf-csv-mzmine-filter.sc RacineMetgem\ Neg.mgf Brassimet_Roots\ after_trKNN_imp_table_10.csv NEG
```
output Filter_th_50.0_RacineMetgem Neg.mgf