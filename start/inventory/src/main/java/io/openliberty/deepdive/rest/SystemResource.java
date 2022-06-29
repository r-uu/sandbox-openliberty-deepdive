package io.openliberty.deepdive.rest;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
@Path("/systems")
public class SystemResource
{
	@Inject
	private Inventory inventory;

	@GET
	@Path             ("/")
	@Produces         (MediaType.APPLICATION_JSON)
	@APIResponseSchema(value               = SystemData.class,
	                   responseDescription = "list of system data stored within the inventory",
	                   responseCode        = "200")
	@Operation        (summary             = "list contents",
	                   description         = "returns the currently stored system data in the inventory",
	                   operationId         = "listContents")
	public List<SystemData> listContents()
	{
		return inventory.getSystems();
	}

	@GET
	@Path    ("/{hostname}")
	@Produces(MediaType.APPLICATION_JSON)
	public SystemData getSystem(@PathParam("hostname") String hostname)
	{
		return inventory.getSystem(hostname);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addSystem(@QueryParam("hostname")    String  hostname,
	                          @QueryParam("osName")      String  osName,
	                          @QueryParam("javaVersion") String  javaVersion,
	                          @QueryParam("heapSize")    Long    heapSize,
	                          @Context                   UriInfo uriInfo)
	{
		SystemData s = inventory.getSystem(hostname);
		if (s != null)
		{
			return fail(hostname + " already exists.");
		}
		inventory.add(hostname, osName, javaVersion, heapSize);
		// return "created" response together with uriInfo
//		return success(hostname + " was added.");
		return created(hostname, uriInfo);
	}

	@PUT @Path("/{hostname}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSystem(
			@PathParam("hostname") String hostname,
			@QueryParam("osName") String osName,
			@QueryParam("javaVersion") String javaVersion,
			@QueryParam("heapSize") Long heapSize)
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

	private Response created(String hostname, UriInfo uriInfo)
	{
    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
    uriBuilder.path(hostname);
    return Response.created(uriBuilder.build()).build();
//		return Response.ok("{ \"ok\" : \"" + message + "\" }").build();
	}

	private Response fail(String message)
	{
		return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\" : \"" + message + "\" }").build();
	}
}