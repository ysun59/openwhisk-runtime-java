#!/bin/bash

# syntax: experiment-run.sh /home/rbruno/git/openwhisk-runtime-java/test-app/sort

app_dir=$1
wrk_dir=$app_dir/results
source $app_dir/experiment.config

url="http://ganymede:8080/run"

# TODO - make sure the docker is up
# TODO - make sure you called $wdir/init-jvm.sh

mkdir $wrk_dir &> /dev/null
rm -r $wrk_dir/*

for cthread in $cthreads
do
    for rep in $reps
    do
        tag="conc-$cthread-rep-$rep"
        nexps=$(($cthread*250))
        echo "running $wrk_dir/$tag.log ..."
        ab \
            -p $app_dir/in.json \
            -T application/json \
            -c $cthread \
            -n $nexps \
            -e $wdir_dir/$tag.csv \
            $url | tee $wrk_dir/$tag.log
        echo "running $wrk_dir/$tag.log ... done"
    done
done
