package io.github.kadir1243.mcreatorPlugin.task;

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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@CacheableTask
public class DownloadMCreatorTask extends DefaultTask {
    @Input
    private final Property<String> url = getInjectedObjectFactory().property(String.class);
    @OutputFile
    private final RegularFileProperty output = getInjectedObjectFactory().fileProperty();

    @TaskAction
    public void doTask() throws IOException {
        File file = output.getAsFile().get();
        file.getParentFile().mkdirs();
        try(InputStream in = new URL(url.get()).openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
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
