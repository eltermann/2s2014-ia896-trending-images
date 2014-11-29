#!/bin/bash

if [ "$1" = "" ] || [ "$2" = "" ]; then
    echo Plz, inform both hashes to compare.
    exit
fi;

echo "compare $1 $2" | nc localhost 10101
