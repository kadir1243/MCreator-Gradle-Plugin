package io.github.kadir1243.mcreatorPlugin;

import org.gradle.api.provider.Property;

public interface MCreatorExtension {
    Property<String> getMCreatorVersion();

    Property<String> getMCreatorMainClass();

    Property<Boolean> forceDownload();

    Property<Boolean> logProgressOfDownload();
}
