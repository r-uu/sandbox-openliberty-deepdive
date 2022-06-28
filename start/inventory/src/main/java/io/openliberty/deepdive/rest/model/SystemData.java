package io.openliberty.deepdive.rest.model;

public class SystemData
{
	private int id;

	private String hostname;

	private String osName;

	private String javaVersion;

	private Long heapSize;

	public SystemData() { }

	public SystemData(String hostname, String osName, String javaVersion, Long heapSize)
	{
		this.hostname = hostname;
		this.osName = osName;
		this.javaVersion = javaVersion;
		this.heapSize = heapSize;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public String getOsName()
	{
		return osName;
	}

	public void setOsName(String osName)
	{
		this.osName = osName;
	}

	public String getJavaVersion()
	{
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion)
	{
		this.javaVersion = javaVersion;
	}

	public Long getHeapSize()
	{
		return heapSize;
	}

	public void setHeapSize(Long heapSize)
	{
		this.heapSize = heapSize;
	}

	@Override public boolean equals(Object host)
	{
		if (host instanceof SystemData)
		{
			return hostname.equals(((SystemData) host).getHostname());
		}
		return false;
	}
}