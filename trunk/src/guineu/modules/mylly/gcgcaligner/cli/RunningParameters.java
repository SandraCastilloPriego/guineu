/*
    Copyright 2006-2007 VTT Biotechnology

    This file is part of MYLLY.

    MYLLY is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MYLLY is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MYLLY; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package guineu.modules.mylly.gcgcaligner.cli;


import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import java.util.Arrays;


public class RunningParameters
{
	private AlignmentParameters	_alignParams;
	private String _inputFileNames[];
	private String _outputFileName;
	private boolean _useBatch;

	public RunningParameters()
	{
		this(new AlignmentParameters());
	}
	
	public RunningParameters(AlignmentParameters alignParams)
	{
		_alignParams = alignParams;
	}
	
	public String[] getInputFileNames()
	{
		return _inputFileNames;
	}
	
	public RunningParameters clone()
	{
		RunningParameters clone = new RunningParameters(_alignParams);
		clone._inputFileNames = (_inputFileNames == null ? null : _inputFileNames.clone());
		clone._outputFileName = _outputFileName;
		return clone;
	}

	public RunningParameters setInputFileNames(String inputFileNames[])
	{
		if (inputFileNames != null)
		{
			_inputFileNames = inputFileNames.clone();
		}
		return this;
	}

	public String getOutputFileName()
	{
		return _outputFileName;
	}

	public RunningParameters setOutputFileName(String outputFileName)
	{
		assert(outputFileName != null);
		_outputFileName = outputFileName;
		return this;
	}
	
	public RunningParameters setAlignParams(AlignmentParameters newParams)
	{
		assert(newParams != null);
		_alignParams = newParams;
		return this;
	}
	
	public AlignmentParameters getAlignParams()
	{
		return _alignParams;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Running parameters:\n");
		sb.append("Input files: ").append(Arrays.asList(_inputFileNames).toString()).append('\n');
		sb.append("Output file: ").append(_outputFileName).append('\n');
		sb.append("Run in batch mode: ").append(_useBatch).append('\n');
		sb.append("Alignment parameters:\n").append(_alignParams.toString());
		return sb.toString();
	}

	public boolean useBatch()
	{
		return _useBatch;
	}

	public RunningParameters setUseBatch(boolean batch)
	{
		_useBatch = batch;
		return this;
	}
	
}
