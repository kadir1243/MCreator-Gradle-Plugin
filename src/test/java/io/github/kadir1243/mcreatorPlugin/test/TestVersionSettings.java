package io.github.kadir1243.mcreatorPlugin.test;

import io.github.kadir1243.mcreatorPlugin.MCreatorExtension;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.Collections;

@Testable
public class TestVersionSettings {
    private Project project;

    @BeforeEach
    public void prepareProject() {
        project = ProjectBuilder.builder().build();
        project.apply(Collections.singletonMap("plugin", "io.github.kadir1243.mcreatorPlugin"));
    }

    @Test
    public void testLatest() {}

    @Test
    public void test20221() {
        MCreatorExtension type = project.getExtensions().getByType(MCreatorExtension.class);
        type.getMCreatorVersion().set("2022.1");
    }
}
