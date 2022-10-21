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

        String version = mcreator.getMCreatorVersion().get();
        String buildNumber = mcreator.getMCreatorBuildNumber().get();
        Path homeDir = project.getGradle().getGradleUserHomeDir().toPath();
        OperatingSystem os = OperatingSystem.CURRENT;

        try {
            JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
            if (Double.parseDouble(version) > 2022.1) {
                extension.setSourceCompatibility(JavaVersion.VERSION_17);
                extension.setTargetCompatibility(JavaVersion.VERSION_17);
            } else if (Double.parseDouble(version) > 2021.3) {
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
                extension = ".tar.gz";
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

        Path downloadedMCreatorZip = homeDir.resolve("mcreator").resolve(version).resolve(os.getOsName());
        Path extractionOfMCreator = homeDir.resolve("mcreator").resolve(version + "-extracted").resolve(os.getOsName());

        ((Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME)).getArchiveExtension().set("zip");

        DownloadMCreatorTask downloadMCreatorTask = project.getTasks().create("downloadMCreator", DownloadMCreatorTask.class);
        downloadMCreatorTask.getOutput().set(downloadedMCreatorZip.toFile());
        downloadMCreatorTask.getUrl().set("https://github.com/MCreator/MCreator/releases/download/" + version + "." + buildNumber +"/MCreator." + version + "." + os.getOsName() + "." + os.getArch() + "bit" + extension);

        ExtractMCreatorTask extractMCreatorTask = project.getTasks().create("extractMCreator", ExtractMCreatorTask.class);
        extractMCreatorTask.getZipPath().set(downloadedMCreatorZip.toFile());
        extractMCreatorTask.getDestPath().set(extractionOfMCreator.toFile());

        RunMCreatorTask runMCreatorTask = project.getTasks().create("runMCreator", RunMCreatorTask.class);
        runMCreatorTask.getPath2MCreator().set(extractionOfMCreator.toFile());
        runMCreatorTask.getVersion().set(version);

        project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, project.fileTree(extractionOfMCreator.resolve("MCreator" + version.replace(".", "")).resolve("lib")));
    }
}
