#!/bin/bash

apk_dirs=("${PWD}/datasets/dataset1/normal_apps" "${PWD}/datasets/dataset1/virtualization_apps" "${PWD}/datasets/dataset2")

lockfile="/tmp/rq3.lock"

exec 200>"$lockfile"

flock -n 200 || { echo "Another instance of the script is already running."; exit 1; }


for apk_dir in $apk_dirs; do
	analysis_type=$(basename ${apk_dir})
	python3 staticVAdetector.py $apk_dir Results
	mv Results/results.csv Results/matrioska_${analysis_type}.csv
	cd vahDocker
	./start.sh $apk_dir
	cd ..
	mv vahDocker/Outcome/VAH_record.csv Results/VAH_record_${analysis_type}.csv
	mv vahDocker/Outcome/VAH_malrecord.csv Results/VAH_malrecord_${analysis_type}.csv	
done

exit 0
