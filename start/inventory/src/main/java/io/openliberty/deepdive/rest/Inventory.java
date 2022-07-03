package io.openliberty.deepdive.rest;

import java.util.List;

import io.openliberty.deepdive.rest.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class Inventory
{
	@PersistenceContext(name = "jpa-unit")
	private EntityManager em;

	public List<SystemData> getSystems()
	{
		return em.createNamedQuery("SystemData.findAll", SystemData.class).getResultList();
	}

	public SystemData getSystem(String hostname)
	{
		List<SystemData> result =
				em.createNamedQuery("SystemData.findSystem", SystemData.class)
				  .setParameter("hostname", hostname)
				  .getResultList();
		return result == null || result.isEmpty() ? null : result.get(0);
	}

	public void add(String hostname, String osName, String javaVersion, Long heapSize)
	{
		em.persist(new SystemData(hostname, osName, javaVersion, heapSize));
	}

	public void update(SystemData s)
	{
		em.merge(s);
	}

	public void removeSystem(SystemData s)
	{
		em.remove(s);
	}
}