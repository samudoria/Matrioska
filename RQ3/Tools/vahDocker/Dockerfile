FROM python:2.7-slim
## FROM --platform=linux/amd64 python:2.7-slim
# FROM --platform=linux/amd64

##
# Set workdir
##
ENV WORKDIR=/workdir
WORKDIR ${WORKDIR}

RUN mkdir /workdir/APPcode && mkdir /workdir/APPmanifest

##
# Install linux dependencies
##
RUN apt-get update && apt-get install -y \
      git \
    && rm -rf /var/lib/apt/lists/*

RUN pip install colorama
RUN apt-get update -y \ 
    && apt-get install lib32stdc++6 lib32z1 lib32z1-dev -y \
    && apt install aapt -y \
    && apt install dexdump -y
# RUN apt install libc++
RUN pip install threadpool \
    && pip install numpy
# RUN pip install aapt

##
# Copy entry point script
##
COPY docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

##
# Copy our custom python script into the androguard directory.
## 
COPY VAHunt ./

RUN ["chmod", "+x", "./docker-entrypoint.sh"]

RUN ["chmod", "+x", "./aapt"]

RUN ["chmod", "+x", "./dexdump"]

ENTRYPOINT ["./docker-entrypoint.sh"]
