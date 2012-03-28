###########################################
# Author: Gopal (gopal.peddinti@vtt.fi)####
# Created: 04-Feb-2010 ####################
# Last update: 15-Feb-2010 ################
# This little tool is for analyzing #######
# Etherpaths gcxgc-tof metabolomics data###
################################################
# Guide for use in Windows:#####################
# Run R Gui, choose File menu, choose "source###
# R code" menu item, and open this script! #####
################################################
# Rules: 1. No spaces in sample names
# 2. One pheno.csv file designed per analysis
# 	with no other samples!



####### STATISTICS PART #############
allvars.anova <- function(dat, pheno, sample.name="SampleName") {
	p <- vector(mode="numeric",length=nrow(dat))
	for(i in seq(nrow(dat))) {
		one.data <- as.numeric(dat[i,pheno[,sample.name]])
		one.2 <- data.frame("Profile"=one.data,
			"Phenotype"=factor(pheno[,"Phenotype"]), "Time"=factor(pheno[,"Time"]))
		one.aov <- aov(Profile ~ Phenotype*Time, data=one.2)
		p[i] <- summary(one.aov)[[1]]["Phenotype:Time", "Pr(>F)"]
	}

	fdr <- p.adjust(p, method="BH")
	return(cbind(p, fdr))
}

silent.t.test <- function(...) {
	obj<-try(t.test(...), silent=TRUE)
	if (is(obj, "try-error")) return(NA) else return(obj$p.value)
}

data.from.sig.metabolites <- function(dat, pheno) {
	sig.rows <- which(dat[,"fdr"] < 0.0001)
	dat.sig <- as.matrix(dat[sig.rows,pheno[,"SampleName"]])
	anno.sig <- dat[sig.rows, setdiff(colnames(dat),pheno[,"SampleName"])]
	rownames(dat.sig) <- paste(rownames(dat.sig),dat[sig.rows, "Metabolite.name"],sep="_")
	rownames(anno.sig) <- rownames(dat.sig)

	return(list("dat.sig"=dat.sig,"anno.sig"=anno.sig))
}


remove.na = function(Object){
  d <- as.matrix(dist(Object))
  d[upper.tri(d)] <- 0
  if(!is.null(na.action(na.omit(d)))) {
    Object <- Object[-na.action(na.omit(d)),]  
  }  
  Object
}

zero.impute = function(Object, covariate=NULL) {
  remove.rows <- apply(Object, 1, sum)
  if(length(which(remove.rows==0))>0){
    Object <- Object[-which(remove.rows==0),]
  }  
  ret.Obj <- NULL
  
  if(is.null(covariate)){
    covariate <- factor(rep(0, ncol(Object)))
  }
  groups <- levels(factor(covariate))
  for(j in seq(length(groups))) {
      datg <- Object[,which(covariate==groups[j])]
      datg[is.na(datg)] <- 0
      
      for(i in seq(nrow(datg))) {
        if(!all(datg[i,]!=0) && (sum(datg[i,]) !=0)) {
          datg[i,which(datg[i,]==0)] <- min(datg[i,which(datg[i,]!=0)])/2
        }
      }
               
      if(j==1) {
        ret.Obj <- datg
      } else {
        ret.Obj <- cbind(ret.Obj, datg)
      }
  }
  return(ret.Obj)
}


fold.changes.no.time <- function(dat, pheno) {
  ##################################
	#### We assume two phenotypes ####
	#### and several time points #####
	##################################
	phenos <- unique(pheno[,"Phenotype"])
  
	pheno1cols <- pheno[which(pheno[,"Phenotype"]==phenos[1]),"SampleName"]
	pheno2cols <- pheno[which(pheno[,"Phenotype"]==phenos[2]),"SampleName"]
	
	res <- data.frame(row.names=rownames(dat))
	res.colnames <- c()
	for(i in seq(length(phenos)))
	{	
		pheno1cols <- pheno[which(pheno[,"Phenotype"]==phenos[1]),"SampleName"]
    pheno2cols <- pheno[which(pheno[,"Phenotype"]==phenos[i]),"SampleName"]
		newcol <- rowMeans(dat[,pheno1cols])/rowMeans(dat[,pheno2cols])
		res <- data.frame(res, newcol)
		res.colnames <- c(res.colnames, as.character(phenos[i]))
	}
	colnames(res) <- res.colnames
  #write.csv(res, "vres.csv");
	return(res)
}

