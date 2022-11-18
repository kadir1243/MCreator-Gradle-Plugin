package io.github.kadir1243.mcreatorPlugin.internal;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class DefaultMCreatorExtension implements MCreatorExtension {
    private final Property<String> mcreatorVersion;
    private final Property<String> mcreatorMainClass;
    private final Property<Boolean> forceDownload;
    private final Property<Boolean> logProgressOfDownload;

    public DefaultMCreatorExtension(ObjectFactory factory) {
        mcreatorVersion = factory.property(String.class);
        mcreatorVersion.convention("2022.2.34517");
        mcreatorMainClass = factory.property(String.class);
        mcreatorMainClass.convention("net.mcreator.Launcher");
        forceDownload = factory.property(Boolean.class);
        forceDownload.convention(false);
        logProgressOfDownload = factory.property(Boolean.class);
        logProgressOfDownload.convention(true);
    }

    @Override
    public Property<String> getMCreatorVersion() {
        return mcreatorVersion;
    }

    @Override
    public Property<String> getMCreatorMainClass() {
        return mcreatorMainClass;
    }

    @Override
    public Property<Boolean> forceDownload() {
        return forceDownload;
    }

    @Override
    public Property<Boolean> logProgressOfDownload() {
        return logProgressOfDownload;
    }
}
