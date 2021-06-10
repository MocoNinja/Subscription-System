#!/bin/bash

## 
# This scripts builds the image for the database.
# It must be placed in the same directory that the Dockerfile and init script.
# It accepts a custom version (via -v flag); default one is: latest.
# Can call help with -h flag.
##

IMAGE_NAME="subscriptions-database"
DEFAULT_TAG="latest"

while getopts ":v:h" arg; do
  case $arg in
    v) INPUT_TAG=$OPTARG;;
    h) # Display Help
	echo "Usage: $0 ( -v [VERSION; default: latest] | -h [HELP])"	
	exit 0;;
    *) # Wrong input: Display Help and Exit
	echo "Usage: $0 ( -v [VERSION; default: latest] | -h [HELP])"	
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

FULL_IMAGE="$IMAGE_NAME:$TAG"
echo "Building image: \"$FULL_IMAGE\"..."

docker build -t $FULL_IMAGE .
