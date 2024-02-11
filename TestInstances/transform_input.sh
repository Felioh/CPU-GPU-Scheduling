#!/bin/bash

# Check if the number of arguments is correct
if [ $# -ne 1 ]; then
    echo "Error: An argument is required."
    echo "Usage: $0 <input_file> <output_file>"
    exit 1
fi

infile=$1

# Check if the input file exists
if [ ! -f "$infile" ]; then
    echo "Error: Input file '$infile' does not exist."
    exit 1
fi

# use jq to transform the input file

# read number of jobs
n=$(jq '.meta.n' < $infile)
# read number of machines
m=$(jq '.meta.m' < $infile)
# read number of sequential machines
k=$(jq '.meta.k' < $infile)

outdata=$(jq --argjson m $m --argjson k $k --argjson n $n '. + {moldable_machines: $m, sequential_machines: $k, number_jobs: $n, jobs: []}' <<< '{}')

# jobs=$(jq -n '{jobs: []}')

for i in $(seq 1 $(($n))); do
    # read the processing times on the cpu
    processingTimes=$(jq ".cpudata.t$i | .[] | round" < $infile | jq -s '.')
    # read the sequential processing time of the job
    sequentialProcessingTime=$(jq ".gpudata.t$i | round" < $infile)
    # add the job to the output
    outdata=$(jq --argjson i $i --argjson processingTimes "$processingTimes" --argjson sequentialProcessingTime $sequentialProcessingTime '.jobs += [{id: $i, processingTimes: $processingTimes, sequentialProcessingTime: $sequentialProcessingTime}]' <<< $outdata)
done

# write the output to the output file
outfile=$(echo "$infile" | rev | cut -d. -f2- | rev).json
echo $outdata > $outfile

# add metadata to the output
# jq --argjson jobs "$jobs" --argjson m $m --argjson k $k --argjson n $n '. + {moldable_machines: $m, sequential_machines: $k, number_jobs: $n, jobs: $jobs}' <<< '{}' > $outfile

# Display the values of 'infile' and 'outfile'
echo "Input file: $infile"
echo "Output file: $outfile"
