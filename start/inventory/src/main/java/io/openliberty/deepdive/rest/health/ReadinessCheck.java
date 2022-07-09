package io.openliberty.deepdive.rest.health;

import java.net.Socket;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck
{
	@Inject
	@ConfigProperty(name = "postgres.host")
	private String host;

	@Inject
	@ConfigProperty(name = "postgres.port")
	private int port;

	@Override
	public HealthCheckResponse call()
	{
		HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Readiness Check");

		try
		{
			Socket socket = new Socket(host, port);
			socket.close();
			responseBuilder.up();
		}
		catch (Exception e)
		{
			responseBuilder.down();
		}
		return responseBuilder.build();
	}
}