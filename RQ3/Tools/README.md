# RQ3 Experiment

In the current directory, you can find the dockerized version of VAHunt and Matrioska's statis analysis (S1 and S2 signatures).

The experiment will consist of running VAHunt, both for virtualization and malware detection, and Matrioska's static analysis on the 2 datasets.

The results can be found in the `Results` directory, which will be created during the first execution of the scripts.

The experiment is expected to have a duration of up to 12 hours.

## Usage 

Execute the `start_experiment.sh` script, it will run the tools in a `screen` instance that can be checked with the `screen -r rq3` command.

The process will be executed in the background.
