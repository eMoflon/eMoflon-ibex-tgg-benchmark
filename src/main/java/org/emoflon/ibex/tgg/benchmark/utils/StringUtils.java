package org.emoflon.ibex.tgg.benchmark.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.runner.operationalizations.OperationalizationType;

/**
 * StringUtils
 */
public abstract class StringUtils {

    private static final String regex = "\\{([^}]+?)\\}";
    private static final Pattern pattern = Pattern.compile(regex);

    public static String substituteString(String format, Map<String, String> replacements) {
        Matcher m = pattern.matcher(format);
        String result = format;
        while (m.find()) {
            String found = m.group(1).trim();
            String replacement = replacements.get(found);
            if (replacement != null) {
                result = result.replaceFirst(regex, replacement);
            }
        }

        return result;
    }

    public static Path createPathFromString(String pathTemplate, Path workspacePath, BenchmarkCasePreferences bcp,
            OperationalizationType operationalization) throws InvalidPathException {
        return createPathFromString(pathTemplate, workspacePath, bcp, operationalization, LocalDateTime.now());
    }

    public static Path createPathFromString(String pathTemplate, Path workspacePath, BenchmarkCasePreferences bcp,
            OperationalizationType operationalization, LocalDateTime dateTime) throws InvalidPathException {

        Map<String, String> vars = new HashMap<>();
        vars.put("Y", String.valueOf(dateTime.getYear()));
        vars.put("year", String.valueOf(dateTime.getYear()));
        vars.put("M", String.format("%02d", dateTime.getMonth().getValue()));
        vars.put("month", String.format("%02d", dateTime.getMonth().getValue()));
        vars.put("D", String.format("%02d", dateTime.getDayOfMonth()));
        vars.put("day", String.format("%02d", dateTime.getDayOfMonth()));
        vars.put("h", String.format("%02d", dateTime.getHour()));
        vars.put("hour", String.format("%02d", dateTime.getHour()));
        vars.put("m", String.format("%02d", dateTime.getMinute()));
        vars.put("minute", String.format("%02d", dateTime.getMinute()));
        vars.put("s", String.format("%02d", dateTime.getSecond()));
        vars.put("second", String.format("%02d", dateTime.getSecond()));

        vars.put("date", String.format("%d-%02d-%02d", dateTime.getYear(), dateTime.getMonth().getValue(),
                dateTime.getDayOfMonth()));

        vars.put("workspace_path", workspacePath.toAbsolutePath().toString());
        vars.put("project_path", bcp.getEclipseProject().getProjectPath().toAbsolutePath().toString());
        vars.put("project_name", bcp.getEclipseProject().getName());
        vars.put("benchmark_case_name", bcp.getBenchmarkCaseName());
        vars.put("operationalization", operationalization.toString());

        return Paths.get(substituteString(pathTemplate, vars));
    }
}