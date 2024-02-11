#!/bin/bash

# script
script=$1
# Directory path
directory=$2


# Loop through each file in the directory
for file in "$directory"/*; do
    # Check if the file is a regular file
    if [[ -f "$file" ]]; then
        # Call your bash script here with the file as an argument
        $script "$file"
    fi
done
