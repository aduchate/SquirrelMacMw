package org.taktik.squirrel.mac.mw.domain;

public class MavenPackage {
	private Boolean presentLocally;
	private String groupId;
	private String artifactId;
	private String version;
	private String sha1;
	private String extension;
	private Boolean snapshot;
	private String snapshotBuildNumber;

	public Boolean getPresentLocally() {
		return presentLocally;
	}

	public void setPresentLocally(Boolean presentLocally) {
		this.presentLocally = presentLocally;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Boolean getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(Boolean snapshot) {
		this.snapshot = snapshot;
	}

	public String getSnapshotBuildNumber() {
		return snapshotBuildNumber;
	}

	public void setSnapshotBuildNumber(String snapshotBuildNumber) {
		this.snapshotBuildNumber = snapshotBuildNumber;
	}
}
