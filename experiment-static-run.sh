#!/bin/bash

# syntax: experiment-run.sh /home/rbruno/git/openwhisk-runtime-java/test-app/sort

app_dir=$1
wrk_dir=$app_dir/results
source $app_dir/experiment.config

# dockers aka lambdas/photons
dhost="r630-02"
dhuser="frodrigo"

# nginx aka load balancer
nhost="ganymede"
nuser="rbruno"

# target for ab
url="http://$nhost:8080/run"

#mode="photons"

function start_dockers {
    if [ "$mode" == "photons" ]; then
        dockers=1
        vcpu=`python -c "print($cthread*$vcpu_factor)"`
	else
		dockers=$cthread
		vcpu=$vcpu_factor
    fi

	for i in $(seq 1 $dockers);
	do
		docker_port=$((8080+$i))
		docker_name=$app_name_$docker_port
		echo "starting docker $docker_name ..."
    	ssh $dhuser@$dhost \
			"docker run \
				-m $mem \
				--cpus=$vcpu \
				-p $docker_port:8080 \
				--name $docker_name \
				rfbpb/java8action &> /tmp/$tag-docker-$docker_name.log &"
		echo "starting docker $docker_name ... done!"
		sleep 5
	done
}

function stop_dockers {
    if [ "$mode" == "photons" ]; then
        dockers=1
	else
		dockers=$cthread
    fi

	for i in $(seq 1 $dockers);
	do
		docker_port=$((8080+$i))
		docker_name=$app_name_$docker_port
		echo "stopping docker $docker_name ..."
		ssh $dhuser@$dhost "docker kill $docker_name"
	    ssh $dhuser@$dhost "docker rm $docker_name"
		echo "stopping docker $docker_name ... done!"
	done
}

function init_dockers {
    if [ "$mode" == "photons" ]; then
        dockers=1
	else
		dockers=$cthread
    fi

	for i in $(seq 1 $dockers);
	do
		docker_port=$((8080+$i))
		docker_name=$app_name_$docker_port
		echo "initializing docker $docker_name ..."
		tools/invoke.py --host $dhost -p $docker_port init $main $app_dir/$jar
		echo "initializing docker $docker_name ... done!"
	done
}

function reload_nginx {
    if [ "$mode" == "photons" ]; then
        dockers=1
	else
		dockers=$cthread
    fi

    ssh $nuser@$nhost "cp /etc/nginx/nginx.conf.$dockers /etc/nginx/nginx.conf"
    ssh $nuser@$nhost "sudo /usr/sbin/service nginx restart"
}

function run_ab {
    ab \
        -p $app_dir/in.json \
        -T application/json \
        -c $cthread \
        -n $(($cthread*250)) \
        -e $wrk_dir/$tag-iter-$rep.csv \
        $url &> $wrk_dir/$tag-iter-$rep.log
}

function run {
    # step 1: launch docker
    start_dockers

    # step 2: init proxy
    init_dockers

	# step 3: initialize load balancer
	reload_nginx

    # step 4: run apache bench
    for rep in $reps
    do
        echo "running $wrk_dir/$tag-iter-$rep.log ..."
        run_ab
        echo "running $wrk_dir/$tag-iter-$rep.log ... done"
    done

    # step 5: kill docker
    stop_dockers

    # step 6: backup config file
    cp $app_dir/experiment.config $wrk_dir
}

mkdir $wrk_dir &> /dev/null
rm -r $wrk_dir/* &> /dev/null

for cthread in $cthreads
do
    tag="conc-$cthread"
    run
done
