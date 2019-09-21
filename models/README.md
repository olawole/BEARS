
This following files contains the effort and value estimations for the 8 product backlog used in the paper's experiments:

* councilNew2.csv for the local government project backlog

* ralic-rate.csv for the RALIC project backlog

* releaseplanner-data.csv for the release planner backlog

* word-processing.csv for the word processing backlog
 
* b30.csv, b50.csv, b100.csv, b200.csv for the 4 synthetic models

These are the files used as input by BEARS and in our experiments.

Each of these files contains the list of work items in the backlog. For each work item, the file includes the mean and standard deviation of the lognormal distribution for the work item's effort and value uncertainty. It also list precedence dependencies for that work item. 

The file Council-Backlog-Raw.xlsx contains the raw data used to define the effort and value probability distributions for the local government problem. This file was used to create councilNew2.csv.

The file Council-Backlog-EVOLVE.csv contains the EVOLVE-II effort and value estimations for the local government project derived from their BEARS estimates as described in the paper. The data can be entered in ReleasePlanner (...) to generate the EVOLVE-II shortlist in the first experiment.
