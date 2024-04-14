package org.vmalibu.module.mathsroadmap.service.topic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;
import java.util.function.Consumer;

class TopicServiceImplTest extends BaseTestClass {

    @Autowired
    private TopicService topicService;

    @Test
    @DisplayName("Test Case: Creating topic with correct values")
    void createWithCorrectValuesTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String body = RandomStringUtils.randomAlphabetic(100);
        int easinessLevel = RandomUtils.nextInt();

        TopicDTO topic = topicService.create(
                title,
                body,
                easinessLevel,
                Set.of(),
                Set.of()
        );

        Consumer<TopicDTO> topicChecker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(title, TopicDTO::title)
                        .returns(body, TopicDTO::body)
                        .extracting(TopicDTO::node)
                        .returns(t.id(), TopicNodeDTO::id)
                        .returns(easinessLevel, TopicNodeDTO::easinessLevel)
                        .returns(Set.of(), TopicNodeDTO::prevIds)
                        .returns(Set.of(), TopicNodeDTO::nextIds);

        topicChecker.accept(topic);
        Assertions.assertThat(topicService.findTopic(topic.id())).isNotNull()
                .satisfies(topicChecker);
    }

    @Test
    @DisplayName("Test Case: Creating 4 topics with circular dependency. Awaiting PlatformException")
    void createMultipleTopicsWithCircularDependencyTest() throws PlatformException {
        int easinessLevel = RandomUtils.nextInt();

        TopicDTO topic1 = topicService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                easinessLevel,
                Set.of(),
                Set.of()
        );

        TopicDTO topic2 = topicService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                easinessLevel,
                Set.of(),
                Set.of(topic1.id())
        );

        TopicDTO topic3 = topicService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                easinessLevel,
                Set.of(),
                Set.of(topic2.id())
        );

        Assertions.assertThatThrownBy(() -> topicService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                easinessLevel,
                Set.of(topic1.id()),
                Set.of(topic3.id())
        )).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.TOPICS_HAVE_CYCLE_CODE);
    }


    @Test
    @DisplayName("Test Case: There is no row with such id. Awaiting that method findTopic will return null")
    void findTopicWhenThereIsNoRowWithSuchIdTest() throws PlatformException {
        long badId = RandomUtils.nextLong(10, Long.MAX_VALUE);
        Assertions.assertThat(topicService.findTopic(badId)).isNull();

        topicService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                RandomUtils.nextInt(),
                Set.of(),
                Set.of()
        );

        Assertions.assertThat(topicService.findTopic(badId)).isNull();
    }

    @Test
    @DisplayName("Test Case: Updating topic title")
    void updateTopicTitleTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String body = RandomStringUtils.randomAlphabetic(100);
        int easinessLevel = RandomUtils.nextInt();

        TopicDTO topic = topicService.create(
                title,
                body,
                easinessLevel,
                Set.of(),
                Set.of()
        );

        Assertions.assertThatThrownBy(() ->
                        topicService.updateTopicTitle(topic.id(), "")
                )
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        String newTitle = RandomStringUtils.randomAlphabetic(20);
        topicService.updateTopicTitle(topic.id(), newTitle);

        TopicDTO updatedTopic = topicService.findTopic(topic.id());
        Assertions.assertThat(updatedTopic).isNotNull()
                .returns(newTitle, TopicDTO::title)
                .returns(body, TopicDTO::body)
                .extracting(TopicDTO::node)
                .returns(updatedTopic.id(), TopicNodeDTO::id)
                .returns(easinessLevel, TopicNodeDTO::easinessLevel)
                .returns(Set.of(), TopicNodeDTO::prevIds)
                .returns(Set.of(), TopicNodeDTO::nextIds);
    }

    @Test
    @DisplayName("Test Case: Updating topic body")
    void updateTopicBodyTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String body = RandomStringUtils.randomAlphabetic(100);
        int easinessLevel = RandomUtils.nextInt();

        TopicDTO topic = topicService.create(
                title,
                body,
                easinessLevel,
                Set.of(),
                Set.of()
        );

        Assertions.assertThatThrownBy(() ->
                        topicService.updateTopicBody(topic.id(), "")
                ).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        String newBody = RandomStringUtils.randomAlphabetic(20);
        topicService.updateTopicBody(topic.id(), newBody);

        TopicDTO updatedTopic = topicService.findTopic(topic.id());
        Assertions.assertThat(updatedTopic).isNotNull()
                .returns(title, TopicDTO::title)
                .returns(newBody, TopicDTO::body)
                .extracting(TopicDTO::node)
                .returns(updatedTopic.id(), TopicNodeDTO::id)
                .returns(easinessLevel, TopicNodeDTO::easinessLevel)
                .returns(Set.of(), TopicNodeDTO::prevIds)
                .returns(Set.of(), TopicNodeDTO::nextIds);
    }

}
