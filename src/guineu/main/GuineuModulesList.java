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
import guineu.modules.file.exit.ExitProgram;
import guineu.modules.file.openBasicFiles.OpenBasicFile;
import guineu.modules.file.openExpressionFiles.OpenExpressionFile;
import guineu.modules.file.openLCMSDatasetFile.OpenLCMSFileModule;
import guineu.modules.statistics.variationCoefficientRow.VariationCoefficientRowFilter;

/**
 * List of modules included in Guineu
 */
public class GuineuModulesList {

        public static final Class<?> MODULES[] = new Class<?>[]{
                OpenLCMSFileModule.class,
                OpenExpressionFile.class,
                OpenBasicFile.class,
                ExitProgram.class,
                HeatMapModule.class,
                GeneralConfiguration.class,
                ParameterConfiguration.class,
                GCGCColumnsView.class,
                LCMSColumnsView.class,
                ParameterConfiguration.class

        };
}
