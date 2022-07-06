package io.openliberty.deepdive.rest.health;

import java.lang.management.ManagementFactory;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

import com.sun.management.OperatingSystemMXBean;

import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class StartupCheck implements HealthCheck
{
	@Override
	public HealthCheckResponse call()
	{
		OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		double cpuLoad = bean.getCpuLoad();
		return HealthCheckResponse.named("startup check").status(cpuLoad < 0.95).build();
	}
}