#!/bin/bash

# Specify the input file
input_file=$1

grep 'Result: ' $input_file | sed 's/^.*: //'