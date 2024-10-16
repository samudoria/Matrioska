# vahDocker

VAHunt can be both launched as a standalone software or within a Docker container.

The `VAHunt` directory contains the framework's source code that we modified to make it work on Linux and fixed to work on multi-dex apps.

Below, we provide instructions to run VAHunt inside a container.

## Usage

The `VAHunt` docker will be automatically created by using the `start.sh` script.

The script requires the path to the apks directory as its only argument.

The script first builds the container:

```
docker buildx build --platform linux/amd64 -t <Tag> .
```

Then, the analysis will start:

```
docker run --rm -it --volume <apk_path>:/workdir/APKs --volume $(pwd)/Outcome:/workdir/resdata <Tag>
```

The process will run in the background in a `screen` instance and the progreess can be checked with the command:

```
screen -x vahunt
```
