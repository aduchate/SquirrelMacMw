package org.taktik.squirrel.mac.mw.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.taktik.squirrel.mac.mw.domain.MavenPackage;
import org.taktik.squirrel.mac.mw.domain.UpdateResponse;
import org.taktik.squirrel.mac.mw.exception.NoContentAvailableException;
import org.taktik.squirrel.mac.mw.service.QuerierService;

@RestController
public class MainController {
	@Autowired
	private QuerierService querierService;

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
	UpdateResponse check(@PathVariable("groupId") String groupId, @PathVariable("artifactId") String artifactId, @RequestParam(value="version", required = false) String version,
						 HttpServletRequest request,
						 HttpServletResponse response) throws NoContentAvailableException, VersionRangeResolutionException {

		if (version==null) { version = "0.0.0"; }
		MavenPackage latestVersion = querierService.getLatestVersion(groupId, artifactId, request.getRemoteHost());
		if (latestVersion.getVersion().equals(version) || version.compareTo(latestVersion.getVersion()) > 0) {
			throw new NoContentAvailableException();
		}

		DefaultUriTemplateHandler defaultUriTemplateHandler = new DefaultUriTemplateHandler();
		String url = request.getScheme() + "://" + request.getHeader("HOST") + "/d/{groupId}/{artifactId}?version={version}";

		return new UpdateResponse(defaultUriTemplateHandler.expand(url, latestVersion.getGroupId(), latestVersion.getArtifactId(), latestVersion.getVersion()));
	}
}
