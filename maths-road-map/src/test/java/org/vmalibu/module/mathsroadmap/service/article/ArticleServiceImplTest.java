package org.vmalibu.module.mathsroadmap.service.article;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.TestUtils;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Random;
import java.util.function.Consumer;


class ArticleServiceImplTest extends BaseTestClass {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("Test Case: Creating topic with correct values")
    void createWithCorrectValuesTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String latex = RandomStringUtils.randomAlphabetic(100);
        String configuration = new Random().nextBoolean() ? RandomStringUtils.randomAlphabetic(100) : null;
        AbstractionLevel abstractionLevel = TestUtils.getRandomEnumValue(AbstractionLevel.class);
        String creatorUsername = RandomStringUtils.randomAlphabetic(10);

        ArticleDTO topic = articleService.create(
                title,
                latex,
                configuration,
                abstractionLevel,
                getUserSource(creatorUsername)
        );

        Consumer<ArticleDTO> topicChecker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(abstractionLevel, ArticleDTO::abstractionLevel)
                        .returns(title, ArticleDTO::title)
                        .returns(creatorUsername, ArticleDTO::creatorUsername);

        topicChecker.accept(topic);
        Assertions.assertThat(articleService.findArticle(topic.id())).isNotNull()
                .satisfies(topicChecker);
    }

    @Test
    @DisplayName("Test Case: There is no row with such id. Awaiting that method findTopic will return null")
    void findTopicWhenThereIsNoRowWithSuchIdTest() throws PlatformException {
        long badId = RandomUtils.nextLong(10, Long.MAX_VALUE);
        Assertions.assertThat(articleService.findArticle(badId)).isNull();

        articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                RandomStringUtils.randomAlphabetic(100),
                TestUtils.getRandomEnumValue(AbstractionLevel.class),
                getUserSource(RandomStringUtils.randomAlphabetic(10))
        );

        Assertions.assertThat(articleService.findArticle(badId)).isNull();
    }

}
