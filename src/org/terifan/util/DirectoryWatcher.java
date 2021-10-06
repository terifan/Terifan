package org.terifan.util;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


/**
 * Wrapper for java.nio.file.WatchService for monitoring a single directory for changes.
 *
 * <pre>
 * // watch a directory without blocking for all changes and print events
 * DirectoryWatcher w1 = new DirectoryWatcher();
 * w1.monitorAsync(targetPath, (watcher, path, kind) -> System.out.println(kind + " " + path));
 *
 * // watch a directory while blocking, stop watching when a file is deleted
 * DirectoryWatcher w2 = new DirectoryWatcher();
 * w2.monitorBlock(dir.toPath(), (watcher, path, kind) -> watcher.stop(), StandardWatchEventKinds.ENTRY_DELETE);
 *
 * w1.stop();
 * </pre>
 */
public class DirectoryWatcher
{
	private WatchService mWatcher;


	public DirectoryWatcher() throws IOException
	{
		mWatcher = FileSystems.getDefault().newWatchService();
	}


	/**
	 * Starts a thread and watches a path.
	 *
	 * @param aPath the directory to watch
	 * @param aCallback called with events
	 * @param aTriggerOnKinds one or more specific events to watch, if no Kinds are provided all events with be watched!
	 */
	public void monitorAsync(Path aPath, WatcherCallback aCallback, Kind... Kinds)
	{
		new Thread()
		{
			{
				setDaemon(true);
			}
			@Override
			public void run()
			{
				monitorBlock(aPath, aCallback);
			}
		}.start();
	}


	/**
	 * Watches a path while blocking. Call the <i>stop</i> method to stop watching.
	 *
	 * @param aPath the directory to watch
	 * @param aCallback called with events
	 * @param aTriggerOnKinds one or more specific events to watch, if no Kinds are provided all events with be watched!
	 */
	public void monitorBlock(Path aPath, WatcherCallback aCallback, Kind<?>... aTriggerOnKinds)
	{
		try
		{
			if (aTriggerOnKinds.length == 0)
			{
				aPath.register(mWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			}
			else
			{
				aPath.register(mWatcher, aTriggerOnKinds);
			}

			boolean valid;

			do
			{
				WatchKey key = mWatcher.take();

				for (WatchEvent<?> event : key.pollEvents())
				{
					try
					{
						aCallback.callback(DirectoryWatcher.this, aPath.resolve(((WatchEvent<Path>)event).context()), event.kind());
					}
					catch (Exception e)
					{
						e.printStackTrace(System.err);
					}
				}

				valid = key.reset();
			}
			while (valid);

			stop();
		}
		catch (ClosedWatchServiceException e)
		{
			// ignore
		}
		catch (Error | Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	public void stop()
	{
		try (WatchService w = mWatcher)
		{
			mWatcher = null;
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}


	@FunctionalInterface
	public interface WatcherCallback
	{
		void callback(DirectoryWatcher aWatcher, Path aPath, WatchEvent.Kind<?> aKind);
	}
}
