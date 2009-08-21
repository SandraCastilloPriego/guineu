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
package guineu.modules.mylly.gcgcaligner.process;

import guineu.modules.mylly.gcgcaligner.gui.StatusReporter;
import java.util.concurrent.CancellationException;



public interface StatusReportingProcess<T,Y> extends Process<T,Y>, StatusReporter
{
	/**
	 * @param input input transformed to output
	 * @return Y
	 * @throws CancellationException if {@link StatusReportingProcess} is cancelled.
	 */
	public Y map(T input) throws CancellationException;
	
	/**
	 * Tries to cancel this {@link StatusReportingProcess}. Returns true if
	 * cancellation is successful, else false. If {@link StatusReportingProcess} is
	 * cancelled, it cannot be run again before resetting its status. May fail if
	 * process is not yet started.
	 * As there is no guarantee of cancellation, use of timeout is suggested.
	 * FIXME See if there is a way to enable asynchronous communication so that cancellation is
	 * guaranteed without major overhaul of systems.
	 * @param timeOut time to wait for acknowledgement of cancellation. 0 may cause forever wait.
	 * @return
	 */
	public boolean cancel(long timeOut);
	
	public boolean isCanceled();
	
	public StatusReportingProcess<T,Y> clone();
	
	
}
