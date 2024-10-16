# RQ3: How well does our solution perform in detecting VBR malware with respect to the state-of-art?

The directory contains datasets, source code, compiled code, and scripts to answer RQ3, as presented in Section 9 of the paper. In particular:

-   `Datasets` contains Dataset1, used in the evaluation of Section 9.2, and Dataset2, used in the evaluation of Section 9.3;

-   `Plots` contains the code used for generating Figure 3 and Figure 4 in the paper;

-   `Results` contains all the results from the experiments performed over Dataset1 and Dataset2 and described in Section 9.2 and Section 9.3, respectively;

-   `Tools` contains Matrioska and VAHunt source codes, as well as additional utility scripts for analyzing Matrioska's output and for processing packed apps. VAHunt is provided within a Docker container.

# Reproduction of the results shown in Section 9.2

These experiments reflect the results shown in Table 1 in Section 9.2 of the paper.

## Requirements

These experiments require the execution of Matrioska's S1 and S2 signatures and of the first detection mechanism of VAHunt over the apps of Dataset1. To speed up the analysis, we implemented Matrioska's S1 and S2 through a static analysis procedure (the effectiveness is the same as their runtime version). Being both executed as static analyzer, there is no need for an Android device.

To run locally, first install the requirements using:

```
pip install -r requirements.txt
```

The experiment can be launched by executing the `start_experiment.sh` script in the `Tools` directory.
`screen` is required to run the experiment in the background (a dedicated server is suggested).

To check its status `screen -x rq3` will show the current output of the script.

## Dataset

The apps used in this experiment are the 100 apps specified in Dataset1, which details are available in the `./Datasets/Dataset1/dataset1.csv` file.

## Experiment execution

A runnable VAHunt version is already available under the `Tools/vahDocker` path. To execute it against Dataset1 apps, please, check out the detailed instruction in the directory.

Similarly, Matrioska's S1 and S2 signatures can be executed through the `staticVAdetector.py`, available under the `Tools` directory path. To execute it, please, use the following command:

`python3 staticVAdetector.py <samples_directory> <output_directory>`

## Expected duration

The experiment should last up to 12 hours.

## Expected output

The output of our analysis is provided in the CSV files available in the `./Results/Dataset1` directory. You can check it out for more details.

### Comparing the results

When the experiment terminates, you will find in the `Results` directory the outcome of the analyses carried out by Matrioska and VAHunt.

To compare the files obtained, please follow the following correspondence:

| `Results` (local)                    | `RQ3/Results/Dataset1` (Repository)             |
| ------------------------------------ | ----------------------------------------------- |
| `matrioska_normal_apps.csv`          | `true_negative_s1_s2_matrioska_performance.csv` |
| `matrioska_virtualization_apps.csv`  | `true_positive_s1_s2_matrioska_performance.csv` |
| `VAH_record_normal_apps.csv`         | `true_negative_vahunt_performance.csv`          |
| `VAH_record_virtualization_apps.csv` | `true_positive_vahunt_performance.csv`          |

# Reproduction of the results shown in Figure 3 of Section 9.3

This experiment follows the same methodology described in the previous point, but this time applied to the 152,602 samples contained in Dataset2.

## Requirements

Launch the experiment mentioned in the previous point but with Dataset2.

## Dataset

The apps used in this experiment are the 152,602 malware specified in Dataset2, which details are available in the four csv files under the `./Datasets/Dataset2` directory.

## Experiment execution

Follow the instructions provided in the `README.md` file under the `Tools` directory.

## Expected duration

This experiment will last up to 12 hours.

## Expected output

The output of our analysis is provided in the `s1_s2_matrioska_vahunt.csv` and `s1_s2_matrioska.csv` files available in the `./Results/Dataset2` directory. You can check them out for more details.

# Reproduction of the results shown in Figure 4 of Section 9.3

## Requirements

To run Matrioska's dynamic analysis (S3, S4, S5 and S6), an ARM64 device is required. We used a Huawei P9 Lite running Android 7 and a Pixel 3a running Android 9. Due to the constraints of our laptop machines, we were not able to test the experiment on emulated devices.

## Dataset

The first analysis over the 152,602 malware allowed us to identify the 1764 samples using the virtualization technique. Thus, this experiment is executed against these 1764 samples to discriminate the benign and the malicious ones.

## Experiment execution

The `autoVAD.py` Python script can automatically perform Matrioska's dynamic analysis through the following command:

```
python3 autoVAD.py <samples_directory> mod=[7, 7arm, 9, 9arm]
- <mod> allows to specify the Matrioska version (i.e., for Android 7 or for Android 9) and the arm native libraries
```

Once the dynamic analysis is done, you have to execute the `dynamicResultReader.py` script on the results, to classify the samples.

The script can be executed with `python3 dynamicResultReader.py <path_to_results.csv>`. The requirements can be installed by running `pip install -r requirements.txt`.

The `autoVAD.py` generates csv files that collect all the signatures found by Matrioska, while `dynamicResultReader.py` generates a csv file that contains a verdict on the maliciousness of the samples.

## Expected output

The output of our analysis is provided in the matrioskaXX_results_XX.csv files available in the `./Results/Dataset2` directory. You can check them out for more details.
The final result of the analysis, together with a manual ground truth, can be found in the `malware_reverseLabel.csv` under the `./Results/Dataset2` directory.
A video demo of five malware samples detected at runtime by Matrioska is available at the following [YouTube video](https://youtu.be/6XtZOxxsYPw).

## Expected disk usage

The experiment is expected to occupy a small amount of space (<10MB), resulting from the csv files generated.

# Experiment with packed apps

Since we found multiple packed apps among the analyzed 152,602 malware, we share here an automatic script for unpacking them.

In the `./Tools/unpack_apps` directory, we provide the setup, while in the README.md file inside the same we share information about the experiment.
