package ch.lupogryph.quarkus.actuator.deployment;

import ch.lupogryph.quarkus.actuator.admin.ActuatorConfig;
import ch.lupogryph.quarkus.actuator.admin.ActuatorRecorder;
import ch.lupogryph.quarkus.actuator.admin.InstanceService;
import ch.lupogryph.quarkus.actuator.admin.InstancesRequest;
import ch.lupogryph.quarkus.actuator.admin.InstancesResponse;
import ch.lupogryph.quarkus.actuator.admin.Metadata;
import ch.lupogryph.quarkus.actuator.admin.SpringBootAdminApi;
import ch.lupogryph.quarkus.actuator.beans.BeansRecorder;
import ch.lupogryph.quarkus.actuator.beans.BeansService;
import ch.lupogryph.quarkus.actuator.configs.ConfigsRecorder;
import ch.lupogryph.quarkus.actuator.configs.ConfigsService;
import ch.lupogryph.quarkus.actuator.loggers.Groups;
import ch.lupogryph.quarkus.actuator.loggers.Loggers;
import ch.lupogryph.quarkus.actuator.loggers.LoggersRecorder;
import ch.lupogryph.quarkus.actuator.loggers.LoggersRequest;
import ch.lupogryph.quarkus.actuator.loggers.LoggersResponse;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementConfig;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;

class ActuatorProcessor {

    private static final String FEATURE = "quarkus-actuator";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void reflection(BuildProducer<ReflectiveClassBuildItem> producer) {
        producer.produce(ReflectiveClassBuildItem.builder(
                InstancesRequest.class,
                InstancesResponse.class,
                Metadata.class,
                LoggersRequest.class,
                LoggersResponse.class,
                Loggers.class,
                Groups.class)
                .constructors()
                .methods()
                .build());
    }

    @BuildStep
    AdditionalIndexedClassesBuildItem addAdditionalClasses() {
        return new AdditionalIndexedClassesBuildItem(SpringBootAdminApi.class.getName());
    }

    @BuildStep
    void addBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(InstanceService.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(BeansService.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(ConfigsService.class));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void registerManagementEndpoints(
            BuildProducer<RouteBuildItem> routes,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            ActuatorRecorder actuatorRecorder,
            LoggersRecorder loggersRecorder,
            BeansRecorder beansRecorder,
            ConfigsRecorder configsRecorder,
            ManagementInterfaceBuildTimeConfig managementInterfaceBuildTimeConfig,
            ManagementConfig managementConfig,
            ActuatorConfig actuatorConfig) {
        if (managementInterfaceBuildTimeConfig.enabled()) {
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("/q")
                    .handler(actuatorRecorder.actuator(actuatorConfig, managementConfig))
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("loggers")
                    .handler(loggersRecorder.loggers())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("loggers/:name")
                    .handler(loggersRecorder.namedLoggers())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("beans")
                    .handler(beansRecorder.beans())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("configprops")
                    .handler(configsRecorder.configs())
                    .build());
        }
    }

}
