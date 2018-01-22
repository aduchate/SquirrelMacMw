package org.taktik.squirrel.mac.mw.service.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.version.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.taktik.squirrel.mac.mw.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.service.QuerierService;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.sql.DriverManager.println;

@Primary
@Service
public class MavenQuerierServiceImpl implements QuerierService {
	@Value("${mw.maven.username}")
	private String username;

	@Value("${mw.maven.password}")
	private String password;

	@Value("${mw.maven.repository}")
	private String repository;

	@Value("${mw.squirrel.ramp.up.days:0}")
	private int rampUpDays;

	private final static String OPEN_RANGE = "[0,)";
	private Map<String,Instant> versionsMap = new HashMap<>();

	@Override
	public MavenPackage getLatestVersion(String groupId, String artifactId, String ip) throws VersionRangeResolutionException {
		RepositorySystem system = getRepositorySystem();
		DefaultRepositorySystemSession session = getDefaultRepositorySystemSession(system);

		List<RemoteRepository> remotes = getRemoteRepositories();

		// create artifact first to verify artifact coordinates
		Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":zip:" + OPEN_RANGE);


		VersionRangeRequest rangeRequest = new VersionRangeRequest();
		rangeRequest.setArtifact(artifact);
		rangeRequest.setRepositories(remotes);


		VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

		List<Version> versions = rangeResult.getVersions();
		List<Version> sortedVersions = versions.stream().sorted(Comparator.reverseOrder()).filter(v -> !v.toString().endsWith("-SNAPSHOT")).collect(Collectors.toList());

		Version highestVersion = sortedVersions.size()>0 ? sortedVersions.get(0) : null;
		if (highestVersion == null) {
			throw new IllegalArgumentException("Unknown artifact");
		}

		Version secondHighestVersion = sortedVersions.size()>1 ? sortedVersions.get(1) : null;

		println("Highest version " + highestVersion + " from repository " + rangeResult.getRepository(highestVersion));

		if (rampUpDays>0 && secondHighestVersion != null) {
			Instant firstInstant = versionsMap.computeIfAbsent(highestVersion.toString(), k -> Instant.now());
			double rampUpRatio = (Instant.now().toEpochMilli()-firstInstant.toEpochMilli())/((double)(rampUpDays*3600L*24*1000));
			String lastIpDigit = ip.replaceAll(".*[:.](.+)","$1");
			boolean isIpv6 = ip.contains(":");
			int lastIpVal = Integer.valueOf(lastIpDigit, isIpv6 ?16:10);
			if (!isIpv6) {
				lastIpVal*=256;
			}
			if (rampUpRatio*65536<lastIpVal) {
				return new MavenPackage(groupId, artifactId, secondHighestVersion.toString());
			}
		}

		return new MavenPackage(groupId, artifactId, highestVersion.toString());
	}

	private DefaultRepositorySystemSession getDefaultRepositorySystemSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository("target/local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		session.setTransferListener(new AbstractTransferListener() {
		});
		session.setRepositoryListener(new AbstractRepositoryListener() {
		});
		return session;
	}

	private RepositorySystem getRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				exception.printStackTrace();
			}
		});

		return locator.getService(RepositorySystem.class);
	}

	private List<RemoteRepository> getRemoteRepositories() {
		RemoteRepository.Builder builder = new RemoteRepository.Builder("taktik", "default", getRepositoryUrl());

		Authentication auth = new AuthenticationBuilder().addUsername(username).addPassword(password).build();
		builder.setAuthentication(auth);

		RemoteRepository remote = builder.build();
		return Collections.singletonList(remote);
	}

	private String getRepositoryUrl() {
		return repository + (repository.endsWith("/")?"":"/");
	}

	@Override
	public URI getUri(String groupId, String artifactId, String version) throws ArtifactResolutionException {
		return this.getUri(groupId, artifactId, version, "zip");
	}

	public URI getUri(String groupId, String artifactId, String version, String extension) throws ArtifactResolutionException {
		DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();
		String url = getRepositoryUrl() +  groupId.replaceAll("\\.", "/") + "/" + artifactId.replaceAll("\\.", "/") + "/" + version + "/" + artifactId + "-" + version + "." + extension;
		return defaultUriTemplateHandler.expand(url, repository, groupId, artifactId, version);
	}

	@Override
	public String getAuthHeader() {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + new String(encodedAuth);
	}

}
