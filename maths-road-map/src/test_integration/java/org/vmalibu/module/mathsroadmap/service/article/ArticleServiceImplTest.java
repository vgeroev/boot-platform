package org.vmalibu.module.mathsroadmap.service.article;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleUserLikesDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleUserLikes;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListElement;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticleListTags;
import org.vmalibu.module.mathsroadmap.service.article.list.ArticlePagingRequest;
import org.vmalibu.module.security.authorization.source.AppUserSource;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

class ArticleServiceImplTest extends BaseTestClass {

    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleUserLikesDAO articleUserLikesDAO;
    @Autowired
    private ArticleDAO articleDAO;

    @Test
    @DisplayName("Test Case: Creating article with correct values")
    void createWithCorrectValuesTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String description = RandomStringUtils.randomAlphabetic(10);
        String latex = RandomStringUtils.randomAlphabetic(100);
        String configuration = ThreadLocalRandom.current().nextBoolean() ? RandomStringUtils.randomAlphabetic(100) : null;
        UserDTO user = createUser();

        ArticleDTO article = articleService.create(
                title,
                description,
                latex,
                configuration,
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        Consumer<ArticleDTO> articleChecker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(description, ArticleDTO::description)
                        .returns(title, ArticleDTO::title)
                        .returns(user.id(), ArticleDTO::userId);

        articleChecker.accept(article);
        Assertions.assertThat(articleService.findArticle(article.id())).isNotNull()
                .satisfies(articleChecker);
    }

