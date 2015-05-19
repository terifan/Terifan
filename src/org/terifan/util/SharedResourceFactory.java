package org.terifan.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.terifan.util.log.Log;


/**
 * This class manages the creation of a resource used by multiple parties that will be destroyed automatically when no party no longer uses it.
 *
 * <pre>
 * SharedResourceFactory<Database,String,Long> factory = new SharedResourceFactory<>(){
 *	protected Database create(String aConnectionString){
 *   return new Database(aConnectionString);
 *  }
 *	protected Database destroy(Database aDatabase){
 *   aDatabase.close();
 *  }
 * };
 * 
 * SharedResource<Database,Long> database1 = factory.get(connectionString, Thread.currentThread().getId()); // create method called and database open
 * database1.something();
 * ...
 * try (SharedResource<Database,Long> database2 = factory.get(connectionString, Thread.currentThread().getId())) // existing database instance returned
 * {
 *    database2.something();
 * }
 * ...
 * factory.close(Thread.currentThread().getId()); // destroy method called and database close
 * </pre>
 *
 * @param <I> Type of objects created by this factory.
 * @param <P> Type of prototypes this factory accepts for creating new instances.
 * @param <O> Type of objects capable to own an object.
 */
public abstract class SharedResourceFactory<I,P,O>
{
	private HashMap<P,I> mPrototypeInstance;
	private HashMap<I,P> mInstancePrototype;
	private HashMap<I,HashSet<O>> mInstanceOwners;


	public SharedResourceFactory()
	{
		mInstanceOwners = new HashMap<>();
		mInstancePrototype = new HashMap<>();
		mPrototypeInstance = new HashMap<>();
	}


	/**
	 * Return an already created instance or create a new object using the prototype provided.
	 */
	public synchronized SharedResource<I> get(P aPrototype, O aOwner)
	{
		I instance = mPrototypeInstance.get(aPrototype);

		if (instance == null)
		{
			instance = create(aPrototype);

			if (instance == null)
			{
				return null;
			}

			mPrototypeInstance.put(aPrototype, instance);
			mInstancePrototype.put(instance, aPrototype);
		}

		HashSet<O> owners = mInstanceOwners.get(instance);

		if (owners == null)
		{
			owners = new HashSet<>();
			mInstanceOwners.put(instance, owners);
		}

		owners.add(aOwner);

		return new SharedResource<>(this, instance, aOwner);
	}


	/**
	 * Removes an Instance and Owner pair. If the instance isn't owned by any other Owner it will be destroyed.
	 */
	private void remove(I aInstance, O aOwner)
	{
		HashSet<O> owners = mInstanceOwners.get(aInstance);

		if (owners != null)
		{
			if (owners.remove(aOwner) && owners.isEmpty())
			{
				remove(aInstance);
			}
		}
	}


	/**
	 * Removes an Instance and Owner pair. If the instance isn't owned by any other Owner it will be destroyed.
	 */
	public synchronized void remove(SharedResource<I> aInstance)
	{
		remove(aInstance.get(), (O)aInstance.getOwner());
	}


	/**
	 * Removes an Instance. The destroy method will be called.
	 */
	public synchronized void remove(I aInstance)
	{
		P prototype = mInstancePrototype.remove(aInstance);

		if (prototype != null)
		{
			mPrototypeInstance.remove(prototype);
			mInstanceOwners.remove(aInstance);
			destroy(prototype, aInstance);
		}
	}


	/**
	 * Removes the Owner provided from all object instances. If an object isn't owned by any Owner then it will be destroyed.
	 */
	public synchronized void removeOwner(O aOwner)
	{
		for (Object instance : mInstanceOwners.keySet().toArray())
		{
			remove((I)instance, aOwner);
		}
	}


	/**
	 * Removes the instance with the Prototype provided. If an object instance of the Prototype isn't owned by any Owner then it will be destroyed.
	 */
	public synchronized void removePrototype(P aPrototype)
	{
		I instance = mPrototypeInstance.get(aPrototype);

		if (instance != null)
		{
			for (Object owner : mInstanceOwners.get(instance).toArray())
			{
				remove(instance, (O)owner);
			}
		}
	}


	/**
	 * Return number of instances this class manages.
	 */
	public synchronized int size()
	{
		return mInstanceOwners.size();
	}


	public synchronized void clear()
	{
		for (Object instance : mInstancePrototype.keySet().toArray())
		{
			remove((I)instance);
		}
	}


	/**
	 * Return all instances managed by this object.
	 */
	public synchronized Set<I> entries()
	{
		return mInstancePrototype.keySet();
	}


	/**
	 * Return all Owners owning a certain instances managed by this object.
	 */
	public synchronized Set<O> owners(I aInstance)
	{
		return mInstanceOwners.get(aInstance);
	}


	public synchronized boolean containsInstance(I aInstance)
	{
		return mInstancePrototype.containsKey(aInstance);
	}


	public synchronized boolean containsPrototype(P aType)
	{
		return mPrototypeInstance.containsKey(aType);
	}


	protected abstract I create(P aObject);


	protected abstract void destroy(P aPrototype, I aObject);
	
	
	public static void main(String... args)
	{
		try
		{
			SharedResourceFactory<int[],Integer,Object> factory = new SharedResourceFactory<int[], Integer, Object>()
			{
				@Override
				protected int[] create(Integer aObject)
				{
					Log.out.println("create " + aObject);
					return new int[aObject];
				}
				@Override
				protected void destroy(Integer aPrototype, int[] aObject)
				{
					Log.out.println("destroy " + aObject);
				}
			};


			try (SharedResource a = factory.get(4, "a"))
			{
				Log.out.println(a.get());

				try (SharedResource b = factory.get(4, "b"))
				{
					Log.out.println(b.get());
				}

				Log.out.println(a.get());
			}

			try (SharedResource a = factory.get(4, "a"))
			{
				Log.out.println(a.get());
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
