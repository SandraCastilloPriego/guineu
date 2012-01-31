my.boxplot = function(my.data.frame, my.factor, xlabel="Samples", ylabel="Concentration", title="Box and whisker plot of concentrations in each sample") {
   my.color <- my.factor
   levels(my.color) <- 1:length(levels(my.factor))
   boxplot(my.data.frame, col=my.color, xlab=xlabel, ylab=ylabel, main=title)
  # legend(x="topright", levels(my.factor), col=levels(my.color), lwd=4)
}


my.boxplot2 = function(my.data.frame, boxes, colors=NULL, xlabel="Groups", ylabel="Concentration", title="Box and whisker plot of concentrations in each sample") {
   if(length(levels(boxes)) == ncol(my.data.frame)) {
     if(!is.null(colors)) {
       colors2 <- colors
       levels(colors2) <- 1:length(levels(colors))
       boxplot(data.frame(apply(my.data.frame, 2, as.numeric)), col=colors2, xlab=xlabel, ylab=ylabel, main=title)
       legend("topright", legend=levels(colors), col=level(colors2))
     } else {
       boxplot(data.frame(apply(my.data.frame, 2, as.numeric)), xlab=xlabel, ylab=ylabel, main=title)       
     }
   } else {
      newdata <- split(t(apply(my.data.frame, 2, as.numeric)), boxes)
      boxplot(newdata, xlab=xlabel, ylab=ylabel, main=title)
   } 
}
