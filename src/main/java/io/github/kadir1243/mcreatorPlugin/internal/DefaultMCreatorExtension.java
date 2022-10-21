package io.github.kadir1243.mcreatorPlugin.internal;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class DefaultMCreatorExtension implements MCreatorExtension {
    private final Property<String> mcreatorVersion;

    public DefaultMCreatorExtension(ObjectFactory factory) {
        mcreatorVersion = factory.property(String.class);
        mcreatorVersion.convention("2022.2");
    }

    @Override
    public Property<String> getMCreatorVersion() {
        return mcreatorVersion;
    }
}
