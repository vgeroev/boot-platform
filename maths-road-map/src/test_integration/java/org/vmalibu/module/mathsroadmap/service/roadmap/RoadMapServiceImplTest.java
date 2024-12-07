package org.vmalibu.module.mathsroadmap.service.roadmap;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vmalibu.module.mathsroadmap.BaseTestClass;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleEdge;
import org.vmalibu.module.security.authorization.source.AppUserSource;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;
import java.util.function.Consumer;

class RoadMapServiceImplTest extends BaseTestClass {

    @Autowired
    private RoadMapServiceImpl roadMapService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Test Case: Getting row by invalid id. Awaiting null")
    void findByIdWithInvalidIdTest() {
        Assertions.assertThat(roadMapService.findById(12345L)).isNull();
    }

    @Test
    @DisplayName("Test Case: Creating with invalid fields. Awaiting PlatformException")
    void createWithInvalidFieldsTest() {
        Assertions.assertThatThrownBy(
                () -> roadMapService.create("  ", null, getUserSource(RandomStringUtils.randomAlphabetic(10))))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE)
                .hasMessageContaining(DBRoadMap.DB_TITLE);
    }

    @Test
    @DisplayName("Test Case: Creating domain object. Awaiting created object")
    void createTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String description =  RandomStringUtils.randomAlphabetic(10);
        UserDTO user = createUser();
        UserSource userSource = new AppUserSource(user.id(), user.username(), user.password());

        Consumer<RoadMapDTO> checker = t ->
                Assertions.assertThat(t).isNotNull()
                        .returns(title, RoadMapDTO::title)
                        .returns(description, RoadMapDTO::description)
                        .returns(userSource.getId(), RoadMapDTO::creatorId);

        RoadMapDTO roadMapDTO = roadMapService.create(title, description, userSource);
        checker.accept(roadMapDTO);
        RoadMapDTO savedObject = roadMapService.findById(roadMapDTO.id());
        checker.accept(savedObject);
    }

    @Test
    @DisplayName("Test Case: Getting tree, when road map does not have on. Awaiting empty tree")
    void getTreeWhenThereIsNoTreeForRoadMapTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String description =  RandomStringUtils.randomAlphabetic(10);
        UserDTO user = createUser();
        UserSource userSource = new AppUserSource(user.id(), user.username(), user.password());
        RoadMapDTO roadMapDTO = roadMapService.create(title, description, userSource);

        RoadMapTreeDTO tree = roadMapService.getTree(roadMapDTO.id());
        Assertions.assertThat(tree).isNotNull()
                .returns(roadMapDTO.id(), t -> t.roadMap().id())
                .returns(0, t -> t.articles().size())
                .returns(0, t -> t.tree().size());
    }

    @Test
    @DisplayName("Test Case: Replacing articles tree, when article graph is not a tree. Awaiting PlatformException")
    void replaceTreeWhenArticleGraphIsNotATreeTest() throws PlatformException {
        String title = RandomStringUtils.randomAlphabetic(10);
        String description =  RandomStringUtils.randomAlphabetic(10);
        UserDTO user = createUser();
        UserSource userSource = new AppUserSource(user.id(), user.username(), user.password());
        RoadMapDTO roadMapDTO = roadMapService.create(title, description, userSource);

        Assertions.assertThatThrownBy(() -> {
            roadMapService.replaceTree(
                    roadMapDTO.id(),
                    Set.of(new ArticleEdge(1, 1)),
                    userSource
            );
        }).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.ARTICLE_GRAPH_IS_NOT_A_TREE_CODE);

        Assertions.assertThatThrownBy(() -> {
            roadMapService.replaceTree(
                    roadMapDTO.id(),
                    Set.of(new ArticleEdge(1, 2), new ArticleEdge(2, 1)),
                    userSource
            );
        }).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.ARTICLE_GRAPH_IS_NOT_A_TREE_CODE);

        /*
            3 - 4
         / |     \
        1  |     5
         \ |   /
           2
        */
        Assertions.assertThatThrownBy(() -> {
            roadMapService.replaceTree(
                    roadMapDTO.id(),
                    Set.of(
                            new ArticleEdge(1, 2),
                            new ArticleEdge(1, 3),
                            new ArticleEdge(3, 4),
                            new ArticleEdge(2, 3),
                            new ArticleEdge(4, 5),
                            new ArticleEdge(5, 2)
                    ),
                    userSource
            );
        }).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.ARTICLE_GRAPH_IS_NOT_A_TREE_CODE);

        /*
            3 - 4
         /       \
        1        5
         \     / |
           2   - 6
        */
        Assertions.assertThatThrownBy(() -> {
                    roadMapService.replaceTree(
                            roadMapDTO.id(),
                            Set.of(
                                    new ArticleEdge(1, 2),
                                    new ArticleEdge(1, 3),
                                    new ArticleEdge(3, 4),
                                    new ArticleEdge(2, 5),
                                    new ArticleEdge(4, 5),
                                    new ArticleEdge(5, 6),
                                    new ArticleEdge(6, 2)
                            ),
                            userSource
                    );
                }).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(MathsRoadMapExceptionFactory.ARTICLE_GRAPH_IS_NOT_A_TREE_CODE);
    }

//    @Test
//    @DisplayName("Test Case: Replacing articles tree. Awaiting replaced tree")
//    void replaceTreeTest() throws PlatformException {
//        String title = RandomStringUtils.randomAlphabetic(10);
//        String description =  RandomStringUtils.randomAlphabetic(10);
//        UserSource userSource = getUserSource(RandomStringUtils.randomAlphabetic(10));
//        RoadMapDTO roadMapDTO = roadMapService.create(title, description, userSource);
//
//        // Empty tree
//        RoadMapTreeDTO emptyTree = roadMapService.replaceTree(
//                roadMapDTO.id(),
//                Set.of(),
//                userSource
//        );
//        Assertions.assertThat(emptyTree).isNotNull()
//                .returns(0, t -> t.tree().size())
//                .returns(0, t -> t.articles().size());
//        emptyTree = roadMapService.getTree(roadMapDTO.id());
//
//        Assertions.assertThat(emptyTree).isNotNull()
//                .returns(0, t -> t.tree().size())
//                .returns(0, t -> t.articles().size());
//    }

    private UserDTO createUser() throws PlatformException {
        String username = org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(10);
        String password = org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(10);
        return userService.create(username, password);
    }

}
