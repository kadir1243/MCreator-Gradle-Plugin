package io.github.kadir1243.mcreatorPlugin.test;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class TestVersionSettings {
    private Project project;

    @BeforeEach
    public void prepareProject() {
        project = ProjectBuilder.builder().build();
        project.apply(Collections.singletonMap("plugin", "mcreatorPlugin"));
    }

    @AfterEach
    public void runEvaluate() {
        project.getTasksByName("tasks", false); // https://stackoverflow.com/questions/39532971/how-to-test-afterevaluate-when-writing-gradle-plugin
    }

    @Test
    public void testLatest() {}

    @Test
    public void test20221() {
        MCreatorExtension type = project.getExtensions().getByType(MCreatorExtension.class);
        type.getMCreatorVersion().set("2022.1.20510");
    }
}
