package io.github.kadir1243.mcreatorPlugin;

import io.github.kadir1243.mcreatorPlugin.internal.DefaultMCreatorExtension;
import io.github.kadir1243.mcreatorPlugin.task.DownloadMCreatorTask;
import io.github.kadir1243.mcreatorPlugin.task.ExtractMCreatorTask;
import io.github.kadir1243.mcreatorPlugin.task.RunMCreatorTask;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.tasks.Jar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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

        String versionedBuildNumber = mcreator.getMCreatorVersion().get();
        int v = versionedBuildNumber.lastIndexOf('.');
        if (v == -1) {
            throw new UnsupportedOperationException("No Build Number");
        }
        String version = versionedBuildNumber.substring(0, v);
        String buildNumber = versionedBuildNumber.substring(v + 1);
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

        Path downloadedMCreatorZip = homeDir.resolve("mcreator").resolve(versionedBuildNumber).resolve(os.getOsName() + extension);
        Path extractionOfMCreator = homeDir.resolve("mcreator").resolve(versionedBuildNumber + "-extracted").resolve(os.getOsName());

        ((Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME)).getArchiveExtension().set("zip");

        project.getTasks().register("downloadMCreator", DownloadMCreatorTask.class, task -> {
            task.setGroup("mcreator");
            task.getOutput().set(downloadedMCreatorZip.toFile());
            task.getUrl().set("https://github.com/MCreator/MCreator/releases/download/" + version + "." + buildNumber +"/MCreator." + version + "." + os.getOsName() + "." + os.getArch() + "bit" + extension);
        });

        project.getTasks().register("extractMCreator", ExtractMCreatorTask.class, task -> {
            task.dependsOn("downloadMCreator");
            task.setGroup("mcreator");
            task.getZipPath().set(downloadedMCreatorZip.toFile());
            task.getDestPath().set(extractionOfMCreator.toFile());
        });

        project.getTasks().register("runMCreator", RunMCreatorTask.class, task -> {
            task.dependsOn("extractMCreator");
            task.setGroup("mcreator");
            task.getPath2MCreator().set(extractionOfMCreator.toFile());
            task.getVersion().set(versionedBuildNumber);
        });

        project.defaultTasks("extractMCreator");
        File[] file = extractionOfMCreator.toFile().listFiles();
        final Path[] file1 = {extractionOfMCreator};
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                if (file != null && file.length != 0) {
                    if (file.length == 1) {
                        file1[0] = file[0].toPath();
                    }
                    Path finalFile = file1[0];
                    project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, project.provider(() -> project.fileTree(project.getLayout().dir(project.provider(() -> finalFile.resolve("lib").toFile())))));
                }
            }
        });
    }

    public static void download(String remotePath, File localPath, Logger logger, boolean logProgress) {
        try {
            URL url = new URL(remotePath);
            URLConnection conn = url.openConnection();
            int size = conn.getContentLength();

            if (size < 0) {
                logger.error("Could not get the file size");
            } else {
                logger.info("File size: " + size);
            }

            BufferedInputStream in = new BufferedInputStream(url.openStream());
            FileOutputStream out = new FileOutputStream(localPath);
            byte[] data = new byte[1024];
            int count;
            double sumCount = 0.0;

            int i;
            int i1 = 0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);

                sumCount += count;
                if (logProgress && size > 0) {
                    i = (int) (sumCount / size * 100.0);
                    if (i != i1)
                        logger.info(i + "% Downloaded");
                    i1 = i;
                }
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.trace("Error Happened when downloading " + remotePath, e);
        }
    }
}