ttest.no.time <- function(dat, pheno){
  ##################################
	#### We assume two or more   #####
  #### phenotypes              #####
	##################################
  phenos <- unique(pheno[,"Phenotype"])
  res <- data.frame()
  res.colnames <- c() 
  for(j in seq(nrow(dat)))
	{
      newrow <- c()
      for(i in seq(length(phenos))){
          pheno1cols <- pheno[which(pheno[,"Phenotype"]==phenos[1]),"SampleName"]
          pheno2cols <- pheno[which(pheno[,"Phenotype"]==phenos[i]),"SampleName"]
          p <- silent.t.test(dat[j, pheno1cols], dat[j, pheno2cols],alternative="two.sided")
          if(j == 1) { res.colnames <- c(res.colnames, phenos[i]) }
  	    	newrow <- c(newrow,p)
      }
      if(j == 1) {
  		  res <- newrow
		  } else {
		  	res <- rbind(res, newrow)
	  	}    
  }
  
  colnames(res) <- res.colnames
  rownames(res) <- row.names(dat)
  write.csv(res, "pres.csv");
	return(res)
}

setGeneric("fold.changes.by.time", def=function(dat, pheno, covariate1, control.covariate1,
                                                covariate2) standardGeneric("fold.changes.by.time"))

setMethod("fold.changes.by.time", signature=c("data.frame", "data.frame", "character", "character",
                                              "missing"),
          function(dat, pheno, covariate1, control.covariate1) {
            ##### In this case, typically, covariate1 has more than two levels
            dat <- data.frame(t(dat), check.names=F)
            cov1 <- factor(pheno[,covariate1])
            dataByGroup <- split(dat, cov1)
            meansByGroup <- lapply(dataByGroup, colMeans)
            caseGroups <- setdiff(levels(cov1), control.covariate1)
            ratiosByGroup <- lapply(meansByGroup[caseGroups],
                                    function(x) { x / meansByGroup[[control.covariate1]] })
            ratiosByGroup <- do.call("data.frame", ratiosByGroup)
            colnames(ratiosByGroup) <- paste(caseGroups, control.covariate1, sep="/")
            ratiosByGroup
          })

setMethod("fold.changes.by.time", signature=c("data.frame", "data.frame", "character", "character",
                                              "character"),
          function(dat, pheno, covariate1, control.covariate1, covariate2) {
            cov1 <- factor(pheno[,covariate1])
            if(length(levels(cov1)) != 2) {
              stop(paste(covariate1, "is allowed to have only two levels"))
            }
            
            cov2 <- factor(pheno[,covariate2])
            dat <- data.frame(t(dat), check.names=F)
            dataByCovs <- split(dat, list(cov2,cov1))
            meansByGroups <- lapply(dataByCovs, colMeans)
            caseGroups <- paste(levels(cov2), setdiff(levels(cov1), control.covariate1), sep=".")
            controlGroups <- paste(levels(cov2), control.covariate1, sep=".")
            ratiosByGroup <- lapply(1:length(levels(cov2)),
                             function(x) {
                               meansByGroups[[caseGroups[x]]] / meansByGroups[[controlGroups[x]]]
                             })
            ratiosByGroup <- do.call("data.frame", ratiosByGroup)
            colnames(ratiosByGroup) <- levels(cov2)
            ratiosByGroup
          })

setGeneric("ttest.by.time", def=function(dat, pheno, covariate1, control.covariate1,
                                                covariate2) standardGeneric("ttest.by.time"))

setMethod("ttest.by.time", signature=c("data.frame", "data.frame", "character", "character",
                                              "missing"),
          function(dat, pheno, covariate1, control.covariate1) {
            
            dat <- data.frame(t(dat), check.names=F)
            cov1 <- factor(pheno[,covariate1])
            dataByGroup <- split(dat, cov1)
            caseGroups <- setdiff(levels(cov1), control.covariate1)
            ttestByGroup <- do.call("data.frame",
                              lapply(caseGroups,
                                    function(x) {
                                      unlist(
                                        lapply(1:ncol(dat), function(j) {
                                          silent.t.test(dataByGroup[[x]][,j],
                                                        dataByGroup[[control.covariate1]][,j])
                                      }))
                                    }))
            colnames(ttestByGroup) <- paste(caseGroups, control.covariate1, sep="/")
            rownames(ttestByGroup) <- colnames(dat)
            return(ttestByGroup)
          })

