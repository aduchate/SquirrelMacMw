package org.taktik.squirrel.mac.mw.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.taktik.squirrel.mac.mw.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.domain.NexusResponse;
import org.taktik.squirrel.mac.mw.service.NexusQuerierService;

//@Service
public class NexusV1QuerierServiceImpl implements NexusQuerierService {
	@Value("${mw.maven.server}")
	private String nexusServer;

	@Value("${mw.maven.username}")
	private String username;

	@Value("${mw.maven.password}")
	private String password;

	@Value("${mw.maven.repository}")
	private String repository;

	@Override
	public MavenPackage getLatestVersion(String groupId, String artifactId) {
		String plainCreds = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		String base64Creds = java.util.Base64.getEncoder().encodeToString(plainCredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		String url = nexusServer + "/service/local/artifact/maven/resolve?r={repository}&g={groupId}&a={artifactId}&v=LATEST&p=zip";
		RestTemplate restTemplate = new RestTemplate();

		// Add the String message converter
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		// Make the HTTP GET request, marshaling the response to a String
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class, repository, groupId, artifactId);

		XStream xStream = new XStream();
		xStream.processAnnotations(NexusResponse.class);
		xStream.omitField(MavenPackage.class, "snapshotTimeStamp");
		xStream.omitField(MavenPackage.class, "repositoryPath");

		NexusResponse r = (NexusResponse) xStream.fromXML(response.getBody());

		if (r != null && r.getData() != null) {
			return r.getData();
		}

		return null;
	}

	@Override
	public URI getUri(String groupId, String artifactId, String version) {
		DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();

		String url = nexusServer + "/service/local/artifact/maven/content?r={repository}&g={groupId}&a={artifactId}&v={version}&p=zip";
		URI expand = defaultUriTemplateHandler.expand(url, repository, groupId, artifactId, version);

		return expand;
	}

	@Override
	public String getAuthHeader() {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + new String(encodedAuth);
	}


}
