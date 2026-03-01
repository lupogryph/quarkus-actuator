package ch.lupogryph.quarkus.actuator.deployment;

import ch.lupogryph.quarkus.actuator.admin.ActuatorConfig;
import ch.lupogryph.quarkus.actuator.admin.ActuatorRecorder;
import ch.lupogryph.quarkus.actuator.admin.InstanceService;
import ch.lupogryph.quarkus.actuator.admin.InstancesRequest;
import ch.lupogryph.quarkus.actuator.admin.InstancesResponse;
import ch.lupogryph.quarkus.actuator.admin.SpringBootAdminApi;
import ch.lupogryph.quarkus.actuator.beans.BeansRecorder;
import ch.lupogryph.quarkus.actuator.beans.BeansService;
import ch.lupogryph.quarkus.actuator.caches.CachesRecorder;
import ch.lupogryph.quarkus.actuator.caches.CachesService;
import ch.lupogryph.quarkus.actuator.configs.ConfigsRecorder;
import ch.lupogryph.quarkus.actuator.configs.ConfigsService;
import ch.lupogryph.quarkus.actuator.env.EnvRecorder;
import ch.lupogryph.quarkus.actuator.env.EnvService;
import ch.lupogryph.quarkus.actuator.heapdump.HeapDumpRecorder;
import ch.lupogryph.quarkus.actuator.loggers.Groups;
import ch.lupogryph.quarkus.actuator.loggers.Loggers;
import ch.lupogryph.quarkus.actuator.loggers.LoggersRecorder;
import ch.lupogryph.quarkus.actuator.loggers.LoggersRequest;
import ch.lupogryph.quarkus.actuator.loggers.LoggersResponse;
import ch.lupogryph.quarkus.actuator.metrics.Metric;
import ch.lupogryph.quarkus.actuator.metrics.MetricsRecorder;
import ch.lupogryph.quarkus.actuator.metrics.MetricsService;
import ch.lupogryph.quarkus.actuator.metrics.Names;
import ch.lupogryph.quarkus.actuator.threaddump.ThreadDump;
import ch.lupogryph.quarkus.actuator.threaddump.ThreadDumpRecorder;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
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
                LoggersRequest.class,
                LoggersResponse.class,
                Loggers.class,
                Groups.class,
                Names.class,
                Metric.class,
                Metric.Measurement.class,
                Metric.AvailableTags.class,
                ThreadDump.class)
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
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(EnvService.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(MetricsService.class));
        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(CachesService.class));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void registerManagementEndpoints(
            CurateOutcomeBuildItem outcome,
            BuildProducer<RouteBuildItem> routes,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            ActuatorRecorder actuatorRecorder,
            LoggersRecorder loggersRecorder,
            BeansRecorder beansRecorder,
            ConfigsRecorder configsRecorder,
            MetricsRecorder metricsRecorder,
            EnvRecorder envRecorder,
            CachesRecorder cachesRecorder,
            ThreadDumpRecorder threadDumpRecorder,
            HeapDumpRecorder heapDumpRecorder,
            ManagementInterfaceBuildTimeConfig managementInterfaceBuildTimeConfig,
            ManagementConfig managementConfig,
            ActuatorConfig actuatorConfig) {
        if (managementInterfaceBuildTimeConfig.enabled()) {
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("/q/actuator")
                    .handler(actuatorRecorder.actuator(actuatorConfig, managementConfig,
                            hasDependency(outcome, "quarkus-cache"), hasDependency(outcome, "quarkus-quartz")))
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/loggers")
                    .handler(loggersRecorder.loggers())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/loggers/:name")
                    .handler(loggersRecorder.namedLoggers())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/beans")
                    .handler(beansRecorder.beans())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/configprops")
                    .handler(configsRecorder.configs())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/metrics")
                    .handler(metricsRecorder.names())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/metrics/:name")
                    .handler(metricsRecorder.metric())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/env")
                    .handler(envRecorder.env())
                    .build());
            if (hasDependency(outcome, "quarkus-cache")) {
                routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                        .management()
                        .route("actuator/caches")
                        .handler(cachesRecorder.allCaches())
                        .build());
                routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                        .management()
                        .route("actuator/caches/:name")
                        .handler(cachesRecorder.cache())
                        .build());
            }
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/threaddump")
                    .handler(threadDumpRecorder.dump())
                    .build());
            routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                    .management()
                    .route("actuator/heapdump")
                    .handler(heapDumpRecorder.dump())
                    .build());
        }
    }

    private boolean hasDependency(CurateOutcomeBuildItem outcome, String artifact) {
        return outcome.getApplicationModel().getDependencies().stream().anyMatch(dep -> dep.getArtifactId().equals(artifact));
    }

}
