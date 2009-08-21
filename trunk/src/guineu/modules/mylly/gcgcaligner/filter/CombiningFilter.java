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
package guineu.modules.mylly.gcgcaligner.filter;


import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.process.StatusChangeListener;
import guineu.modules.mylly.gcgcaligner.process.StatusReportingProcess;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class CombiningFilter extends FutureTask<Alignment> implements StatusReportingProcess<Alignment, Alignment>
{

	public CombiningFilter(Callable<Alignment> callable)
	{
		super(callable);
		// TODO Auto-generated constructor stub
	}

	public boolean cancel(long timeOut)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCanceled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Alignment map(Alignment input) throws CancellationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public CombiningFilter clone(){return null;}

	public void addStatusChangeListener(StatusChangeListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		// TODO Auto-generated method stub
		
	}
}
