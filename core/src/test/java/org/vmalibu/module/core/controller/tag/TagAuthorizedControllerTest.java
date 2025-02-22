package org.vmalibu.module.core.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.module.core.WebMvcTestConfiguration;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.core.service.tag.TagService;
import org.vmalibu.module.core.utils.HexColorUtils;
import org.vmalibu.modules.web.advice.ExceptionControllerAdvice;
import org.vmalibu.modules.web.advice.JsonResponseBodyAdvice;

import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagAuthorizedController.class)
@ContextConfiguration(classes = {
        TagAuthorizedController.class,
        ExceptionControllerAdvice.class,
        JsonResponseBodyAdvice.class,
        WebMvcTestConfiguration.class
})
class TagAuthorizedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    @DisplayName("Test Case: Performing REST request to create tag when name is null. Awaiting error")
    void createWhenNameIsNullTest() throws Exception {
        String hexColor = "09AFBF";

        TagAuthorizedController.CreateTagRequest request = new TagAuthorizedController.CreateTagRequest();
        request.setName(null);
        request.setHexColor(hexColor);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        mockMvc.perform(
                        post(path("/create"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                ).andExpect(status().is4xxClientError());

        verify(tagService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Test Case: Performing REST request to create tag when name is empty. Awaiting error")
    void createWhenNameIsEmptyTest() throws Exception {
        String hexColor = "09AFBF";

        TagAuthorizedController.CreateTagRequest request = new TagAuthorizedController.CreateTagRequest();
        request.setName("   ");
        request.setHexColor(hexColor);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        mockMvc.perform(
                post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(status().is4xxClientError());

        verify(tagService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Test Case: Performing REST request to create tag when hex color is invalid. Awaiting error")
    void createWhenHexColorIsInvalidTest() throws Exception {
        String name = RandomStringUtils.randomAlphabetic(10);
        String hexColor = "ZZ0011";

        TagAuthorizedController.CreateTagRequest request = new TagAuthorizedController.CreateTagRequest();
        request.setName(name);
        request.setHexColor(hexColor);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        mockMvc.perform(
                post(path("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(status().is4xxClientError());

        verify(tagService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Test Case: Performing REST request to create tag. Awaiting new tag")
    void createTest() throws Exception {
        long id = ThreadLocalRandom.current().nextLong();
        String name = RandomStringUtils.randomAlphabetic(10);
        String hexColor = "09AFBF";
        Integer color = HexColorUtils.parse(hexColor);
        Assertions.assertThat(color).isNotNull();

        TagDTO tagDTO = mock(TagDTO.class);
        when(tagDTO.id()).thenReturn(id);
        when(tagDTO.name()).thenReturn(name);
        when(tagDTO.hexColor()).thenReturn(name);
        when(tagService.create(name, color)).thenReturn(tagDTO);

        TagAuthorizedController.CreateTagRequest request = new TagAuthorizedController.CreateTagRequest();
        request.setName(name);
        request.setHexColor(hexColor);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        mockMvc.perform(
                        post(path("/create"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(tagDTO.id()))
                .andExpect(jsonPath("$.data.name").value(tagDTO.name()))
                .andExpect(jsonPath("$.data.hexColor").value(tagDTO.hexColor()));

        verify(tagService, only()).create(ArgumentMatchers.eq(name), ArgumentMatchers.eq(color));
    }

    private static String path(String relPath) {
        return CoreConsts.REST_AUTHORIZED_PREFIX + "/tag" + relPath;
    }
}
