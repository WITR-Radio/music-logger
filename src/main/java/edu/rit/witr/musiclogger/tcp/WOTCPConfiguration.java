package edu.rit.witr.musiclogger.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;

/**
 * WideOrbit TCP Configuration
 */
@Configuration
@EnableIntegration
public class WOTCPConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(WOTCPConfiguration.class);

    @Bean
    public TcpReceivingChannelAdapter demoTcpReceivingChannelAdapter() {
        if (!"true".equalsIgnoreCase(System.getenv("WIDEORBIT_ENABLE"))) {
            return null;
        }

        LOGGER.info("Listening to WideOrbit TCP messages");

        var adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(prepareDemoTcpNetClientConnectionFactory());
        adapter.setClientMode(true);
        adapter.setOutputChannelName("wideorbit");
        return adapter;
    }

    private TcpNetClientConnectionFactory prepareDemoTcpNetClientConnectionFactory() {
        var factory = new TcpNetClientConnectionFactory(System.getenv("WIDEORBIT_HOST"), Integer.parseInt(System.getenv("WIDEORBIT_PORT")));
        factory.setDeserializer(new ByteArrayStringTerminatorSerializer("</nowplaying>"));
        return factory;
    }

}
