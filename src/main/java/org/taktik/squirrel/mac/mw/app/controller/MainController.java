package org.taktik.squirrel.mac.mw.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.taktik.squirrel.mac.mw.app.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.app.domain.UpdateResponse;
import org.taktik.squirrel.mac.mw.app.exception.NoContentAvailableException;
import org.taktik.squirrel.mac.mw.app.service.NexusQuerierService;

@RestController
public class MainController {
	@Autowired
	private NexusQuerierService nexusQuerierService;

	@RequestMapping("/")
	public String index() {
		return ",;;:;,\n" +
				"   ;;;;;\n" +
				"  ,:;;:;    ,'=.\n" +
				"  ;:;:;' .=\" ,'_\\\n" +
				"  ':;:;,/  ,__:=@\n" +
				"   ';;:;  =./)_\n" +
				" jgs `\"=\\_  )_\"`\n" +
				"          ``'\"`";
	}

	@RequestMapping("/{groupId}/{artifactId}")
	public @ResponseBody
	UpdateResponse check(@PathVariable("groupId") String groupId, @PathVariable("artifactId") String artifactId, @RequestParam("version") String version) throws NoContentAvailableException {
		MavenPackage latestVersion = nexusQuerierService.getLatestVersion(groupId, artifactId);

		if (latestVersion.getVersion().equals(version) || version.compareTo(latestVersion.getVersion()) > 0) {
			throw new NoContentAvailableException();
		}

		return new UpdateResponse(nexusQuerierService.getUri(latestVersion));
	}
}
