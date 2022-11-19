package io.github.kadir1243.mcreatorPlugin.task;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

@CacheableTask
public class RunMCreatorTask extends DefaultTask {
    @Internal
    private final MCreatorExtension extension = getProject().getExtensions().findByType(MCreatorExtension.class);
    private final Set<File> jarOutputs = dependsOn("jar").getOutputs().getFiles().getFiles();
    @InputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
    private final RegularFileProperty path2MCreator = getInjectedObjectFactory().fileProperty();
    @Input
    private final Property<String> version = getInjectedObjectFactory().property(String.class);

    @TaskAction
    public void doTask() {
        File file = path2MCreator.getAsFile().get();
        File[] files = file.listFiles();
        File path = file;
        if (files == null || files.length == 0) {
            throw new IllegalStateException("is it downloaded right files ?");
        }
        if (files.length == 1) {
            path = files[0];
        }
        getLogger().debug("mcreator path : " + path.getPath());
        File libraries = new File(path, "lib");
        getLogger().debug("mcreator library path : " + libraries.getPath());
        File finalPath = path;
        getInjectedExecOperations().javaexec(execSpec -> {
            execSpec.bootstrapClasspath(getInjectedObjectFactory().fileTree().from(libraries).filter(f -> !f.getName().equals("mcreator.jar")));
            execSpec.classpath(getInjectedObjectFactory().fileTree().from(new File(libraries, "mcreator.jar")));
            execSpec.getMainClass().set(extension.getMCreatorMainClass());
            execSpec.jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED");
            execSpec.environment("MCREATOR_PLUGINS_FOLDER", jarOutputs);
            execSpec.workingDir(finalPath);
        });
    }

    @Inject
    protected ObjectFactory getInjectedObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ExecOperations getInjectedExecOperations() {
        throw new UnsupportedOperationException();
    }

    public RegularFileProperty getPath2MCreator() {
        return path2MCreator;
    }

    public Property<String> getVersion() {
        return version;
    }
}
