#!/bin/sh

if [ $# -ne 1 ]; then
    echo "Usage: $0 apks_dir"
    exit 1
fi

# since analysis might take a long time run the command in background with screen 
# to check progress run `screen -r vahunt`
docker buildx build --platform linux/amd64 -t vahunt .
screen -dm -S vahunt ./docker_run.sh $1
