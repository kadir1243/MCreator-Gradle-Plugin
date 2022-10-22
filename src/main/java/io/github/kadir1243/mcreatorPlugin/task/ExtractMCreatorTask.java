package io.github.kadir1243.mcreatorPlugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.*;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.resources.ReadableResource;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;

public class ExtractMCreatorTask extends DefaultTask {
    @InputFile
    private final RegularFileProperty zipPath = getInjectedObjectFactory().fileProperty();
    @OutputDirectory
    private final DirectoryProperty destPath = getInjectedObjectFactory().directoryProperty();

    public ExtractMCreatorTask() {
        dependsOn("downloadMCreator");
        setGroup("mcreator");
    }

    @TaskAction
    public void doTask() {
        File path = zipPath.getAsFile().get();
        File dest = destPath.getAsFile().get();
        dest.getParentFile().mkdirs();
        if (path.getName().endsWith(".zip")) {
            FileTree files = getInjectedArchiveOperations().zipTree(path);
            getInjectedFileSystemOperations().copy(copySpec -> copySpec.from(files).into(dest));
        }
        if (path.getName().endsWith(".gz")) {
            ReadableResource files = getInjectedArchiveOperations().gzip(path);
            getInjectedFileSystemOperations().copy(copySpec -> copySpec.from(files).into(dest));
        }
    }

    public DirectoryProperty getDestPath() {
        return destPath;
    }

    public RegularFileProperty getZipPath() {
        return zipPath;
    }

    @Inject
    protected ObjectFactory getInjectedObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ArchiveOperations getInjectedArchiveOperations() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected FileSystemOperations getInjectedFileSystemOperations() {
        throw new UnsupportedOperationException();
    }
}
