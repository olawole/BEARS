# BAYESIAN ECONOMIC ANALYSIS FOR RELEASE PLANNING (BEARS)
BEARS is a JAVA tool that supports project planners in making release planning decisions under uncertainty in the context of release cycles with fixed date and flexible work scope.
"The design and evaluation of BEARS are described in:

Olawole Oni and Emmanuel Letier, [Bayesian Economic Analysis for Software Release Planning under Uncertainty, Under Submission, 2019](https://github.com/olawole/BEARS/blob/master/BEARS-2019.pdf)."

# Download and Installation
Download the jar file [here](https://drive.google.com/file/d/18Lsu9rvc8ItERebjtTKH6ENewAAXnu_G/view?usp=sharing)

# Running BEARS
In order to run BEARS, we provide a runnable JAR file with the supporting document. Download the zipped folder and extract it into a folder. You will see the BearsTool.jar file, an Example folder containing the motivation example data and experiment folder containing the experiment data set.

To execute the motivation example in BEARS, run the command below with the following parameters.

java -jar BearsTool.jar --data models/CouncilNew2.csv --capacity 40,40,40 --interest 2 --alg nsgaii


Where,

--data represents the path to the input csv file

--capacity represents effort capacity per release

--scatter represents a boolean value stating whether to show scatter plot for the Pareto front

--interest represents the interest rate in percentage

--alg represents the algorithm to use. The options are mocell, spea2, nsgaii and Random (non-case sensitive).

--compare used to specify comparison with other methods. i.e. --compare evolve will compare BEARS with evolve, other options are --compare bearsR to compare with BEARS-fixed-scope and --compare all to compare with all the other methods. Do not set it if you only want to run BEARS.

--investment used to specify number of investment horizons. Default is 10 periods.

--numberOfReleases used to specify number of releases to plan. Please note that number of releases must be same to the length of capacity array. if this is not set, the length of capacity array is used

--budget used to specify budget per release. set to 0 by default. Note that if specify, the length must be same as $capacity$ and $numberOfReleases$.

--output used to specify path to the output folder

--help to get more information

# Replicating the experiments
To replicate the experiment, download and extract the supporting document into a folder. Open your terminal and navigate to the root directory of the supporting document. Ensure that the data folder and BearsTool.jar are in the same folder. The data folder contains the synthetic projects used for the empirical evaluation. Run the following command to run the experiment.

java -jar  BearsTool.jar  --exp true

After the execution of the experiment two folders "pareto_front" and "result" will be created in the current working directory. The reference Pareto front for each of the problem are stored in the "pareto_front". In the result folder, there are sub-folders for each of the methods i.e. bears, evolve etc.

NOTE: Running experiment takes considerable amount of time and computing resources.