setMethod("ttest.by.time", signature=c("data.frame", "data.frame", "character", "character",
                                              "character"),
          function(dat, pheno, covariate1, control.covariate1, covariate2) {
            cov1 <- factor(pheno[,covariate1])
            if(length(levels(cov1)) != 2) {
              stop(paste(covariate1, "is allowed to have only two levels"))
            }
            
            cov2 <- factor(pheno[,covariate2])
            dat <- data.frame(t(dat), check.names=F)
            dataByCovs <- split(dat, list(cov2,cov1))
            caseGroups <- paste(levels(cov2), setdiff(levels(cov1), control.covariate1), sep=".")
            controlGroups <- paste(levels(cov2), control.covariate1, sep=".")
            ttestByGroup <- 
              do.call("data.frame",
                      lapply(1:length(caseGroups),
                             function(x) {
                               unlist(
                                 lapply(1:ncol(dat), function(j) {
                                   silent.t.test(dataByCovs[[caseGroups[x]]][,j],
                                                 dataByCovs[[controlGroups[x]]][,j])
                                 }))
                             }))
            colnames(ttestByGroup) <- levels(cov2)
            rownames(ttestByGroup) <- colnames(dat)
            return(ttestByGroup)
          })

draw.heatmap <- function(dat.for.heatmap, pvals.by.time,...) {
	stars.by.time <- matrix("",nrow=nrow(pvals.by.time),ncol=ncol(pvals.by.time))
	for(j in seq(ncol(pvals.by.time)))
	{
		stars.by.time[which(pvals.by.time[,j]<0.05),j]="*"
		stars.by.time[which(pvals.by.time[,j]<0.01),j]="**"
		stars.by.time[which(pvals.by.time[,j]<0.001),j]="***"
	}
	require("gplots")
	br <- c(seq(from=min(dat.for.heatmap,na.rm=T),to=0,length.out=256),
			seq(from=0,to=max(dat.for.heatmap,na.rm=T),length.out=256))

	heatmap.2(dat.for.heatmap,
		trace="none",
		col=bluered(length(br)-1),
		breaks=br,
		density.info="none",
		#symkey=T,
		cellnote=stars.by.time,
		notecol="black",...)
	#detach("package:gplots")
}

draw.heatmap.from.file <- function()
{
	dat.hm <- read.csv(tclvalue(tkgetOpenFile()),row.names=1, stringsAsFactors=F, sep=tclvalue(mysep))
	dat.hm2 <- t(scale(t(as.matrix(dat.hm)),center=F))
	draw.heatmap(dat.hm2, pvals.by.time,
		dendrogram="row",
		Colv=FALSE,
		notecex=5,
		cexCol=3,
		cexRow=3)
}


