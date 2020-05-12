#!/bin/bash


if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters. Syntax: track-memory.sh <pid> <outfile>"
	exit 1
fi

sampling_period=1
pid=$1
outfile=$2

start=$(date +%s)

while mem=$(ps -o rss= -p "$pid"); do
    time=$(date +%s)

    # print the time since starting the program followed by its memory usage
    printf "%d %s\n" $((time-start)) "$mem" >> "$outfile"

    # sleep for a tenth of a second
    sleep $sampling_period
done
