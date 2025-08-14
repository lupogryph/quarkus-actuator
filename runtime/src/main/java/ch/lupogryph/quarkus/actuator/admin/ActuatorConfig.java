package ch.lupogryph.quarkus.actuator.admin;

import static io.quarkus.runtime.annotations.ConfigPhase.BUILD_AND_RUN_TIME_FIXED;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.actuator")
@ConfigRoot(phase = BUILD_AND_RUN_TIME_FIXED)
public interface ActuatorConfig {

    /**
     * The local address to communicate to spring-boot-admin.
     */
    @WithName("local-address")
    Optional<String> localAddress();

    default String getLocalAddress() throws UnknownHostException {
        return localAddress().orElse(InetAddress.getLocalHost().getHostAddress());
    }
}
