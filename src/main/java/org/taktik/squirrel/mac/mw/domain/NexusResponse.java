package org.taktik.squirrel.mac.mw.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("artifact-resolution")
public class NexusResponse {
	private MavenPackage data;

	public MavenPackage getData() {
		return data;
	}

	public void setData(MavenPackage data) {
		this.data = data;
	}
}
