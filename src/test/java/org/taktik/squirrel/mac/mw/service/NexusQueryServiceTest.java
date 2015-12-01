package org.taktik.squirrel.mac.mw.service;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.taktik.squirrel.mac.mw.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.domain.NexusResponse;

public class NexusQueryServiceTest {
	@Test
	public void testXStramUnmarshalling() throws Exception {
		String testXml = "<artifact-resolution>\n" +
				"  <data>\n" +
				"    <presentLocally>true</presentLocally>\n" +
				"    <groupId>org.taktik.icure</groupId>\n" +
				"    <artifactId>icure-macos-app</artifactId>\n" +
				"    <version>4.0.0-nyl9ft_80cfc38</version>\n" +
				"    <extension>zip</extension>\n" +
				"    <snapshot>false</snapshot>\n" +
				"    <snapshotBuildNumber>0</snapshotBuildNumber>\n" +
				"    <snapshotTimeStamp>0</snapshotTimeStamp>\n" +
				"    <sha1>c4bf69698a18a39cdd8cf37d515f7711aa90f2ee</sha1>\n" +
				"    <repositoryPath>/org/taktik/icure/icure-macos-app/4.0.0-nyl9ft_80cfc38/icure-macos-app-4.0.0-nyl9ft_80cfc38.zip</repositoryPath>\n" +
				"  </data>\n" +
				"</artifact-resolution>";

		XStream xStream = new XStream();
		xStream.processAnnotations(NexusResponse.class);
		xStream.omitField(MavenPackage.class, "snapshotTimeStamp");
		xStream.omitField(MavenPackage.class, "repositoryPath");
		NexusResponse r = (NexusResponse) xStream.fromXML(testXml, new NexusResponse());

		Assert.assertEquals("icure-macos-app",r.getData().getArtifactId());
	}

	@Test
	public void testXStramMarshalling() throws Exception {
		String testXml = "<artifact-resolution>\n" +
				"  <data>\n" +
				"    <presentLocally>true</presentLocally>\n" +
				"    <groupId>org.taktik.icure</groupId>\n" +
				"    <artifactId>icure-macos-app</artifactId>\n" +
				"    <version>4.0.0-nyl9ft_80cfc38</version>\n" +
				"    <extension>zip</extension>\n" +
				"    <snapshot>false</snapshot>\n" +
				"    <snapshotBuildNumber>0</snapshotBuildNumber>\n" +
				"    <snapshotTimeStamp>0</snapshotTimeStamp>\n" +
				"    <sha1>c4bf69698a18a39cdd8cf37d515f7711aa90f2ee</sha1>\n" +
				"    <repositoryPath>/org/taktik/icure/icure-macos-app/4.0.0-nyl9ft_80cfc38/icure-macos-app-4.0.0-nyl9ft_80cfc38.zip</repositoryPath>\n" +
				"  </data>\n" +
				"</artifact-resolution>";

		XStream xStream = new XStream();
		xStream.processAnnotations(NexusResponse.class);

		NexusResponse r = new NexusResponse();
		r.setData(new MavenPackage());

		r.getData().setArtifactId("icure-macos-app");
		r.getData().setGroupId("org.taktik.icure");
		r.getData().setVersion("4.0.0-nyl9ft_80cfc38");

		String s = xStream.toXML(r);

		Assert.assertEquals("<artifact-resolution>\n" +
				"  <data>\n" +
				"    <groupId>org.taktik.icure</groupId>\n" +
				"    <artifactId>icure-macos-app</artifactId>\n" +
				"    <version>4.0.0-nyl9ft_80cfc38</version>\n" +
				"  </data>\n" +
				"</artifact-resolution>",s);
	}


}
