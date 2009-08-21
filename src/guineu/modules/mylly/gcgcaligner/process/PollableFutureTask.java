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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PollableFutureTask<V> implements Pollable, Future<V>
{

	@SuppressWarnings("unchecked")
	private final static PollableCallable	stubPollable	= new PollableCallable()
															{
																public Object call()
																		throws Exception
																{
																	return null;
																}

																public double getDone()
																{
																	return 0;
																}

																public double getTotal()
																{
																	return 0;
																}
															};

	@SuppressWarnings("unchecked")
	private static Future newFuture(Future f)
	{
		try
		{
			return f.getClass().newInstance();
		} catch (InstantiationException e1)
		{
			throw new InternalError(f.getClass().getCanonicalName()
					+ " is abstract/interface although it should not be");
		} catch (IllegalAccessException e1)
		{
			throw new InternalError(
				"No-arg instantiation not supported, although it should be for "
						+ f.getClass().getCanonicalName());
		}
	}

	@SuppressWarnings("unchecked")
	private final static Future		stubFuture	= new Future()
												{
													private boolean	done		= false;
													private boolean	canceled	= false;

													synchronized public boolean cancel(
															boolean mayInterruptIfRunning)
													{
														if (done || canceled) { return false; }
														canceled = true;
														return canceled;
													}

													synchronized public Object get()
															throws InterruptedException,
															ExecutionException
													{
														if (canceled) { throw new InterruptedException(
															"Canceled"); }
														done = true;
														return null;
													}

													synchronized public Object get(
															long timeout,
															TimeUnit unit)
															throws InterruptedException,
															ExecutionException,
															TimeoutException
													{
														return get();
													}

													synchronized public boolean isCancelled()
													{
														return canceled;
													}

													synchronized public boolean isDone()
													{
														return done;
													}

												};

	protected PollableCallable<V>	pollable;
	protected Future<V>				futureTask;

	@SuppressWarnings("unchecked")
	public PollableFutureTask()
	{
		pollable = stubPollable;
		futureTask = newFuture(stubFuture);
	}

	public synchronized boolean setPollableCallabe(PollableCallable<V> pc)
	{
		if (pollable != stubPollable) { return false; }
		pollable = pc;
		futureTask = new FutureTask<V>(pc);
		return true;
	}

	public double getDone()
	{
		return pollable.getDone();
	}

	public double getTotal()
	{
		return pollable.getTotal();
	}

	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return futureTask.cancel(mayInterruptIfRunning);
	}

	public V get() throws InterruptedException, ExecutionException
	{
		return futureTask.get();
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException
	{
		return futureTask.get(timeout, unit);
	}

	public boolean isCancelled()
	{
		return futureTask.isCancelled();
	}

	public boolean isDone()
	{
		return futureTask.isDone();
	}

}
