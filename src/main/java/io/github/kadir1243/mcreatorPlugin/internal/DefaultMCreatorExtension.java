package io.github.kadir1243.mcreatorPlugin.internal;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class DefaultMCreatorExtension implements MCreatorExtension {
    private final Property<String> mcreatorVersion;
    private final Property<String> mcreatorMainClass;

    public DefaultMCreatorExtension(ObjectFactory factory) {
        mcreatorVersion = factory.property(String.class);
        mcreatorVersion.convention("2022.2.34517");
        mcreatorMainClass = factory.property(String.class);
        mcreatorMainClass.convention("net.mcreator.Launcher");
    }

    @Override
    public Property<String> getMCreatorVersion() {
        return mcreatorVersion;
    }

    @Override
    public Property<String> getMCreatorMainClass() {
        return mcreatorMainClass;
    }
}
