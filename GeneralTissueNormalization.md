# Tissue Normalization #

## Description ##

Normalization is based on internal standards. One internal standard is use to normalize the intensity of similar group of compounds. The similarity is based on the compound name, so the identificacion should be done before this step. The standard compound used by the unknown compounds depends on the ranges of retention time set by the user.

Levenshtein Distance Algorithm is used to get the similarity between the name of the compound and the name of the standards.

One dataset has to be selected to perform its normalization.

Normalization formula:

```

            Normalized Intensity = (Intensity of the compound / Intensity of the standard compound) * (Amount of standard added / Weight of the sample);

```

The final units will depend on the units of the number you introduce. For example:

```

            Amount of standard = umols/sample
            Weight of the sample = ug/sample
            The final units will be:
                umols of compound / ug

```

#### Method parameters ####

_Standard Amount_
> Amount of standard added during the sample preparation.
_RT Range_
> Range of retention time where the standard will be use to normalize unknown compounds.
_Sample Weights_
> Weight of each sample.