package org.vmalibu.module.core.controller.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.module.core.access.TagManagingPrivilege;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.core.service.tag.TagService;
import org.vmalibu.module.core.utils.HexColorUtils;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;

@RestController
@RequestMapping(CoreConsts.REST_AUTHORIZED_PREFIX + "/tag")
@AllArgsConstructor
public class TagAuthorizedController {

    private final TagService tagService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessPermission(
            values = @PrivilegeAccess(privilege = TagManagingPrivilege.class, ops = AccessOp.WRITE)
    )
    public TagDTO create(
            @RequestBody CreateTagRequest request
    ) throws PlatformException {
       if (!StringUtils.hasText(request.name)) {
           throw GeneralExceptionFactory.buildEmptyValueException(CreateTagRequest.JSON_NAME);
       }

       Integer color = HexColorUtils.parse(request.hexColor);
       if (color == null) {
           throw GeneralExceptionFactory.buildInvalidValueException(CreateTagRequest.JSON_HEX_COLOR);
       }

       return tagService.create(request.name.trim(), color);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AccessPermission(
            values = @PrivilegeAccess(privilege = TagManagingPrivilege.class, ops = AccessOp.DELETE)
    )
    public void remove(@RequestBody Set<Long> ids) {
        tagService.remove(ids);
    }

    @Data
    public static class CreateTagRequest {

        static final String JSON_NAME = "name";
        static final String JSON_HEX_COLOR = "hex_color";

        @JsonProperty(JSON_NAME)
        private String name;

        @JsonProperty(JSON_HEX_COLOR)
        private String hexColor;

    }
}