    @Test
    @DisplayName("Test Case: There is no row with such id. Awaiting that method findArticle will return null")
    void findArticleWhenThereIsNoRowWithSuchIdTest() throws PlatformException {
        long badId = RandomUtils.nextLong(10, Long.MAX_VALUE);
        Assertions.assertThat(articleService.findArticle(badId)).isNull();
        UserDTO user = createUser();

        articleService.create(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(100),
                RandomStringUtils.randomAlphabetic(100),
                RandomStringUtils.randomAlphabetic(100),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
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
        UserDTO user = createUser();

        ArticleDTO articleDTO1 = articleService.create(
                title1,
                description1,
                latex,
                configuration,
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        String title2 = "title2";
        String description2 = "desc2";

        ArticleDTO articleDTO2 = articleService.create(
                title2,
                description2,
                latex,
                configuration,
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        String title3 = "Algebra";
        String description3 = "Algebra desc";

        ArticleDTO articleDTO3 = articleService.create(
                title3,
                description3,
                latex,
                configuration,
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        ArticleListTags result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("title")
                        .build()
        );
        Assertions.assertThat(result.articles().getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.articles().getResult())
                .map(ArticleListElement::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO1.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("desc")
                        .build()
        );
        Assertions.assertThat(result.articles().getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.articles().getResult())
                .map(ArticleListElement::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO1.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText(description2)
                        .build()
        );
        Assertions.assertThat(result.articles().getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.articles().getResult())
                .map(ArticleListElement::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText(title1)
                        .build()
        );
        Assertions.assertThat(result.articles().getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.articles().getResult())
                .map(ArticleListElement::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO2.id()));

        result = articleService.findAll(
                new ArticlePagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("alg")
                        .build()
        );
        Assertions.assertThat(result.articles().getTotalCount()).isEqualTo(1);
        Assertions.assertThat(result.articles().getResult())
                .map(ArticleListElement::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(articleDTO3.id()));
    }

    @Test
    @DisplayName("Test Case: User added a like to an article. Awaiting that likes counter will be incremented")
    void firstLikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.like(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(1, DBArticle::getLikes)
                .returns(0, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNotNull()
                .returns(true, DBArticleUserLikes::getValue);
    }

    @Test
    @DisplayName("Test Case: User added a like to an article if that article already had a like from this user." +
            " Awaiting that likes counter will be incremented and then decremented")
    void secondLikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.like(articleDTO.id(), user.id());
        articleService.like(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(0, DBArticle::getLikes)
                .returns(0, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNull();
    }

    @Test
    @DisplayName("Test Case: User added a dislike to an article. Awaiting that likes counter will be decremented")
    void firstDislikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.dislike(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(0, DBArticle::getLikes)
                .returns(1, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNotNull()
                .returns(false, DBArticleUserLikes::getValue);
    }

    @Test
    @DisplayName("Test Case: User added a like to an article if that article already had a dislike from this user." +
            " Awaiting that likes counter will be decremented and then incremented")
    void secondDislikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.dislike(articleDTO.id(), user.id());
        articleService.dislike(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(0, DBArticle::getLikes)
                .returns(0, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNull();
    }

    @Test
    @DisplayName("Test Case: User added a like to an article and then a dislike." +
            " Awaiting that likes counter will be decremented")
    void likeAndThenDislikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.like(articleDTO.id(), user.id());
        articleService.dislike(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(0, DBArticle::getLikes)
                .returns(1, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNotNull()
                .returns(false, DBArticleUserLikes::getValue);
    }

    @Test
    @DisplayName("Test Case: User added a dislike to an article and then a like." +
            " Awaiting that likes counter will be incremented")
    void dislikeAndThenLikeTest() throws PlatformException {
        UserDTO user = createUser();

        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(user.id(), user.username(), user.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        articleService.dislike(articleDTO.id(), user.id());
        articleService.like(articleDTO.id(), user.id());

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(1, DBArticle::getLikes)
                .returns(0, DBArticle::getDislikes);

        DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), user.id()).orElse(null);
        Assertions.assertThat(articleUserLikes).isNotNull()
                .returns(true, DBArticleUserLikes::getValue);
    }

    @Test
    @DisplayName("Test Case: 2500 users submit like or dislike on article concurrently.")
    void likeMultipleUsers() throws PlatformException {
        int cUsers = 2_500;
        List<UserDTO> users = new ArrayList<>(cUsers);
        for (int i = 0; i < cUsers; i++) {
            UserDTO user = createUser();
            users.add(user);
        }

        UserDTO creator = users.get(0);
        ArticleDTO articleDTO = articleService.create(
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                RandomStringUtils.randomAlphabetic(25),
                new AppUserSource(creator.id(), creator.username(), creator.password(), List.of())
        );

        //--------------------------------------------------------------------------------------------------------------

        ExecutorService executor = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors());
        List<Long> usersWithLike = Collections.synchronizedList(new ArrayList<>());
        List<Long> usersWithDislike = Collections.synchronizedList(new ArrayList<>());
        try {
            List<Callable<Long>> tasks = new ArrayList<>(cUsers);
            for (int i = 0; i < cUsers; i++) {
                UserDTO user = users.get(i);
                boolean like = ThreadLocalRandom.current().nextBoolean();
                tasks.add(() -> {
                    if (like) {
                        usersWithLike.add(user.id());
                        articleService.like(articleDTO.id(), user.id());
                    } else {
                        usersWithDislike.add(user.id());
                        articleService.dislike(articleDTO.id(), user.id());
                    }
                    return user.id();
                });
            }
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } finally {
            executor.shutdown();
        }

        //--------------------------------------------------------------------------------------------------------------

        DBArticle article = articleDAO.findById(articleDTO.id()).orElse(null);
        Assertions.assertThat(article).isNotNull()
                .returns(usersWithLike.size(), DBArticle::getLikes)
                .returns(usersWithDislike.size(), DBArticle::getDislikes);

        for (Long userId : usersWithLike) {
            DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), userId).orElse(null);
            Assertions.assertThat(articleUserLikes).isNotNull()
                    .returns(true, DBArticleUserLikes::getValue);
        }

        for (Long userId : usersWithDislike) {
            DBArticleUserLikes articleUserLikes = articleUserLikesDAO.findByArticleAndUser(article.getId(), userId).orElse(null);
            Assertions.assertThat(articleUserLikes).isNotNull()
                    .returns(false, DBArticleUserLikes::getValue);
        }
    }

    private UserDTO createUser() throws PlatformException {
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        return userService.create(username, password);
    }

}
