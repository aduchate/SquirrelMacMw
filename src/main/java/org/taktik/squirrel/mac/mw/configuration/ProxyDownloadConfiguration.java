package org.taktik.squirrel.mac.mw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.taktik.squirrel.mac.mw.service.NexusQuerierService;
import org.taktik.squirrel.mac.mw.servlet.ProxyAsyncDownloadServlet;

@Configuration
public class ProxyDownloadConfiguration {
	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private NexusQuerierService nexusQuerierService;

	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyAsyncDownloadServlet(nexusQuerierService), "/d/*");

		servletRegistrationBean.addInitParameter("timeout","3600000");

		return servletRegistrationBean;
	}
}
