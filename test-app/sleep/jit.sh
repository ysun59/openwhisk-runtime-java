#!/bin/bash

ifile=$1

grep "cold" $ifile | wc -l
grep "warm" $ifile | wc -l
grep "hot" $ifile | wc -l
grep "very-hot" $ifile | wc -l
grep "scorching" $ifile | wc -l
