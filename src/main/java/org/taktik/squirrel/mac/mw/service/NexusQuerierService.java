package org.taktik.squirrel.mac.mw.service;

import java.net.URI;

import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.taktik.squirrel.mac.mw.domain.MavenPackage;

public interface NexusQuerierService {
	MavenPackage getLatestVersion(String groupId, String artifactId) throws VersionRangeResolutionException;
	URI getUri(String groupId, String artifactId, String version) throws ArtifactResolutionException;
	String getAuthHeader();
}