heat.2 <- function (x, Rowv = TRUE, Colv = if (symm) "Rowv" else TRUE, 
    distfun = dist, hclustfun = hclust, dendrogram = c("both", 
        "row", "column", "none"), symm = FALSE, scale = c("none", 
        "row", "column"), na.rm = TRUE, revC = identical(Colv, 
        "Rowv"), add.expr, breaks, symbreaks = min(x < 0, na.rm = TRUE) || 
        scale != "none", col = "heat.colors", colsep, rowsep, 
    sepcolor = "white", sepwidth = c(0.05, 0.05), cellnote, notecex = 1, 
    notecol = "cyan", na.color = par("bg"), trace = c("column", 
        "row", "both", "none"), tracecol = "cyan", hline = median(breaks), 
    vline = median(breaks), linecol = tracecol, margins = c(5, 
        5), ColSideColors, RowSideColors, cexRow = 0.2 + 1/log10(nr), 
    cexCol = 0.2 + 1/log10(nc), labRow = NULL, labCol = NULL, 
    key = TRUE, keysize = 1.5, density.info = c("histogram", 
        "density", "none"), denscol = tracecol, symkey = min(x < 
        0, na.rm = TRUE) || symbreaks, densadj = 0.25, main = NULL, 
    xlab = NULL, ylab = NULL, lmat = NULL, lhei = NULL, lwid = NULL, xlas = 2,
    ...) 
{
    scale01 <- function(x, low = min(x), high = max(x)) {
        x <- (x - low)/(high - low)
        x
    }
    retval <- list()
    scale <- if (symm && missing(scale)) 
        "none"
    else match.arg(scale)
    dendrogram <- match.arg(dendrogram)
    trace <- match.arg(trace)
    density.info <- match.arg(density.info)
    if (length(col) == 1 && is.character(col)) 
        col <- get(col, mode = "function")
    if (!missing(breaks) && (scale != "none")) 
        warning("Using scale=\"row\" or scale=\"column\" when breaks are", 
            "specified can produce unpredictable results.", "Please consider using only one or the other.")
    if (is.null(Rowv) || is.na(Rowv)) 
        Rowv <- FALSE
    if (is.null(Colv) || is.na(Colv)) 
        Colv <- FALSE
    else if (Colv == "Rowv" && !isTRUE(Rowv)) 
        Colv <- FALSE
    if (length(di <- dim(x)) != 2 || !is.numeric(x)) 
        stop("`x' must be a numeric matrix")
    nr <- di[1]
    nc <- di[2]
    if (nr <= 1 || nc <= 1) 
        stop("`x' must have at least 2 rows and 2 columns")
    if (!is.numeric(margins) || length(margins) != 2) 
        stop("`margins' must be a numeric vector of length 2")
    if (missing(cellnote)) 
        cellnote <- matrix("", ncol = ncol(x), nrow = nrow(x))
    if (!inherits(Rowv, "dendrogram")) {
        if (((!isTRUE(Rowv)) || (is.null(Rowv))) && (dendrogram %in% 
            c("both", "row"))) {
            if (is.logical(Colv) && (Colv)) 
                dendrogram <- "column"
            else dedrogram <- "none"
            warning("Discrepancy: Rowv is FALSE, while dendrogram is `", 
                dendrogram, "'. Omitting row dendogram.")
        }
    }
    if (!inherits(Colv, "dendrogram")) {
        if (((!isTRUE(Colv)) || (is.null(Colv))) && (dendrogram %in% 
            c("both", "column"))) {
            if (is.logical(Rowv) && (Rowv)) 
                dendrogram <- "row"
            else dendrogram <- "none"
            warning("Discrepancy: Colv is FALSE, while dendrogram is `", 
                dendrogram, "'. Omitting column dendogram.")
        }
    }
    if (inherits(Rowv, "dendrogram")) {
        ddr <- Rowv
        rowInd <- order.dendrogram(ddr)
    }
    else if (is.integer(Rowv)) {
        hcr <- hclustfun(distfun(x))
        ddr <- as.dendrogram(hcr)
        ddr <- reorder(ddr, Rowv)
        rowInd <- order.dendrogram(ddr)
        if (nr != length(rowInd)) 
            stop("row dendrogram ordering gave index of wrong length")
    }
    else if (isTRUE(Rowv)) {
        Rowv <- rowMeans(x, na.rm = na.rm)
        hcr <- hclustfun(distfun(x))
        ddr <- as.dendrogram(hcr)
        ddr <- reorder(ddr, Rowv)
        rowInd <- order.dendrogram(ddr)
        if (nr != length(rowInd)) 
            stop("row dendrogram ordering gave index of wrong length")
    }
    else {
        rowInd <- nr:1
    }
    if (inherits(Colv, "dendrogram")) {
        ddc <- Colv
        colInd <- order.dendrogram(ddc)
    }
    else if (identical(Colv, "Rowv")) {
        if (nr != nc) 
            stop("Colv = \"Rowv\" but nrow(x) != ncol(x)")
        if (exists("ddr")) {
            ddc <- ddr
            colInd <- order.dendrogram(ddc)
        }
        else colInd <- rowInd
    }
    else if (is.integer(Colv)) {
        hcc <- hclustfun(distfun(if (symm) 
            x
        else t(x)))
        ddc <- as.dendrogram(hcc)
        ddc <- reorder(ddc, Colv)
        colInd <- order.dendrogram(ddc)
        if (nc != length(colInd)) 
            stop("column dendrogram ordering gave index of wrong length")
    }
    else if (isTRUE(Colv)) {
        Colv <- colMeans(x, na.rm = na.rm)
        hcc <- hclustfun(distfun(if (symm) 
            x
        else t(x)))
        ddc <- as.dendrogram(hcc)
        ddc <- reorder(ddc, Colv)
        colInd <- order.dendrogram(ddc)
        if (nc != length(colInd)) 
            stop("column dendrogram ordering gave index of wrong length")
    }
    else {
        colInd <- 1:nc
    }
    retval$rowInd <- rowInd
    retval$colInd <- colInd
    retval$call <- match.call()
    x <- x[rowInd, colInd]
    x.unscaled <- x
    cellnote <- cellnote[rowInd, colInd]
    if (is.null(labRow)) 
        labRow <- if (is.null(rownames(x))) 
            (1:nr)[rowInd]
        else rownames(x)
    else labRow <- labRow[rowInd]
    if (is.null(labCol)) 
        labCol <- if (is.null(colnames(x))) 
            (1:nc)[colInd]
        else colnames(x)
    else labCol <- labCol[colInd]
    if (scale == "row") {
        retval$rowMeans <- rm <- rowMeans(x, na.rm = na.rm)
        x <- sweep(x, 1, rm)
        retval$rowSDs <- sx <- apply(x, 1, sd, na.rm = na.rm)
        x <- sweep(x, 1, sx, "/")
    }
    else if (scale == "column") {
        retval$colMeans <- rm <- colMeans(x, na.rm = na.rm)
        x <- sweep(x, 2, rm)
        retval$colSDs <- sx <- apply(x, 2, sd, na.rm = na.rm)
        x <- sweep(x, 2, sx, "/")
    }
    if (missing(breaks) || is.null(breaks) || length(breaks) < 
        1) {
        if (missing(col) || is.function(col)) 
            breaks <- 16
        else breaks <- length(col) + 1
    }
    if (length(breaks) == 1) {
        if (!symbreaks) 
            breaks <- seq(min(x, na.rm = na.rm), max(x, na.rm = na.rm), 
                length = breaks)
        else {
            extreme <- max(abs(x), na.rm = TRUE)
            breaks <- seq(-extreme, extreme, length = breaks)
        }
    }
    nbr <- length(breaks)
    ncol <- length(breaks) - 1
    if (class(col) == "function") 
        col <- col(ncol)
    min.breaks <- min(breaks)
    max.breaks <- max(breaks)
    x[x < min.breaks] <- min.breaks
    x[x > max.breaks] <- max.breaks
    if (missing(lhei) || is.null(lhei)) 
        lhei <- c(keysize, 4)
    if (missing(lwid) || is.null(lwid)) 
        lwid <- c(keysize, 4)
    if (missing(lmat) || is.null(lmat)) {
        lmat <- rbind(4:3, 2:1)
        if (!missing(ColSideColors)) {
            if (!is.character(ColSideColors) || length(ColSideColors) != 
                nc) 
                stop("'ColSideColors' must be a character vector of length ncol(x)")
            lmat <- rbind(lmat[1, ] + 1, c(NA, 1), lmat[2, ] + 
                1)
            lhei <- c(lhei[1], 0.2, lhei[2])
        }
        if (!missing(RowSideColors)) {
            if (!is.character(RowSideColors) || length(RowSideColors) != 
                nr) 
                stop("'RowSideColors' must be a character vector of length nrow(x)")
            lmat <- cbind(lmat[, 1] + 1, c(rep(NA, nrow(lmat) - 
                1), 1), lmat[, 2] + 1)
            lwid <- c(lwid[1], 0.2, lwid[2])
        }
        lmat[is.na(lmat)] <- 0
    }
    if (length(lhei) != nrow(lmat)) 
        stop("lhei must have length = nrow(lmat) = ", nrow(lmat))
    if (length(lwid) != ncol(lmat)) 
        stop("lwid must have length = ncol(lmat) =", ncol(lmat))
    op <- par(no.readonly = TRUE)
    on.exit(par(op))
    layout(lmat, widths = lwid, heights = lhei, respect = FALSE)
    if (!missing(RowSideColors)) {
        par(mar = c(margins[1], 0, 0, 0.5))
        image(rbind(1:nr), col = RowSideColors[rowInd], axes = FALSE)
    }
    if (!missing(ColSideColors)) {
        par(mar = c(0.5, 0, 0, margins[2]))
        image(cbind(1:nc), col = ColSideColors[colInd], axes = FALSE)
    }
    par(mar = c(margins[1], 0, 0, margins[2]))
    x <- t(x)
    cellnote <- t(cellnote)
    if (revC) {
        iy <- nr:1
        if (exists("ddr")) 
            ddr <- rev(ddr)
        x <- x[, iy]
        cellnote <- cellnote[, iy]
    }
    else iy <- 1:nr
    image(1:nc, 1:nr, x, xlim = 0.5 + c(0, nc), ylim = 0.5 + 
        c(0, nr), axes = FALSE, xlab = "", ylab = "", col = col, 
        breaks = breaks, ...)
    retval$carpet <- x
    if (exists("ddr")) 
        retval$rowDendrogram <- ddr
    if (exists("ddc")) 
        retval$colDendrogram <- ddc
    retval$breaks <- breaks
    retval$col <- col
    if (!invalid(na.color) & any(is.na(x))) {
        mmat <- ifelse(is.na(x), 1, NA)
        image(1:nc, 1:nr, mmat, axes = FALSE, xlab = "", ylab = "", 
            col = na.color, add = TRUE)
    }
    axis(1, 1:nc, labels = labCol, las = xlas, line = -0.5, tick = 0, 
        cex.axis = cexCol)
    if (!is.null(xlab)) 
        mtext(xlab, side = 1, line = margins[1] - 1.25)
    axis(4, iy, labels = labRow, las = 2, line = -0.5, tick = 0, 
        cex.axis = cexRow)
    if (!is.null(ylab)) 
        mtext(ylab, side = 4, line = margins[2] - 1.25)
    if (!missing(add.expr)) 
        eval(substitute(add.expr))
    if (!missing(colsep)) 
        for (csep in colsep) rect(xleft = csep + 0.5, ybottom = rep(0, 
            length(csep)), xright = csep + 0.5 + sepwidth[1], 
            ytop = rep(ncol(x) + 1, csep), lty = 1, lwd = 1, 
            col = sepcolor, border = sepcolor)
    if (!missing(rowsep)) 
        for (rsep in rowsep) rect(xleft = 0, ybottom = (ncol(x) + 
            1 - rsep) - 0.5, xright = nrow(x) + 1, ytop = (ncol(x) + 
            1 - rsep) - 0.5 - sepwidth[2], lty = 1, lwd = 1, 
            col = sepcolor, border = sepcolor)
    min.scale <- min(breaks)
    max.scale <- max(breaks)
    x.scaled <- scale01(t(x), min.scale, max.scale)
    if (trace %in% c("both", "column")) {
        retval$vline <- vline
        vline.vals <- scale01(vline, min.scale, max.scale)
        for (i in colInd) {
            if (!is.null(vline)) {
                abline(v = i - 0.5 + vline.vals, col = linecol, 
                  lty = 2)
            }
            xv <- rep(i, nrow(x.scaled)) + x.scaled[, i] - 0.5
            xv <- c(xv[1], xv)
            yv <- 1:length(xv) - 0.5
            lines(x = xv, y = yv, lwd = 1, col = tracecol, type = "s")
        }
    }
    if (trace %in% c("both", "row")) {
        retval$hline <- hline
        hline.vals <- scale01(hline, min.scale, max.scale)
        for (i in rowInd) {
            if (!is.null(hline)) {
                abline(h = i + hline, col = linecol, lty = 2)
            }
            yv <- rep(i, ncol(x.scaled)) + x.scaled[i, ] - 0.5
            yv <- rev(c(yv[1], yv))
            xv <- length(yv):1 - 0.5
            lines(x = xv, y = yv, lwd = 1, col = tracecol, type = "s")
        }
    }
    if (!missing(cellnote)) 
        text(x = c(row(cellnote)), y = c(col(cellnote)), labels = c(cellnote), 
            col = notecol, cex = notecex)
    par(mar = c(margins[1], 0, 0, 0))
    if (dendrogram %in% c("both", "row")) {
        plot(ddr, horiz = TRUE, axes = FALSE, yaxs = "i", leaflab = "none")
    }
    else plot.new()
    par(mar = c(0, 0, if (!is.null(main)) 5 else 0, margins[2]))
    if (dendrogram %in% c("both", "column")) {
        plot(ddc, axes = FALSE, xaxs = "i", leaflab = "none")
    }
    else plot.new()
    if (!is.null(main)) 
        title(main, cex.main = 1.5 * op[["cex.main"]])
    if (key) {
        par(mar = c(5, 4, 2, 1), cex = 0.75)
        tmpbreaks <- breaks
        if (symkey) {
            max.raw <- max(abs(c(x, breaks)), na.rm = TRUE)
            min.raw <- -max.raw
            tmpbreaks[1] <- -max(abs(x))
            tmpbreaks[length(tmpbreaks)] <- max(abs(x))
        }
        else {
            min.raw <- min(x, na.rm = TRUE)
            max.raw <- max(x, na.rm = TRUE)
        }
        z <- seq(min.raw, max.raw, length = length(col))
        image(z = matrix(z, ncol = 1), col = col, breaks = tmpbreaks, 
            xaxt = "n", yaxt = "n")
        par(usr = c(0, 1, 0, 1))
        lv <- pretty(breaks)
        xv <- scale01(as.numeric(lv), min.raw, max.raw)
        axis(1, at = xv, labels = lv)
        if (scale == "row") 
            mtext(side = 1, "Row Z-Score", line = 2)
        else if (scale == "column") 
            mtext(side = 1, "Column Z-Score", line = 2)
        else mtext(side = 1, "Value", line = 2)
        if (density.info == "density") {
            dens <- density(x, adjust = densadj, na.rm = TRUE)
            omit <- dens$x < min(breaks) | dens$x > max(breaks)
            dens$x <- dens$x[-omit]
            dens$y <- dens$y[-omit]
            dens$x <- scale01(dens$x, min.raw, max.raw)
            lines(dens$x, dens$y/max(dens$y) * 0.95, col = denscol, 
                lwd = 1)
            axis(2, at = pretty(dens$y)/max(dens$y) * 0.95, pretty(dens$y))
            title("Color Key\nand Density Plot")
            par(cex = 0.5)
            mtext(side = 2, "Density", line = 2)
        }
        else if (density.info == "histogram") {
            h <- hist(x, plot = FALSE, breaks = breaks)
            hx <- scale01(breaks, min.raw, max.raw)
            hy <- c(h$counts, h$counts[length(h$counts)])
            lines(hx, hy/max(hy) * 0.95, lwd = 1, type = "s", 
                col = denscol)
            axis(2, at = pretty(hy)/max(hy) * 0.95, pretty(hy))
            title("Color Key\nand Histogram")
            par(cex = 0.5)
            mtext(side = 2, "Count", line = 2)
        }
        else title("Color Key")
    }
    else plot.new()
    retval$colorTable <- data.frame(low = retval$breaks[-length(retval$breaks)], 
        high = retval$breaks[-1], color = retval$col)
    invisible(retval)
}

