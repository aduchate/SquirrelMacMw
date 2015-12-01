package org.taktik.squirrel.mac.mw.service;

import java.net.URI;

import org.taktik.squirrel.mac.mw.domain.MavenPackage;

public interface NexusQuerierService {
	MavenPackage getLatestVersion(String groupId, String artifactId);
	URI getUri(String groupId, String artifactId, String version);
	String getAuthHeader();
}
