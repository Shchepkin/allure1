package ru.yandex.qatools.allure;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.yandex.qatools.allure.io.ReportWriter;
import ru.yandex.qatools.allure.plugins.PluginManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ru.yandex.qatools.allure.AllureUtils.listAttachmentFiles;
import static ru.yandex.qatools.allure.AllureUtils.listFiles;
import static ru.yandex.qatools.allure.utils.DirectoryMatcher.contains;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 19.02.15
 */
public class AllureReportGeneratorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void shouldGenerateWithoutFailures() throws Exception {
        AllureReportGenerator generator = new AllureReportGenerator(Paths.get("target/allure-results"));
        Path outputDirectory = folder.newFolder().toPath();
        generator.generate(outputDirectory);

        Path dataDirectory = outputDirectory.resolve(ReportWriter.DATA_DIRECTORY_NAME);
        assertTrue("Data directory should be created", Files.exists(dataDirectory));

        assertThat(dataDirectory, contains("xunit.json"));
        assertThat(dataDirectory, contains("timeline.json"));
        assertThat(dataDirectory, contains("behaviors.json"));
        assertThat(dataDirectory, contains("defects.json"));
        assertThat(dataDirectory, contains("graph.json"));
        assertThat(dataDirectory, contains(PluginManager.WIDGETS_JSON));
        assertThat(dataDirectory, contains(ReportWriter.REPORT_JSON));

        assertThat(listAttachmentFiles(dataDirectory), hasSize(12));
        assertThat(listFiles("*-testcase.json", dataDirectory), hasSize(320));
    }

    @Test(expected = ReportGenerationException.class)
    public void shouldFailIfNoResults() throws Exception {
        AllureReportGenerator generator = new AllureReportGenerator(folder.newFolder().toPath());
        generator.generate(folder.newFolder().toPath());
    }


    @Test(expected = ReportGenerationException.class)
    public void shouldFailIfNoResultsDirectory() throws Exception {
        AllureReportGenerator generator = new AllureReportGenerator(
                folder.newFolder().toPath().resolve("unknown-directory"));
        generator.generate(folder.newFolder().toPath());
    }
}
