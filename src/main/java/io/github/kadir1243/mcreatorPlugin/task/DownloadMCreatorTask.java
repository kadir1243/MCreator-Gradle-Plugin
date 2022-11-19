package io.github.kadir1243.mcreatorPlugin.task;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import io.github.kadir1243.mcreatorPlugin.MainPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import javax.inject.Inject;
import java.io.File;

@CacheableTask
public class DownloadMCreatorTask extends DefaultTask {
    private final MCreatorExtension extension = getProject().getExtensions().getByType(MCreatorExtension.class);
    @Input
    private final Property<String> url = getInjectedObjectFactory().property(String.class);
    @OutputFile
    private final RegularFileProperty output = getInjectedObjectFactory().fileProperty();

    public DownloadMCreatorTask() {
        getOutputs().upToDateWhen(e -> !extension.forceDownload().get() && output.getAsFile().get().exists());
    }

    @TaskAction
    public void doTask() {
        File file = output.getAsFile().get();
        file.getParentFile().mkdirs();
        String remotePath = this.url.get();
        getLogger().info("Downloading From Url: " + remotePath);
        MainPlugin.download(remotePath, file, getLogger(), extension.logProgressOfDownload().get());
    }

    public RegularFileProperty getOutput() {
        return output;
    }

    public Property<String> getUrl() {
        return url;
    }

    @Inject
    protected ObjectFactory getInjectedObjectFactory() {
        throw new UnsupportedOperationException("Should be injected");
    }
}
