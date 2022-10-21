package io.github.kadir1243.mcreatorPlugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.*;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;

public class ExtractMCreatorTask extends DefaultTask {
    @InputFile
    private final RegularFileProperty zipPath = getInjectedObjectFactory().fileProperty();
    @OutputDirectory
    private final DirectoryProperty destPath = getInjectedObjectFactory().directoryProperty();

    public ExtractMCreatorTask() {
        dependsOn("downloadMCreator");
    }

    @TaskAction
    public void doTask() {
        Path path = zipPath.getAsFile().get().toPath();
        File dest = destPath.get().getAsFile();
        dest.getParentFile().mkdirs();
        if (path.endsWith(".zip")) {
            FileTree files = getInjectedArchiveOperations().zipTree(path);
            getInjectedFileSystemOperations().copy(copySpec -> {
                copySpec.from(files);
                copySpec.into(dest);
            });
        }
        if (path.endsWith(".tar")) {
            FileTree files = getInjectedArchiveOperations().tarTree(path);
            getInjectedFileSystemOperations().copy(copySpec -> {
                copySpec.from(files);
                copySpec.into(dest);
            });
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
