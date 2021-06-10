#!/bin/bash

IMAGE_NAME="subscriptions-database"
DEFAULT_TAG="latest"
CONTAINER_NAME="subscriptions-database"
DEFAULT_PORT="65432"

while getopts ":v:p:h" arg; do
  case $arg in
    v) INPUT_TAG=$OPTARG;;
    p) INPUT_PORT=$OPTARG;;
    h) # Display Help
        echo "Usage: $0 ( -v [VERSION; default: latest], -p [PORT; default: 65432] | -h [HELP])"   
        exit 0;;
    *) # Wrong input: Display Help and Exit
        echo "Usage: $0 ( -v [VERSION; default: latest], -p [PORT; default: 65432] | -h [HELP])"   
        exit 1;;
  esac
done

if [ ! -z "$INPUT_TAG" ]; then
        echo "Custom tag: $INPUT_TAG"
        TAG="$INPUT_TAG"
else
        echo "Using default tag: $DEFAULT_TAG"
        TAG="$DEFAULT_TAG"
fi

if [ ! -z "$INPUT_PORT" ]; then
        echo "Custom port: $INPUT_PORT"
        PORT="$INPUT_PORT"
else
        echo "Using default port: $DEFAULT_PORT"
        PORT="$DEFAULT_PORT"
fi

if [ ! -z $(docker ps -a | grep $CONTAINER_NAME | awk '{print $1}') ]; then
	echo "Container is already running. Removing it..."
	docker rm -f $CONTAINER_NAME
fi

echo "Starting container in port: $PORT..."

docker run -d -p $PORT:5432 --name $CONTAINER_NAME $IMAGE_NAME:$DEFAULT_TAG
