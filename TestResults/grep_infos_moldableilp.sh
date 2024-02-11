#!/bin/bash

# Specify the input file
input_file=$1

# Initialize variables
n=""
m=""
k=""
total_solve_time=""

# Read the file line by line
while IFS= read -r line; do
    if [[ $line =~ n[0-9]+ ]]; then
        n=$(grep -oP 'n\K[0-9]+' <<< "$line")
        m=$(grep -oP 'm\K[0-9]+' <<< "$line")
        k=$(grep -oP 'k\K[0-9]+' <<< "$line")
    elif [[ $line =~ total_solve_time ]]; then
        total_solve_time=$(grep -oP 'total_solve_time: \K[0-9\.]+' <<< "$line")
        # Print the values in CSV format and reset the variables
        echo "$n,$m,$k,$total_solve_time"
        n=""
        m=""
        k=""
        total_solve_time=""
    fi
done < "$input_file"
