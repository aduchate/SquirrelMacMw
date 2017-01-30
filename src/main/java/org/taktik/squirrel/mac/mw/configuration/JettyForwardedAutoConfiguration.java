package org.taktik.squirrel.mac.mw.configuration;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyForwardedAutoConfiguration implements EmbeddedServletContainerCustomizer {
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		JettyEmbeddedServletContainerFactory containerFactory = (JettyEmbeddedServletContainerFactory) container;
		containerFactory.addServerCustomizers(server -> {
			for (Connector connector : server.getConnectors()) {
				ConnectionFactory connectionFactory = connector.getDefaultConnectionFactory();
				if (connectionFactory instanceof HttpConnectionFactory) {
					HttpConnectionFactory defaultConnectionFactory = (HttpConnectionFactory) connectionFactory;
					HttpConfiguration httpConfiguration = defaultConnectionFactory.getHttpConfiguration();
					httpConfiguration.addCustomizer(new ForwardedRequestCustomizer());
				}
			}
		});
	}
}