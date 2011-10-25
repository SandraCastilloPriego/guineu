/*
 * Copyright 2007-2011 VTT Biotechnology
 *
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.main;

import guineu.modules.R.heatmaps.HeatMapModule;
import guineu.modules.configuration.general.GeneralConfiguration;
import guineu.modules.configuration.parameters.ParameterConfiguration;
import guineu.modules.configuration.tables.GCGC.GCGCColumnsView;
import guineu.modules.configuration.tables.LCMS.LCMSColumnsView;
import guineu.modules.database.deleteDataDB.DeleteDatasetDBModule;
import guineu.modules.database.openDataDB.OpenFileDBModule;
import guineu.modules.database.saveDatasetDB.SaveFileDBModule;
import guineu.modules.file.exit.ExitProgram;
import guineu.modules.file.openBasicFiles.OpenBasicFileModule;
import guineu.modules.file.openExpressionFiles.OpenExpressionFile;
import guineu.modules.file.openLCMSDatasetFile.OpenLCMSFileModule;
import guineu.modules.file.saveGCGCFile.SaveGCGCFileModule;
import guineu.modules.file.saveLCMSFile.SaveLCMSFileModule;
import guineu.modules.file.saveOtherFile.SaveOtherFileModule;
import guineu.modules.filter.Alignment.RANSAC.RansacAlignerModule;
import guineu.modules.filter.Alignment.centering.mean.MeanCenteringModule;
import guineu.modules.filter.Alignment.centering.median.MedianCenteringModule;
import guineu.modules.filter.Alignment.normalizationSTD.STDNormalizationModule;
import guineu.modules.filter.UnitsChangeFilter.UnitsChangeFilterModule;
import guineu.modules.filter.dataselection.DataSelectionModule;
import guineu.modules.filter.relatedpeaks.RelatedPeaksFilterModule;
import guineu.modules.filter.sortingSamples.SortingModule;
import guineu.modules.filter.splitdatasets.SplitModule;
import guineu.modules.filter.transpose.TransposeFilterModule;
import guineu.modules.identification.AdductSearch.AdductSearchModule;
import guineu.modules.identification.CustomIdentification.CustomDBSearchModule;
import guineu.modules.identification.linearnormalization.LinearNormalizerModule;
import guineu.modules.identification.normalizationserum.NormalizeSerumModule;
import guineu.modules.identification.normalizationtissue.NormalizeTissueModule;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentModule;
import guineu.modules.mylly.filter.ConcentrationsFromMass.ConcentrationsFromMassModule;
import guineu.modules.mylly.filter.GroupIdentification.GroupIdentificationModule;
import guineu.modules.mylly.filter.NameFilter.NameFilterModule;
import guineu.modules.mylly.filter.NameGolmIdentification.NameGolmIdentificationModule;
import guineu.modules.mylly.filter.NonPolarComponents.removeNonPolarModule;
import guineu.modules.mylly.filter.SimilarityFilter.SimilarityModule;
import guineu.modules.mylly.filter.calculateDeviations.CalculateDeviationsModule;
import guineu.modules.mylly.filter.classIdentification.ClassIdentificationModule;
import guineu.modules.mylly.filter.linearNormalizer.LinearGCGCNormalizerModule;
import guineu.modules.mylly.filter.peakCounter.PeakCountModule;
import guineu.modules.mylly.filter.pubChem.GolmIdentification.GetGolmIDsModule;
import guineu.modules.mylly.filter.pubChem.PubChemModule;
import guineu.modules.mylly.filter.tools.PrepareDeviationFile.RTIFileModule;
import guineu.modules.mylly.openFiles.OpenGCGCFilesModule;
import guineu.modules.mylly.openGCGCDatasetFile.OpenGCGCDatasetModule;
import guineu.modules.statistics.Media.mediaFilterModule;
import guineu.modules.statistics.PCA.CDAPlotModule;
import guineu.modules.statistics.PCA.PCAPlotModule;
import guineu.modules.statistics.PCA.SammonsPlotModule;
import guineu.modules.statistics.Ttest.TtestModule;
import guineu.modules.statistics.anova.AnovaTestModule;
import guineu.modules.statistics.clustering.ClusteringModule;
import guineu.modules.statistics.foldChanges.FoldtestModule;
import guineu.modules.statistics.standardVariation.StandardVariationModule;
import guineu.modules.statistics.variationCoefficient.VariationCoefficientModule;
import guineu.modules.statistics.variationCoefficientRow.VariationCoefficientRowModule;
import guineu.modules.visualization.intensityboxplot.IntensityBoxPlotModule;
import guineu.modules.visualization.intensityplot.IntensityPlotModule;

/**
 * List of modules included in Guineu
 */
public class GuineuModulesList {

        public static final Class<?> MODULES[] = new Class<?>[]{
                OpenLCMSFileModule.class, 
                OpenGCGCFilesModule.class,
                OpenGCGCDatasetModule.class,
                OpenBasicFileModule.class,
                OpenExpressionFile.class,
                SaveLCMSFileModule.class,
                SaveGCGCFileModule.class,
                SaveOtherFileModule.class,
                ExitProgram.class,                
                GeneralConfiguration.class,
                ParameterConfiguration.class,
                GCGCColumnsView.class,
                LCMSColumnsView.class,
                RansacAlignerModule.class,
                MeanCenteringModule.class,
                MedianCenteringModule.class,
                STDNormalizationModule.class,
                UnitsChangeFilterModule.class,
                SplitModule.class,
                TransposeFilterModule.class,
                SortingModule.class,
                DataSelectionModule.class,
                CustomDBSearchModule.class,
                RelatedPeaksFilterModule.class,
                AdductSearchModule.class,
                LinearNormalizerModule.class,
                NormalizeSerumModule.class,
                NormalizeTissueModule.class,
                ScoreAlignmentModule.class,
                ConcentrationsFromMassModule.class,
                GroupIdentificationModule.class,
                NameGolmIdentificationModule.class,
                ClassIdentificationModule.class,
                NameFilterModule.class,
                GetGolmIDsModule.class,
                removeNonPolarModule.class,
                SimilarityModule.class,
                CalculateDeviationsModule.class,
                PeakCountModule.class,
                PubChemModule.class,
                LinearGCGCNormalizerModule.class,
                RTIFileModule.class,
                VariationCoefficientModule.class,
                VariationCoefficientRowModule.class,                
                mediaFilterModule.class,
                TtestModule.class,
                AnovaTestModule.class,
                FoldtestModule.class,                
               // StandardVariationModule.class,
                PCAPlotModule.class,
                CDAPlotModule.class,
                SammonsPlotModule.class,
                ClusteringModule.class,
                HeatMapModule.class,
                IntensityPlotModule.class,
                IntensityBoxPlotModule.class,
                SaveFileDBModule.class,                
                OpenFileDBModule.class,
                DeleteDatasetDBModule.class

        };
}
