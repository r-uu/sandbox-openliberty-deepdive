package io.openliberty.deepdive.rest;

import java.util.List;

import io.openliberty.deepdive.rest.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped @Path("/systems") public class SystemResource
{
	@Inject Inventory inventory;

	@GET @Path("/") @Produces(MediaType.APPLICATION_JSON) public List<SystemData> listContents()
	{
		return inventory.getSystems();
	}

	@GET @Path("/{hostname}")
	@Produces(MediaType.APPLICATION_JSON)
	public SystemData getSystem(@PathParam("hostname") String hostname)
	{
		return inventory.getSystem(hostname);
	}

	@POST @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON) public Response addSystem(
			@QueryParam("hostname") String hostname, @QueryParam("osName") String osName,
			@QueryParam("javaVersion") String javaVersion, @QueryParam("heapSize") Long heapSize)
	{

		SystemData s = inventory.getSystem(hostname);
		if (s != null)
		{
			return fail(hostname + " already exists.");
		}
		inventory.add(hostname, osName, javaVersion, heapSize);
		return success(hostname + " was added.");
	}

	@PUT @Path("/{hostname}") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON) public Response updateSystem(
			@PathParam("hostname") String hostname, @QueryParam("osName") String osName,
			@QueryParam("javaVersion") String javaVersion, @QueryParam("heapSize") Long heapSize)
	{

		SystemData s = inventory.getSystem(hostname);
		if (s == null)
		{
			return fail(hostname + " does not exists.");
		}
		s.setOsName(osName);
		s.setJavaVersion(javaVersion);
		s.setHeapSize(heapSize);
		inventory.update(s);
		return success(hostname + " was updated.");
	}

	@DELETE @Path("/{hostname}") @Produces(MediaType.APPLICATION_JSON) public Response removeSystem(
			@PathParam("hostname") String hostname)
	{
		SystemData s = inventory.getSystem(hostname);
		if (s != null)
		{
			inventory.removeSystem(s);
			return success(hostname + " was removed.");
		}
		else
		{
			return fail(hostname + " does not exists.");
		}
	}

	@POST @Path("/client/{hostname}") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON) public Response addSystemClient(
			@PathParam("hostname") String hostname)
	{
		return fail("This api is not implemented yet.");
	}

	private Response success(String message)
	{
		return Response.ok("{ \"ok\" : \"" + message + "\" }").build();
	}

	private Response fail(String message)
	{
		return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\" : \"" + message + "\" }").build();
	}
}