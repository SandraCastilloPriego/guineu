# ANOVA #

## Description ##

In its simplest form ANOVA provides a statistical test of whether or not the means of several groups are all equal, and therefore generalizes Student's two-sample t-test to more than two groups. ANOVAs are helpful because they possess a certain advantage over a two-sample t-test. Doing multiple two-sample t-tests would result in a largely increased chance of committing a type I error. For this reason, ANOVAs are useful in comparing three or more means.

This module uses Apache Commons math libraries to perform ANOVA test on each compound in the list.

The result is a P value which represents the the probability of obtaining a test statistic at least as extreme as the one that was actually observed, assuming that the null hypothesis is true. The lower the p-value, the less likely the result is if the null hypothesis is true, and consequently the more "significant" the result is, in the sense of statistical significance.

[Source](http://en.wikipedia.org/wiki/Analysis_of_variance)

The groups of samples are taken from the module "parameters" in the "configuration" menu, where the user has to define the meta data of the samples.