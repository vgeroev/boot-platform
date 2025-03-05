package org.vmalibu.module.mathsroadmap.controller.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.vmalibu.module.core.service.tag.TagDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class ArticleTagResponse {

    public static final String JSON_RESULT = "result";

    @JsonProperty(JSON_RESULT)
    private List<TagDTO> result;

}
