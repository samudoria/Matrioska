#!/bin/sh

if [ $# -ne 1 ]; then
    echo "Usage: $0 apks_dir"
    exit 1
fi

docker run --rm -it --volume $1:/workdir/APKs --volume $(pwd)/Outcome:/workdir/resdata vahunt
