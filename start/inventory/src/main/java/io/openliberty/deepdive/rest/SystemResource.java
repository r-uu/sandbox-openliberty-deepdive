package io.openliberty.deepdive.rest;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.openliberty.deepdive.rest.client.SystemClient;
import io.openliberty.deepdive.rest.client.UnknownUriExceptionMapper;
import io.openliberty.deepdive.rest.model.SystemData;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

	@Inject
	@ConfigProperty(name = "client.https.port")
	private String clientPort;

	@Inject
	private JsonWebToken jwt;


	@GET
	@Path             ("/")
	@Produces         (MediaType.APPLICATION_JSON)
	@APIResponseSchema(value               = SystemData.class,
	                   responseDescription = "list of system data items stored within the inventory",
	                   responseCode        = HttpResponseConstants._OK_)
	@Operation        (summary             = "list contents",
	                   description         = "returns the system data currently stored within the inventory",
	                   operationId         = "listSystems")
	public List<SystemData> listSystems()
	{
		return inventory.getSystems();
	}

	@GET
	@Path             ("/{hostname}")
	@Produces         (MediaType.APPLICATION_JSON)
	@APIResponseSchema(value               = SystemData.class,
	                   responseDescription = "system data of a particular host.",
	                   responseCode        = HttpResponseConstants._OK_)
	@Operation        (summary             = "get system data",
	                   description         =   "retrieves and returns the system data for the system "
	                                         + "service running on the particular host.",
	                   operationId         = "getSystem")
	public SystemData getSystem(
			@Parameter(name        = "hostname",
			           in          = ParameterIn.PATH,
			           description = "The hostname of the system",
			           required    = true,
			           example     = "localhost",
			           schema      = @Schema(type = SchemaType.STRING))
			@PathParam("hostname")
			String hostname)
	{
		return inventory.getSystem(hostname);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@APIResponses(value = { @APIResponse(responseCode = HttpResponseConstants._CREATED_,
	                                     description  = "successfully added system to inventory"),
	                        @APIResponse(responseCode = HttpResponseConstants._BAD_REQUEST_,
	                                     description  = "unable to add system to inventory")})
	@Parameters  (value = { @Parameter(name        = "hostname",
	                                   in          = ParameterIn.QUERY,
	                                   description = "hostname of the system",
	                                   required    = true,
	                                   example     = "localhost",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name        = "osName",
	                                   in          = ParameterIn.QUERY,
	                                   description = "operating system of the system",
	                                   required    = true,
	                                   example     = "linux",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name        = "javaVersion",
	                                   in          = ParameterIn.QUERY,
	                                   description = "java version of the system",
	                                   required    = true,
	                                   example     = "11",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name        = "heapSize",
	                                   in          = ParameterIn.QUERY,
	                                   description = "heap size of the system",
	                                   required    = true,
	                                   example     = "1048576",
	                                   schema      = @Schema(type = SchemaType.NUMBER))})
	@Operation   (summary     = "add system",
	              description = "add a system and its data to the inventory",
	              operationId = "addSystem")
	public Response addSystem(@QueryParam("hostname"   ) String  hostname,
	                          @QueryParam("osName"     ) String  osName,
	                          @QueryParam("javaVersion") String  javaVersion,
	                          @QueryParam("heapSize"   ) Long    heapSize,
	                          @Context                   UriInfo uriInfo)
	{
		SystemData s = inventory.getSystem(hostname);
		if (s != null)
		{
			return fail(hostname + " already exists.");
		}
		inventory.add(hostname, osName, javaVersion, heapSize);
		return created(hostname, uriInfo);
	}

	@PUT
	@Path("/{hostname}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@RolesAllowed({ "admin", "user" })
	@APIResponses(value = { @APIResponse(responseCode = HttpResponseConstants._OK_,
	                                     description  = "successfully updated system"),
	                        @APIResponse(responseCode = HttpResponseConstants._BAD_REQUEST_,
	                                     description  =   "unable to update "
	                                                    + "because the system does not exist in the inventory.")})
	@Parameters  (value = { @Parameter(name        = "hostname",
	                                   in          = ParameterIn.PATH, // really Path, not Query?
	                                   description = "hostname of the system",
	                                   required    = true,
	                                   example     = "localhost",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name = "osName",
	                                   in          = ParameterIn.QUERY,
	                                   description = "operating system of the system",
	                                   required    = true,
	                                   example     = "linux",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name        = "javaVersion",
	                                   in          = ParameterIn.QUERY,
	                                   description = "java version of the system",
	                                   required    = true,
	                                   example     = "11",
	                                   schema      = @Schema(type = SchemaType.STRING)),
	                        @Parameter(name        = "heapSize",
	                                   in          = ParameterIn.QUERY,
	                                   description = "heap size of the system",
	                                   required    = true,
	                                   example     = "1048576",
	                                   schema      = @Schema(type = SchemaType.NUMBER))})
	@Operation    (summary     = "update system",
	               description = "update a system and its data on the inventory",
	               operationId = "updateSystem")
	public Response updateSystem(@PathParam ("hostname"   ) String hostname, // really PathParam, not QueryParam?
	                             @QueryParam("osName"     ) String osName,
	                             @QueryParam("javaVersion") String javaVersion,
	                             @QueryParam("heapSize"   ) Long heapSize)
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

	@DELETE
	@Path("/{hostname}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@RolesAllowed({ "admin" })
	@APIResponses(value = { @APIResponse(responseCode = HttpResponseConstants._OK_,
	                                     description  = "successfully deleted the system from inventory"),
	                        @APIResponse(responseCode = HttpResponseConstants._BAD_REQUEST_,
	                                     description  =   "unable to delete "
	                                                    + "because the system does not exist in the inventory")})
	@Parameter   (name        = "hostname",
	              in          = ParameterIn.PATH,
	              description = "hostname of the system",
	              required    = true,
	              example     = "localhost",
	              schema      = @Schema(type = SchemaType.STRING))
	@Operation   (summary     = "remove system",
	              description = "removes a system from the inventory.",
	              operationId = "removeSystem")
	public Response removeSystem(@PathParam("hostname") String hostname)
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

	@POST
	@Path("/client/{hostname}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@APIResponses(value = { @APIResponse(responseCode = HttpResponseConstants._OK_,
	                                     description  = "successfully added system client"),
	                        @APIResponse(responseCode = HttpResponseConstants._BAD_REQUEST_,
	                                     description = "unable to add system client") })
	@Parameter   (name        = "hostname",
	              in          = ParameterIn.PATH,
	              description = "hostname of the system",
	              required    = true,
	              example     = "localhost",
	              schema      = @Schema(type = SchemaType.STRING))
	@Operation(summary     = "add system client",
	           description = "add a system client",
	           operationId = "addSystemClient")
	public Response addSystemClient(@PathParam("hostname") String hostname)
	{
		SystemData s = inventory.getSystem(hostname);

		if (s != null)
		{
			return fail(hostname + " already exists.");
		}

		SystemClient customRestClient = null;

		try
		{
			customRestClient = getSystemClient(hostname);
		}
		catch (Exception e)
		{
			return fail("Failed to create the client " + hostname + ".");
		}

		String authHeader = "Bearer " + jwt.getRawToken();
		try
		{
			String osName = customRestClient.getProperty(authHeader, "os.name");
			String javaVer = customRestClient.getProperty(authHeader, "java.version");
			Long heapSize = customRestClient.getHeapSize(authHeader);
			inventory.add(hostname, osName, javaVer, heapSize);
		}
		catch (Exception e)
		{
			return fail("Failed to reach the client " + hostname + ".");
		}

		return success(hostname + " was added.");
	}

	private SystemClient getSystemClient(String hostname) throws Exception
	{
		String customURIString = "https://" + hostname + ":" + clientPort + "/system";
		URI customURI = URI.create(customURIString);
		return
				RestClientBuilder.newBuilder()
				                 .baseUri(customURI)
				                 .register(UnknownUriExceptionMapper.class)
				                 .build(SystemClient.class);
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
	}

	private Response fail(String message)
	{
		return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\" : \"" + message + "\" }").build();
	}
}