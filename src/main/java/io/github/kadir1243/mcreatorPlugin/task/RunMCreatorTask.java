package io.github.kadir1243.mcreatorPlugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.internal.ExecActionFactory;
import org.gradle.process.internal.JavaExecAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

@CacheableTask
public class RunMCreatorTask extends DefaultTask {
    private final Set<File> jarOutputs;
    @InputDirectory
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
        String mainClass;
        try(JarFile jarFile = new JarFile(new File(libraries, "mcreator.jar"))) {
            mainClass = (String) jarFile.getManifest().getMainAttributes().get("Main-Class");
        } catch (IOException e) {
            mainClass = "net.mcreator.Launcher";
        }
        action.classpath(getInjectedObjectFactory().fileTree().from(libraries));
        action.getMainClass().set(mainClass);
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
