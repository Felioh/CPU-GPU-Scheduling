#!/bin/bash

# Specify the input JSON file
input_file=$1

# Use jq to round all floats to integers recursively
jq '
  def roundall:
    if type == "object" then
      with_entries( if .value | type == "array" then .value |= map( if type == "number" then . | round else . end ) else .value |= roundall end )
    elif type == "array" then map( if type == "number" then . | round else roundall end )
    elif type == "number" then . | round
    else .
    end;
  roundall' "$input_file" > $input_file.rounded

# delete the original file
rm $input_file
# rename the new file
mv $input_file.rounded $input_file