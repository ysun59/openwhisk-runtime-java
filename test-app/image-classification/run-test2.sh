#!/bin/bash
#run-test2.sh

for c in '1' '2' '4' '8'; do
	for nt in '16' '4'; do
		echo "Running test2 for concurrency ${c} , nt ${nt}"
		./test2.sh ${c} ${nt}
		echo ""
		sleep 5
	done
done


for c in '1' '4' '8' '16'; do
        for nt in '32'; do
                echo "Running test2 for concurrency ${c} , nt ${nt}"
                ./test2.sh ${c} ${nt}
                echo ""
                sleep 5
        done
done



for c in '1' '4' '16' '32'; do
        for nt in '60'; do
                echo "Running test2 for concurrency ${c} , nt ${nt}"
                ./test2.sh ${c} ${nt}
                echo ""
		echo ""
                sleep 30
        done
done
