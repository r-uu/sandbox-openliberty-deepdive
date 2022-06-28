package io.openliberty.deepdive.rest;

import io.openliberty.deepdive.rest.model.SystemData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory
{
	private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());

	public List<SystemData> getSystems()
	{
		return systems;
	}

	public SystemData getSystem(String hostname)
	{
		for (SystemData s : systems)
		{
			if (s.getHostname().equalsIgnoreCase(hostname))
			{
				return s;
			}
		}
		return null;
	}

	public void add(String hostname, String osName, String javaVersion, Long heapSize)
	{
		systems.add(new SystemData(hostname, osName, javaVersion, heapSize));
	}

	public void update(SystemData s)
	{
		for (SystemData systemData : systems)
		{
			if (systemData.getHostname().equalsIgnoreCase(s.getHostname()))
			{
				systemData.setOsName(s.getOsName());
				systemData.setJavaVersion(s.getJavaVersion());
				systemData.setHeapSize(s.getHeapSize());
			}
		}
	}

	public boolean removeSystem(SystemData s)
	{
		return systems.remove(s);
	}
}