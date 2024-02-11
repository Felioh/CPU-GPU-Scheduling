# Test Results

This directory contains the test results for the CPU-GPU Scheduling project.

## Contents

- [Test Results](#test-results)
  - [Contents](#contents)
  - [Description](#description)
  - [External Test](#external-test)
  - [Internal Tests](#internal-tests)
    - [File Names](#file-names)
    - [Test Runs](#test-runs)

## Description

The `TestResults` directory stores the results of various tests conducted for the CPU-GPU Scheduling project. These tests are designed to evaluate the performance and efficiency of the scheduling algorithms implemented in the project.

## External Test

We tested our algorithm against an existing algorithm https://github.com/hunsa/moldableILP.

The final results are stored in `.csv` files named `<cpugpu/moldableilp><test_run>.csv`.

`cpugpu` refers to the algorithm implemented in this repository. \
`moldableilp` refers tho the algorithm implemented in https://github.com/hunsa/moldableILP.

The columns in the `.csv` files are as follows:

For the `moldableilp`:
| Column Name | Description                                             | Data Type |
| ----------- | ------------------------------------------------------- | --------- |
| n           | The number of jobs in the test instance.                | Integer   |
| m           | The number of moldable machines in the test instance.   | Integer   |
| l           | The number of sequential machines in the test instance. | Integer   |
| s           | The execution time in seconds.                          | Double    |

For the `cpugpu`:
| Column Name      | Description                                                 | Data Type |
| ---------------- | ----------------------------------------------------------- | --------- |
| InstanceID       | The ID of the test instance.                                | Integer   |
| jobs             | The number of jobs in the test instance.                    | Integer   |
| machines         | The number of machines in the test instance.                | Integer   |
| seqMachines      | The number of sequential machines in the test instance.     | Integer   |
| malMachines      | The number of malicious machines in the test instance.      | Integer   |
| estimatedOptimum | The estimated optimum makespan for the test instance.       | Double    |
| achievedMakespan | The actual makespan achieved by the scheduling algorithm.   | Double    |
| milliseconds     | The time taken by the scheduling algorithm in milliseconds. | Integer   |

## Internal Tests

### File Names

Each `.csv` file contains the results of one set of test runs. They are named `results_<naive/optimized>_<test_run>.csv`. The parameters of each test run are shown below.

The columns in the `.csv` files are as follows:

| Column Name      | Description                                                 | Data Type |
| ---------------- | ----------------------------------------------------------- | --------- |
| InstanceID       | The ID of the test instance.                                | Integer   |
| jobs             | The number of jobs in the test instance.                    | Integer   |
| machines         | The number of machines in the test instance.                | Integer   |
| seqMachines      | The number of sequential machines in the test instance.     | Integer   |
| malMachines      | The number of malicious machines in the test instance.      | Integer   |
| estimatedOptimum | The estimated optimum makespan for the test instance.       | Double    |
| achievedMakespan | The actual makespan achieved by the scheduling algorithm.   | Double    |
| milliseconds     | The time taken by the scheduling algorithm in milliseconds. | Integer   |

Please note that the `.csv` files do not have headers.

### Test Runs

Here are the parameters and results for each test run:

- Test Run 1:

  - Number of Jobs: 50
  - Number of malleable Machines: 10-100
  - Number of Sequential Machines: 20%
  - Maximum Sequential Processing Time: 100

- Test Run 2:
  - Number of Jobs: 10-100
  - Number of malleable Machines: 50
  - Number of Sequential Machines: 20%
  - Maximum Sequential Processing Time: 100
