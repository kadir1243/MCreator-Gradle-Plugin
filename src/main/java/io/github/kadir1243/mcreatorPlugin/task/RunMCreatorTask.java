package io.github.kadir1243.mcreatorPlugin.task;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.process.internal.ExecActionFactory;
import org.gradle.process.internal.JavaExecAction;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

@CacheableTask
public class RunMCreatorTask extends DefaultTask {
    private final MCreatorExtension extension = getProject().getExtensions().findByType(MCreatorExtension.class);
    private final Set<File> jarOutputs;
    @InputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
    private final RegularFileProperty path2MCreator = getInjectedObjectFactory().fileProperty();
    @Input
    private final Property<String> version = getInjectedObjectFactory().property(String.class);

    public RunMCreatorTask() {
        jarOutputs = dependsOn("jar").getOutputs().getFiles().getFiles();
    }

    @TaskAction
    public void doTask() {
        File file = path2MCreator.get().getAsFile();
        File path = new File(file, "MCreator" + version.get().replace(".", ""));
        File libraries = new File(path, "lib");
        JavaExecAction action = getInjectedExecActionFactory().newJavaExecAction();
        action.classpath(getInjectedObjectFactory().fileTree().from(libraries));
        action.getMainClass().set(extension.getMCreatorMainClass());
        action.jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED");
        action.environment("MCREATOR_PLUGINS_FOLDER", jarOutputs);
        action.execute();
    }

    @Inject
    protected ObjectFactory getInjectedObjectFactory() {
        throw new UnsupportedOperationException();
    }

    public RegularFileProperty getPath2MCreator() {
        return path2MCreator;
    }

    public Property<String> getVersion() {
        return version;
    }

    @Inject
    protected ExecActionFactory getInjectedExecActionFactory() {
        throw new UnsupportedOperationException();
    }
}
