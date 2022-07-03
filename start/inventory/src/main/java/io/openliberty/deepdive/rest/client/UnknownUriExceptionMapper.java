package io.openliberty.deepdive.rest.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import io.openliberty.deepdive.rest.HttpResponseConstants;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

public class UnknownUriExceptionMapper implements ResponseExceptionMapper<UnknownUriException>
{
	private static final Logger LOG = Logger.getLogger(UnknownUriExceptionMapper.class.getName());

	@Override
	public boolean handles(int status, MultivaluedMap<String, Object> headers)
	{
		LOG.log(Level.INFO, "status = {0}", status);
		return status == HttpResponseConstants.NOT_FOUND;
	}

	@Override
	public UnknownUriException toThrowable(Response response)
	{
		return new UnknownUriException();
	}
}