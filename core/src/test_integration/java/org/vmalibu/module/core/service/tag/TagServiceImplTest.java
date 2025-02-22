package org.vmalibu.module.core.service.tag;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.core.BaseTestClass;
import org.vmalibu.module.core.database.dao.TagDAO;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.core.service.tag.list.TagPagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;
import java.util.concurrent.*;

class TagServiceImplTest extends BaseTestClass {

    @Autowired
    private TagServiceImpl tagService;
    @Autowired
    private TagDAO tagDAO;

    @Test
    @DisplayName("Test Case: Trying to find tag by identifier")
    void findByIdTest() {
        String name = RandomStringUtils.randomAlphabetic(10);
        int color = generateColor();

        DBTag newTag = new DBTag();
        newTag.setName(name);
        newTag.setColor(color);

        DBTag saved = tagDAO.save(newTag);

        //--------------------------------------------------------------------------------------------------------------

        TagDTO found = tagService.findById(saved.getId());
        Assertions.assertThat(found).isNotNull()
                .returns(saved.getId(), TagDTO::id)
                .returns(name, TagDTO::name)
                .returns(Integer.toHexString(color), TagDTO::hexColor);

        //--------------------------------------------------------------------------------------------------------------
        long incorrectId = 12345L;
        TagDTO notFound = tagService.findById(incorrectId);
        Assertions.assertThat(notFound).isNull();
    }

    @Test
    @DisplayName("Test Case: Finding all tags by name.")
    void findAllTest() {
        String name1 = "name1";
        int color1 = generateColor();

        DBTag newTag1 = new DBTag();
        newTag1.setName(name1);
        newTag1.setColor(color1);
        DBTag tag1 = tagDAO.save(newTag1);

        String name2 = "name2";
        int color2 = generateColor();

        DBTag newTag2 = new DBTag();
        newTag2.setName(name2);
        newTag2.setColor(color2);
        DBTag tag2 = tagDAO.save(newTag2);

        String name3 = "different_name";
        int color3 = generateColor();

        DBTag newTag3 = new DBTag();
        newTag3.setName(name3);
        newTag3.setColor(color3);
        DBTag tag3 = tagDAO.save(newTag3);

        //--------------------------------------------------------------------------------------------------------------

        PaginatedDto<TagDTO> result = tagService.findAll(
                new TagPagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("name")
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.getResult())
                .map(TagDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag1.getId()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag2.getId()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag3.getId()));

        result = tagService.findAll(
                new TagPagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText("erent")
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(1);
        Assertions.assertThat(result.getResult())
                .map(TagDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag3.getId()));

        result = tagService.findAll(
                new TagPagingRequest.Builder(0, Integer.MAX_VALUE)
                        .withSearchText(null)
                        .build()
        );
        Assertions.assertThat(result.getTotalCount()).isEqualTo(3);
        Assertions.assertThat(result.getResult())
                .map(TagDTO::id)
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag1.getId()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag2.getId()))
                .anySatisfy(id -> Assertions.assertThat(id).isEqualTo(tag3.getId()));
    }

    @Test
    @DisplayName("Test Case: Create tag with invalid fields. Awaiting PlatformException")
    void createWithInvalidFieldsTest() {
        String name = RandomStringUtils.randomAlphabetic(10);
        int color = generateColor();

        Assertions.assertThatThrownBy(() -> tagService.create("  ", color))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        Assertions.assertThatThrownBy(() -> tagService.create(name, -1234))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.INVALID_VALUE_CODE);

        Assertions.assertThatThrownBy(() -> tagService.create(name, Integer.MAX_VALUE))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.INVALID_VALUE_CODE);
    }

    @Test
    @DisplayName("Test Case: Create tag with not unique name. Awaiting PlatformException")
    void createWithNotUniqueNameTest() throws PlatformException {
        String name = RandomStringUtils.randomAlphabetic(10);
        int color = generateColor();

        tagService.create(name, color);

        Assertions.assertThatThrownBy(() -> tagService.create(name, color))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.NOT_UNIQUE_DOMAIN_OBJECT_CODE)
                .hasMessageContaining(name);
    }

    @Test
    @DisplayName("Test Case: Creating tag. Awaiting correctly persisted entity")
    void createTest() throws PlatformException {
        String name = RandomStringUtils.randomAlphabetic(10);
        int color = generateColor();

        TagDTO tagDTO = tagService.create(name, color);
        Assertions.assertThat(tagDTO).isNotNull()
                .returns(name, TagDTO::name)
                .returns(Integer.toHexString(color), TagDTO::hexColor);

        DBTag tag = tagDAO.findById(tagDTO.id()).orElse(null);
        Assertions.assertThat(tag).isNotNull()
                .returns(tagDTO.id(), DBTag::getId)
                .returns(name, DBTag::getName)
                .returns(Integer.toHexString(color), DBTag::getHexColor);
    }

    @Test
    @DisplayName("Test Case: Removing tags. Awaiting that existing tags with such identifiers were removed.")
    void removeTest() {
        String name1 = "name1";
        int color1 = generateColor();

        DBTag newTag1 = new DBTag();
        newTag1.setName(name1);
        newTag1.setColor(color1);
        DBTag tag1 = tagDAO.save(newTag1);

        String name2 = "name2";
        int color2 = generateColor();

        DBTag newTag2 = new DBTag();
        newTag2.setName(name2);
        newTag2.setColor(color2);
        DBTag tag2 = tagDAO.save(newTag2);

        String name3 = "name3";
        int color3 = generateColor();

        DBTag newTag3 = new DBTag();
        newTag3.setName(name3);
        newTag3.setColor(color3);
        DBTag tag3 = tagDAO.save(newTag3);

        tagService.remove(Set.of(tag1.getId(), 12345L, tag3.getId()));

        Assertions.assertThat(tagDAO.existsById(tag1.getId())).isFalse();
        Assertions.assertThat(tagDAO.existsById(tag2.getId())).isTrue();
        Assertions.assertThat(tagDAO.existsById(tag3.getId())).isFalse();
    }

    private static int generateColor() {
        return ThreadLocalRandom.current().nextInt(10_000);
    }
}
