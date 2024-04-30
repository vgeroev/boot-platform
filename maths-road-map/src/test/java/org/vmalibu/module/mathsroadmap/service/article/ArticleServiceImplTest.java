package org.vmalibu.module.mathsroadmap.service.article;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;
import java.util.function.Consumer;

import static org.vmalibu.module.mathsroadmap.TestUtils.getRandomEnumValue;

class ArticleServiceImplTest extends BaseTestClass {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("Test Case: Creating topic with correct values")
    void createWithCorrectValuesTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String latex = RandomStringUtils.randomAlphabetic(100);
        AbstractionLevel abstractionLevel = getRandomEnumValue(AbstractionLevel.class);

        ArticleDTO topic = articleService.create(
                title,
                latex,
                abstractionLevel,
                Set.of(),
                Set.of()
        );

        Consumer<ArticleDTO> topicChecker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(abstractionLevel, ArticleDTO::abstractionLevel)
                        .returns(title, ArticleDTO::title);

        topicChecker.accept(topic);
        Assertions.assertThat(articleService.findArticle(topic.id())).isNotNull()
                .satisfies(topicChecker);
    }

    @Test
    @DisplayName("Test Case: Creating 4 topics with circular dependency. Awaiting PlatformException")
    void createMultipleTopicsWithCircularDependencyTest() throws PlatformException {
        AbstractionLevel abstractionLevel = getRandomEnumValue(AbstractionLevel.class);

        ArticleDTO topic1 = articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                abstractionLevel,
                Set.of(),
                Set.of()
        );

        ArticleDTO topic2 = articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                abstractionLevel,
                Set.of(),
                Set.of(topic1.id())
        );

        ArticleDTO topic3 = articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                abstractionLevel,
                Set.of(),
                Set.of(topic2.id())
        );

        Assertions.assertThatThrownBy(() -> articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                abstractionLevel,
                Set.of(topic1.id()),
                Set.of(topic3.id())
        )).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.TOPICS_HAVE_CYCLE_CODE);
    }


    @Test
    @DisplayName("Test Case: There is no row with such id. Awaiting that method findTopic will return null")
    void findTopicWhenThereIsNoRowWithSuchIdTest() throws PlatformException {
        long badId = RandomUtils.nextLong(10, Long.MAX_VALUE);
        Assertions.assertThat(articleService.findArticle(badId)).isNull();

        articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                getRandomEnumValue(AbstractionLevel.class),
                Set.of(),
                Set.of()
        );

        Assertions.assertThat(articleService.findArticle(badId)).isNull();
    }

}
