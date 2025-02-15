package org.vmalibu.modules.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vmalibu.modules.database.changelog.DatabaseChangelog;
import org.vmalibu.modules.graph.GraphTraverser;
import org.vmalibu.modules.utils.Version;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class LiquibaseConfiguration {

    private final List<DatabaseChangelog> changelogs;

    @Autowired(required = false)
    public LiquibaseConfiguration(List<DatabaseChangelog> changelogs) {
        this.changelogs = changelogs;
    }

    @Bean
    SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);

        Map<Version, List<DatabaseChangelog>> changelogsVersionTree = getChangelogsVersionTree();
        Path changelogFilePath = getChangelogFilePath();
        createMasterChangelogFile(changelogsVersionTree, changelogFilePath);
        liquibase.setChangeLog("file:" + changelogFilePath);

        return liquibase;
    }

    private Map<Version, List<DatabaseChangelog>> getChangelogsVersionTree() {
        Map<Version, List<DatabaseChangelog>> versionLayer = changelogs.stream()
                .collect(Collectors.groupingBy(DatabaseChangelog::getVersion));

        Map<Version, List<DatabaseChangelog>> result = new TreeMap<>();
        for (Map.Entry<Version, List<DatabaseChangelog>> entry : versionLayer.entrySet()) {
            result.put(entry.getKey(), getTreeFromFixedVersion(entry.getValue()));
        }

        return result;
    }

    private List<DatabaseChangelog> getTreeFromFixedVersion(List<DatabaseChangelog> changelogs) {
        return GraphTraverser.simpleDependencyTreeConstructor(
                changelogs,
                (n1, n2) -> n1.getDependencies().contains(n2.getModuleUuid())
        );
    }

    private void createMasterChangelogFile(Map<Version, List<DatabaseChangelog>> changelogs, Path changelogFilePath) {
        List<String> changelogPaths = new ArrayList<>(changelogs.size());
        for (Map.Entry<Version, List<DatabaseChangelog>> entry : changelogs.entrySet()) {
            for (DatabaseChangelog changelog : entry.getValue()) {
                changelogPaths.add(changelog.getPath());
            }
        }

        String masterChangelogContent = createMasterChangelog(changelogPaths);
        try {
            Files.writeString(
                    changelogFilePath,
                    masterChangelogContent
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Path getChangelogFilePath() {
        try {
            Path file = Files.createTempFile("boot-platform-changelog", ".yaml");
            return file.toAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String createMasterChangelog(List<String> changelogPaths) {
        StringBuilder masterChangelog = new StringBuilder();

        masterChangelog.append("databaseChangeLog:").append(System.lineSeparator());
        for (String path : changelogPaths) {
            masterChangelog.append("  - include:")
                    .append(System.lineSeparator())
                    .append("      file: %s".formatted(path))
                    .append(System.lineSeparator());
        }

        return masterChangelog.toString();
    }

}
