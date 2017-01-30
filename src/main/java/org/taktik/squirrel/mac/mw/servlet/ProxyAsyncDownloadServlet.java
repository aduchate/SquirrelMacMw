package org.taktik.squirrel.mac.mw.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.taktik.squirrel.mac.mw.service.NexusQuerierService;

public class ProxyAsyncDownloadServlet extends AsyncProxyServlet {
	private final NexusQuerierService nexusQuerierService;
	private Pattern uriParser = Pattern.compile("/d/([a-zA-Z0-9._-]+)/([a-zA-Z0-9._-]+)");

	public ProxyAsyncDownloadServlet(NexusQuerierService nexusQuerierService) {
		this.nexusQuerierService = nexusQuerierService;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		Matcher matcher = uriParser.matcher(request.getRequestURI());
		String version = request.getParameter("version");
		if (matcher.matches() && version != null) {
			String groupId = matcher.group(1);
			String artifactId = matcher.group(2);

			try {
				return nexusQuerierService.getUri(groupId, artifactId, version).toString();
			} catch (ArtifactResolutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
		proxyRequest.getHeaders().add(new HttpField(HttpHeaders.AUTHORIZATION, nexusQuerierService.getAuthHeader()));
		proxyRequest.getHeaders().remove(HttpHeader.ACCEPT);
		super.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
	}

	@Override
	protected HttpClient newHttpClient() {
		SslContextFactory sslContextFactory = new SslContextFactory();
		return new HttpClient(sslContextFactory);
	}

}
