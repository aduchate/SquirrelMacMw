package org.taktik.squirrel.mac.mw.app.service;

import java.net.URI;

import org.taktik.squirrel.mac.mw.app.domain.MavenPackage;

public interface NexusQuerierService {
	MavenPackage getLatestVersion(String groupId, String artifactId);

	URI getUri(MavenPackage p);
}
