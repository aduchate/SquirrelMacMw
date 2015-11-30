package org.taktik.squirrel.mac.mw.app.service.impl;

import java.net.URI;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.taktik.squirrel.mac.mw.app.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.app.domain.NexusResponse;
import org.taktik.squirrel.mac.mw.app.service.NexusQuerierService;

@Service
public class NexusQuerierServiceImpl implements NexusQuerierService {
	@Value("${nexusServer}")
	private String nexusServer;

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;

	@Value("${repository}")
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
	public URI getUri(MavenPackage p) {
		DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();

		String url = nexusServer + "/service/local/artifact/maven/content?r={repository}&g={groupId}&a={artifactId}&v={version}&p=zip";
		return defaultUriTemplateHandler.expand(url, repository, p.getGroupId(), p.getArtifactId(), p.getVersion());
	}
}
