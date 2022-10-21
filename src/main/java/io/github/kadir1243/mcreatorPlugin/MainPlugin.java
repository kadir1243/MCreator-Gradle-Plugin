package io.github.kadir1243.mcreatorPlugin;

import io.github.kadir1243.mcreatorPlugin.internal.DefaultMCreatorExtension;
import io.github.kadir1243.mcreatorPlugin.task.DownloadMCreatorTask;
import io.github.kadir1243.mcreatorPlugin.task.ExtractMCreatorTask;
import io.github.kadir1243.mcreatorPlugin.task.RunMCreatorTask;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.jvm.tasks.Jar;

import java.nio.file.Path;

public class MainPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Fix For IDEA-265203
        System.setProperty("user.dir", project.getProjectDir().toString());

        if (!project.getPluginManager().hasPlugin("java")) {
            project.getPluginManager().apply("java"); // To be sure applied the java plugin
        }

        MCreatorExtension mcreator = project.getExtensions().create(MCreatorExtension.class, "mcreator", DefaultMCreatorExtension.class, project.getObjects());

        Property<String> version = mcreator.getMCreatorVersion();
        Path homeDir = project.getGradle().getGradleUserHomeDir().toPath();
        OperatingSystem os = OperatingSystem.CURRENT;

        try {
            JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
            if (Double.parseDouble(version.get()) > 2022.1) {
                extension.setSourceCompatibility(JavaVersion.VERSION_17);
                extension.setTargetCompatibility(JavaVersion.VERSION_17);
            } else if (Double.parseDouble(version.get()) > 2021.3) {
                extension.setSourceCompatibility(JavaVersion.VERSION_16);
                extension.setTargetCompatibility(JavaVersion.VERSION_16);
            } else {
                extension.setSourceCompatibility(JavaVersion.VERSION_1_8);
                extension.setTargetCompatibility(JavaVersion.VERSION_1_8);
            }
        } catch (Throwable ignored) {}

        String extension;
        switch (os) {
            case LINUX64:
            case LINUX:
                extension = ".tar";
                break;
            case MACOS:
                extension = ".dmg";
                break;
            case WINDOWS:
            case WINDOWS64:
                extension = ".zip";
                break;
            default:
            case UNKNOWN:
                throw new UnsupportedOperationException("Unknown operating system : " + System.getProperty("os.name"));
        }

        Path downloadedMCreatorZip = homeDir.resolve("mcreator").resolve(version.get()).resolve(os.getOsName());
        Path extractionOfMCreator = homeDir.resolve("mcreator").resolve(version.get() + "-extracted").resolve(os.getOsName());

        Jar jar = (Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME);
        jar.getArchiveExtension().set("zip");
        project.getTasks().create("downloadMCreator", DownloadMCreatorTask.class, task -> {
            task.getOutput().set(downloadedMCreatorZip.toFile());
            task.getUrl().set("https://mcreator.net/repository/" + version.get() + "/MCreator%20" + version.get() + "%20" + os.getOsName() + "%20" + os.getArch() + "bit" + extension);
        });
        project.getTasks().create("extractMCreator", ExtractMCreatorTask.class, task -> {
            task.getZipPath().set(downloadedMCreatorZip.toFile());
            task.getDestPath().set(extractionOfMCreator.toFile());
        });
        project.getTasks().create("runMCreator", RunMCreatorTask.class, task -> {
            task.getPath2MCreator().set(extractionOfMCreator.toFile());
            task.getVersion().set(version);
        });
        project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, project.provider(() -> extractionOfMCreator));
    }
}
