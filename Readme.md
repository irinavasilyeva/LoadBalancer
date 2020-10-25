# Load Balancer Coding Assignment

All the requirements should be met: please see `LoadBalancerTest`.

Since this assignment definitely requires more that 2 hours to complete, I would consider this version as a first iteration.

The next iterations would include:
1. Proper unit testing
2. Proper logging with different levels
3. API description
4. Probably more sophisticated concurrency handling
5. Exception strategy
6. More accurate algorithms implementation
7. Cluster capacity (it should be calculated and insured in different way)
8. More consideration regarding corner cases: how to deal with sequencial algorithm when providers got dead or excluded, how not to allow healthchecker to include the provider which has been manually excluded, how to find out that the provider completed the request (and decrease the number of current reuqest), etc. 
