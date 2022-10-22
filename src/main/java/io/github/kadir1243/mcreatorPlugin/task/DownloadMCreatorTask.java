package io.github.kadir1243.mcreatorPlugin.task;

import io.github.kadir1243.mcreatorPlugin.MainPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;

@CacheableTask
public class DownloadMCreatorTask extends DefaultTask {
    @Input
    private final Property<String> url = getInjectedObjectFactory().property(String.class);
    @OutputFile
    private final RegularFileProperty output = getInjectedObjectFactory().fileProperty();

    public DownloadMCreatorTask() {
        setGroup("mcreator");
    }

    @TaskAction
    public void doTask() {
        File file = output.getAsFile().get();
        file.getParentFile().mkdirs();
        getLogger().info("Downloading From Url: " + url.getOrElse("unknown"));
        MainPlugin.download(this.url.get(), file, getLogger());
    }

    public RegularFileProperty getOutput() {
        return output;
    }

    public Property<String> getUrl() {
        return url;
    }

    @Inject
    protected ObjectFactory getInjectedObjectFactory() {
        throw new UnsupportedOperationException();
    }
}
