## ECS 7002 - Assignment 1
### Group M

### Dependency

Note that this agent has an extra dependency on the [SSJ Package from Université de Montréal.](https://www.iro.umontreal.ca/~simardr/ssj/indexe.html).

This package is used for efficient sampling of the Normal and Gamma distributions.

To add this dependency, copy paste the following snippet into the `pom.xml` file in the **dependencies tag:**

`
<dependency>
    <groupId>ca.umontreal.iro.simul</groupId>
    <artifactId>ssj</artifactId>
    <version>3.3.2</version>
</dependency>
`

This package is available in the [Maven Repository](https://mvnrepository.com/artifact/ca.umontreal.iro/ssj/2.5) and has a GPU License.




### Agent description
Group M has opted to implement an MCTS agent. We have extended the BasicMCTS class by copying it over to the GroupM package.

**Our agent is instantiated via the `GroupMMCTSParams` class' `instatiate()` method.**

The class takes the same arguments as BaiscMCTS:
- K
- rolloutLength
- maxTreeDepth
- epsilon
- heuristic


The class can take the following extra arguments:
- nRoots: the number of determinisations to run the agent with (default 1 (PIMCTS))
- exporationStrategy: either UCB1 (default), Thompson (for Thompson sampling), or BayesUCB (for Bayes-UCB)
- amaf: boolean value to enable All Moves As First heuristic.
- name: optional name for the agent.
- prune: boolean value to toggle pruning.
- minRetained: minimum retained nodes when pruning.
- pruneThreshold: threshold value when pruning.
- pruneAlpha: value of alpha when pruning.
- logChildDataEveryN: optional boolean value to log thompson child action values using metric.