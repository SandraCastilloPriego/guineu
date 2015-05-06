# Projection Plot #

## Description ##

High-dimensional data, meaning data that requires more than two or three dimensions to be represented, can be difficult to interpret. One approach to simplification is to assume that the data of interest lies on an embedded non-linear manifold within the higher-dimensional space. If the manifold is of low enough dimension then the data can be visualised in the low dimensional space.


### Principal component analysis (PCA) ###

Principal component analysis (PCA) involves a mathematical procedure that transforms a number of possibly correlated variables into a smaller number of uncorrelated variables called principal components.

PCA is mathematically defined as an orthogonal linear transformation that transforms the data to a new coordinate system such that the greatest variance by any projection of the data comes to lie on the first coordinate (called the first principal component), the second greatest variance on the second coordinate, and so on. PCA is theoretically the optimum transform for given data in least square terms. (http://en.wikipedia.org/wiki/Principal_component_analysis)

![http://1.bp.blogspot.com/_FvytGGj7NgU/TCnR8DHBZ-I/AAAAAAAAAQI/bC3jyRfU2y4/s1600/PCA.png](http://1.bp.blogspot.com/_FvytGGj7NgU/TCnR8DHBZ-I/AAAAAAAAAQI/bC3jyRfU2y4/s1600/PCA.png)

### Sammon's projection ###

The main use for the projection is visualization. Sammon's projection is useful for preliminary analysis in all statistical pattern recognition, because a rough visualization of the class distribution can be obtained, especially the overlap of the classes.

Denote the distance between ith and jth objects in the original space by d\*ij, and the distance between their projections by dij. Sammon's projection aims to minimize the following error function, which is often referred to as Sammon's stress:

![http://2.bp.blogspot.com/_FvytGGj7NgU/TCnRwPGWLnI/AAAAAAAAAPw/_vgqSDX-Clk/s1600/error+function.png](http://2.bp.blogspot.com/_FvytGGj7NgU/TCnRwPGWLnI/AAAAAAAAAPw/_vgqSDX-Clk/s1600/error+function.png)

The minimization can be performed either by gradient descent or by other means. (http://en.wikipedia.org/wiki/Sammon%27s_projection)

![http://3.bp.blogspot.com/_FvytGGj7NgU/TCnSFP_7HUI/AAAAAAAAAQw/3QhZdudSwgM/s1600/SP.png](http://3.bp.blogspot.com/_FvytGGj7NgU/TCnSFP_7HUI/AAAAAAAAAQw/3QhZdudSwgM/s1600/SP.png)

### Curvilinear distance analysis (CDA) ###

This algorithm trains a self-organizing neural network to fit the manifold and seeks to preserve geodesic distances in its embedding. It based on Curvilinear Component Analysis (which extended Sammon's mapping), but uses geodesic distances instead.

![http://3.bp.blogspot.com/_FvytGGj7NgU/TCnRvgjNwFI/AAAAAAAAAPg/GslZJm47NvY/s1600/CDA.png](http://3.bp.blogspot.com/_FvytGGj7NgU/TCnRvgjNwFI/AAAAAAAAAPg/GslZJm47NvY/s1600/CDA.png)

#### Method parameters ####

_Data files_
> Raw data files correspondent to the samples selected to bi in the projection plot.
_Coloring style_
> The dots corresponding to every sample can be colored depending on the sample's parameter state or on the file.
_Peaks_
> Peaks that will be taken into account to create the projection plot.
_Component on X-axis_
> This parameters is only enable in PCA algorithm and it allows to the user to choose the component on X axis.
_Component on Y-axis_
> This parameters is only enable in PCA algorithm and it allows to the user to choose the component on X axis