draw.heat.2 <- function(dat.for.heatmap, pvals.by.time,...) {
  stars.by.time <- matrix("",nrow=nrow(pvals.by.time),ncol=ncol(pvals.by.time))
	for(j in seq(ncol(pvals.by.time)))
	{
		stars.by.time[which(pvals.by.time[,j]<0.05),j]="*"
		stars.by.time[which(pvals.by.time[,j]<0.01),j]="**"
		stars.by.time[which(pvals.by.time[,j]<0.001),j]="***"
	}
	br <- c(seq(from=min(dat.for.heatmap,na.rm=T),to=0,length.out=256),
			seq(from=0,to=max(dat.for.heatmap,na.rm=T),length.out=256))
	heat.2(dat.for.heatmap,
		trace="none",
		col=bluered(length(br)-1),
		breaks=br,
		density.info="none",
		cellnote=stars.by.time,
		notecol="black",...)	
}







changes <- function(dataset, f){
    if(f == 0){
        return(dataset)
    }else if(f == 1){
        dat <- log2(as.matrix(dataset))      
    }else if(f == 2){
        dat <- t(scale(t(as.matrix(dataset)),center=F))       
    }else if(f == 3){
        dat <- t(scale(t(log2(as.matrix(dataset))),center=F))    
    }
    return(dat)
  
}














