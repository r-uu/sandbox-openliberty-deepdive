package io.openliberty.deepdive.rest.health;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck
{
	@Override
	public HealthCheckResponse call()
	{
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

		long memUsed = memBean.getHeapMemoryUsage().getUsed();
		long memMax  = memBean.getHeapMemoryUsage().getMax();

		return HealthCheckResponse.named("liveness check").status(memUsed < memMax * 0.9).build();
	}
}