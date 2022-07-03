package io.openliberty.deepdive.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Schema(name = "SystemData", description = "pojo representing a single inventory entry")
@Entity
@Table(name = "SystemData")
@NamedQuery(name = "SystemData.findAll"   , query = "SELECT e FROM SystemData e")
@NamedQuery(name = "SystemData.findSystem", query = "SELECT e FROM SystemData e WHERE e.hostname = :hostname")
public class SystemData
{
	@GeneratedValue
	@Id
	private int id;

	@Schema(required = true)
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
		if (host instanceof SystemData systemData)
		{
			return hostname.equals(systemData.getHostname());
		}
		return false;
	}
}