##### Following lines were written on 16.8.2010
# to produce heatmaps for a poster.
# These lines were run after performing anova by
# first running the command:

final.hm <- function(dataset, pvals.by.time=NULL, xlas=2,
                     clusterColumns = F, clusterRows = F,
                     wf = 5, hf = 5, mf = 0.55, na.omit = T,
                     device = "X11", OutputFileName = NULL, ...)
{ ## wf = width factor. hf = height factor. mf = margin factor
  
  write.csv(dataset, "dataset.csv")
write.csv(pvals.by.time, "pvalues.csv")
  
  
  if(clusterColumns && clusterRows) {
    dendrogram = "both"
    Rowv = T
    Colv = T
    hahm <- 0.5
    wd <- 0.5
  } else if(clusterColumns) {
    dendrogram = "column"
    Rowv = F
    Colv = T
    hahm <- 0.5
    wd <- 0.01
  } else if(clusterRows) {
    dendrogram = "row"
    Rowv = T
    Colv = F
    hahm <- 0.01
    wd <- 0.5
  } else {
    dendrogram = "none"
    Rowv = F
    Colv = F
    
    hahm <- 0.01
    wd <- 0.01
  }
  
  if(na.omit) {
    removeRows <- na.action(na.omit(dataset))
    if(length(removeRows)>0) {
      dataset <- dataset[-removeRows,]
      rnames <- rownames(dataset)
      dataset <- apply(dataset, 2, as.numeric)
      rownames(dataset) <- rnames
      if(!is.null(pvals.by.time)) {
        pvals.by.time <- pvals.by.time[-removeRows,]
      }
    }
  } 
  
  rnames<-rownames(dataset)
  dataset<-apply(dataset, 2, as.numeric)
  rownames(dataset) <- rnames
  
  library(gplots)

  hhm = 20*plogis(nrow(dataset), location=20, scale=10)
  huhm = min(1.3, (2 - plogis(nrow(dataset), location=20, scale=100)))
  whm= 20*plogis(ncol(dataset), location=20, scale=10)
  cexR = ifelse(nrow(dataset) < 300, 2, 1) * huhm * (log(hhm) / (1+log(nrow(dataset))))
  cexC = ifelse(nrow(dataset) < 300, 2, 1) * huhm * (log(whm) / (1+log(ncol(dataset))))
  noteCex = max(cexR, cexC)
  rowmargin = 1 + max(nchar(rownames(dataset)))
  colmargin = 1 + huhm + max(nchar(colnames(dataset)))
  
  dat <- dataset
  
  switch(device,
         "pdf"=pdf(OutputFileName, width=wd+whm+0.2*rowmargin, height=hahm+hhm+huhm+0.2*colmargin),
         "png"=png(OutputFileName, width=96*(wd+whm+0.2*rowmargin), height=96*(hahm+hhm+huhm+0.2*colmargin), res=96),
         "wmf"=win.metafile(OutputFileName, width=wd+whm+0.2*rowmargin, height=hahm+hhm+huhm+0.2*colmargin)
         #X11(width=wd+whm+0.2*rowmargin, height=hahm+hhm+huhm+0.2*colmargin)
         )
  
  
  if(is.null(pvals.by.time)){    
    br <- c(seq(from=min(dat,na.rm=T),to=0,length.out=256),
            seq(from=0,to=max(dat,na.rm=T),length.out=256))
    heatmap.2(dat,
              trace="none",
              col=bluered(length(br)-1),
              breaks=br,
              density.info="none",	
              notecol="black",
              dendrogram=dendrogram,
              Rowv = Rowv,
              Colv=Colv,
              lmat=rbind( c(0, 3), c(2,1), c(0,4)), lhei=c(hahm, hhm, huhm),
              margins=c(colmargin, rowmargin),
              lwid=c(wd,whm), cexRow=cexR, cexCol=cexC, notecex=noteCex
              )
  } else{
    draw.heat.2(dat,pvals.by.time,
                dendrogram=dendrogram,
                Rowv = Rowv,
                Colv = Colv,
                lmat=rbind(c(0, 3), c(2,1), c(0,4)), lhei=c(hahm, hhm, huhm),
                margins=c(colmargin, rowmargin),
                lwid=c(wd,whm),xlas=xlas, cexRow=cexR, cexCol= cexC, notecex=noteCex, ...
                )
  }
  switch(device,
         "pdf"=dev.off(),
         "png"=dev.off(),
         "wmf"=dev.off()
         )
}


