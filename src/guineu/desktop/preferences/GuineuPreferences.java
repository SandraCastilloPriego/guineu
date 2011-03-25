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

package guineu.desktop.preferences;

import guineu.desktop.numberFormat.NumberFormatParameter;
import guineu.desktop.numberFormat.RTFormatParameter;
import guineu.desktop.numberFormat.RTFormatter;
import guineu.desktop.numberFormat.RTFormatterType;
import guineu.main.GuineuCore;
import guineu.parameters.Parameter;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.OptionalModuleParameter;
import guineu.util.dialogs.ExitCode;
import java.text.DecimalFormat;


/**
 *
 */
public class GuineuPreferences extends SimpleParameterSet {

	public static final NumberFormatParameter mzFormat = new NumberFormatParameter(
			"m/z value format",
			"Format of m/z values. Please check the help file for details.",
			new DecimalFormat("0.000"));

	public static final RTFormatParameter rtFormat = new RTFormatParameter(
			"Retention time value format",
			"Format of retention time values. Please check the help file for details.",
			new RTFormatter(RTFormatterType.NumberInMin, "0.0"));

	public static final NumberFormatParameter intensityFormat = new NumberFormatParameter(
			"Intensity format",
			"Format of intensity values. Please check the help file for details.",
			new DecimalFormat("0.0E0"));

	public static final NumOfThreadsParameter numOfThreads = new NumOfThreadsParameter();

	public static final OptionalModuleParameter proxySettings = new OptionalModuleParameter(
			"Use proxy", "Use proxy for internet connection",
			new ProxySettings());

	public static final WindowStateParameter windowState = new WindowStateParameter();

	public GuineuPreferences() {
		super(new Parameter[] { mzFormat, rtFormat, intensityFormat,
				numOfThreads, proxySettings, windowState });
	}

	public ExitCode showSetupDialog() {

		ExitCode retVal = super.showSetupDialog();

		if (retVal == ExitCode.OK) {

			// Update system proxy settings
			if (getParameter(proxySettings).getValue()) {
				ParameterSet proxyParams = getParameter(proxySettings)
						.getEmbeddedParameters();
				String address = proxyParams.getParameter(
						ProxySettings.proxyAddress).getValue();
				String port = proxyParams.getParameter(ProxySettings.proxyPort)
						.getValue();
				System.setProperty("http.proxyHost", address);
				System.setProperty("http.proxyPort", port);
			} else {
				System.clearProperty("http.proxyHost");
				System.clearProperty("http.proxyPort");
			}

			// Repaint windows to update number formats
			GuineuCore.getDesktop().getMainFrame().repaint();
		}

		return retVal;
	}

}
