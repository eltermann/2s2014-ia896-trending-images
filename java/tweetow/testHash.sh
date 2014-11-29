#!/bin/bash

if [ "$1" = "" ]; then
    echo Plz, inform the file path!
    exit
fi;

echo "hash $1" | nc localhost 10101
