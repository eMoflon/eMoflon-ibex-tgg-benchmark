package org.emoflon.ibex.tgg.benchmark.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;
import org.junit.jupiter.api.Test;;

/**
 * StringUtilsTest
 */
public class StringUtilsTest {

    @Test
    public void substituteString() {
        Map<String, String> vars = new HashMap<>();
        vars.put("foo", "FOO");
        vars.put("bar", "BAR");

        assertEquals("FOO/BAR", StringUtils.substituteString("{foo}/{ bar  }", vars));
    }

    @Test
    public void createPathFromString() {
        Path workspacePath = Paths.get("MyWorkspace").toAbsolutePath();
        Path projectPath = Paths.get("MyProject").toAbsolutePath();
        String projectName = "MyProject";
        OperationalizationType op = OperationalizationType.MODELGEN;
        LocalDateTime dateTime = LocalDateTime.now();

        assertEquals(workspacePath.resolve(String.format("%d-%02d-%02d_%s.xlsx", dateTime.getYear(), dateTime.getMonth().getValue(), dateTime.getDayOfMonth(), projectName)), StringUtils.createPathFromString("{workspace_path}/{date}_{project_name}.xlsx", workspacePath, projectPath, projectName, op));
        assertEquals(projectPath.resolve(String.format("%d-%02d-%02d_report.xlsx", dateTime.getYear(), dateTime.getMonth().getValue(), dateTime.getDayOfMonth())), StringUtils.createPathFromString("{project_path}/{Y}-{M}-{D}_report.xlsx", workspacePath, projectPath, projectName, op));
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            assertThrows(InvalidPathException.class, () -> StringUtils.createPathFromString("$]\\\"", workspacePath, projectPath, projectName, op));
        }
    }
    
}