# final.hm <- function(dataset, pvals.by.time=NULL, xlas=1, device = "X11", OutputFileName = NULL, ...)
# {
#  #  write.csv(dataset, "results1.csv")
#   removeRows <- na.action(na.omit(dataset))
#   if(length(removeRows)>0) {
#     dataset <- dataset[-removeRows,]
#     rnames <- rownames(dataset)
#     dataset <- apply(dataset, 2, as.numeric)
#     rownames(dataset) <- rnames
#     if(!is.null(pvals.by.time)) { pvals.by.time <- pvals.by.time[-removeRows,]
#                                   #write.csv(dataset, "pvalues2.csv")
#                                   }
#   }
# # write.csv(dataset, "results2.csv")
#   
#   rnames<-rownames(dataset)
#   dataset<-apply(dataset, 2, as.numeric)
#   rownames(dataset) <- rnames
#   
#   library(gplots)
#   library(JavaGD)
#   cexR = 1.4
#   cexC = 1.4
# 
#   hahm <- 1
#   hhm = 5*log(nrow(dataset))
#   huhm = min(1, log(nrow(dataset)))+0.1*max(nchar(colnames(dataset)))
#   rowmargin = 0.55*max(nchar(rownames(dataset)))
#   colmargin = 0.85*max(nchar(colnames(dataset)))
#   wd=1
#   whm= 5*log(ncol(dataset))
#   
#   
#   dat <- dataset
#   
#   switch(device,
#     "pdf"=pdf(OutputFileName, width=wd+whm+0.1*rowmargin, height=hahm+hhm+huhm+0.1*colmargin),
#     "png"=png(OutputFileName, width=96*(wd+whm+0.1*rowmargin), height=96*(hahm+hhm+huhm+0.1*colmargin), res=96)
#     #,JavaGD(width=96*(wd+whm+0.1*rowmargin), height=96*(hahm+hhm+huhm+0.1*colmargin),ps=1)
#   )
#   
# 
#   if(is.null(pvals.by.time)){    
#       br <- c(seq(from=min(dat,na.rm=T),to=0,length.out=256),
#   		seq(from=0,to=max(dat,na.rm=T),length.out=256))
#     	heatmap.2(dat,
# 		    trace="none",
# 	    	col=bluered(length(br)-1),
# 	    	breaks=br,
# 	    	density.info="none",	
# 		    notecol="black",
#   	    dendrogram="row",
# 	  	  Colv=FALSE,
#         lmat=rbind( c(0, 3), c(2,1), c(0,4)), lhei=c(hahm, hhm, huhm),
#         margins=c(colmargin, rowmargin),
#         lwid=c(wd,whm), cexRow=cexR, cexCol=cexC, notecex=3
#       )
#   } else{
#       draw.heat.2(dat,pvals.by.time,
# 		    dendrogram="row",
# 	  	  Colv=FALSE,
#         lmat=rbind(c(0, 3), c(2,1), c(0,4)), lhei=c(hahm, hhm, huhm),
#         margins=c(colmargin, rowmargin),
#         lwid=c(wd,whm),xlas=1, cexRow=cexR, cexCol= cexC, notecex=3, ...
#       )
#   }
#   switch(device,
#     "pdf"=dev.off(),
#     "png"=dev.off()
#     #,dev.off()
#   )
# }




