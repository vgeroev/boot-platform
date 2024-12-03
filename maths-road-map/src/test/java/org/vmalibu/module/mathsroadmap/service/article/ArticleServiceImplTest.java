package org.vmalibu.module.mathsroadmap.service.article;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Random;
import java.util.function.Consumer;


class ArticleServiceImplTest extends BaseTestClass {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("Test Case: Creating article with correct values")
    void createWithCorrectValuesTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String description = RandomStringUtils.randomAlphabetic(10);
        String latex = RandomStringUtils.randomAlphabetic(100);
        String configuration = new Random().nextBoolean() ? RandomStringUtils.randomAlphabetic(100) : null;
        String creatorUsername = RandomStringUtils.randomAlphabetic(10);

        ArticleDTO topic = articleService.create(
                title,
                description,
                latex,
                configuration,
                getUserSource(creatorUsername)
        );

        Consumer<ArticleDTO> topicChecker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(description, ArticleDTO::description)
                        .returns(title, ArticleDTO::title)
                        .returns(creatorUsername, ArticleDTO::creatorUsername);

        topicChecker.accept(topic);
        Assertions.assertThat(articleService.findArticle(topic.id())).isNotNull()
                .satisfies(topicChecker);
    }

    @Test
    @DisplayName("Test Case: There is no row with such id. Awaiting that method findArticle will return null")
    void findArticleWhenThereIsNoRowWithSuchIdTest() throws PlatformException {
        long badId = RandomUtils.nextLong(10, Long.MAX_VALUE);
        Assertions.assertThat(articleService.findArticle(badId)).isNull();

        articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                RandomStringUtils.randomAlphabetic(100),
                RandomStringUtils.randomAlphabetic(100),
                getUserSource(RandomStringUtils.randomAlphabetic(10))
        );

        Assertions.assertThat(articleService.findArticle(badId)).isNull();
    }

    @Test
    @DisplayName("Test Case: findAll by title and description.")
    void findAllBySearchTextTest() throws PlatformException {
        String title1 = "title1";
        String description1 = "desc1";
        String latex = RandomStringUtils.randomAlphabetic(10);
        String configuration = new Random().nextBoolean() ? RandomStringUtils.randomAlphabetic(10) : null;
        String creatorUsername = RandomStringUtils.randomAlphabetic(10);

        ArticleDTO articleDTO1 = articleService.create(
                title1,
                description1,
                latex,
                configuration,
                getUserSource(creatorUsername)
        );

        String title2 = "title2";
        String description2 = "desc2";

        ArticleDTO articleDTO2 = articleService.create(
                title2,
                description2,
                latex,
                configuration,
                getUserSource(creatorUsername)
        );

        String title3 = "Algebra";
        String description3 = "Algebra desc";

        ArticleDTO articleDTO3 = articleService.create(
                title3,
                description3,
                latex,
                configuration,
                getUserSource(creatorUsername)
        );

        //--------------------------------------------------------------------------------------------------------------

        PaginatedDto<ArticleDTO> result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("title")
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.getResult())
                .map(ArticleDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO1.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("desc")
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.getResult())
                .map(ArticleDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO1.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText(description2)
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.getResult())
                .map(ArticleDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText(title1)
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.getResult())
                .map(ArticleDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("alg")
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(1);
        Assertions.assertThat(result.getResult())
                .map(ArticleDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));
    }


}
