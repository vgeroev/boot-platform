package org.vmalibu.modules.controller.anon.platform;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vmalibu.modules.PlatformConsts;
import org.vmalibu.modules.service.platformmanager.PlatformManager;

import java.util.List;

@RestController
@RequestMapping(PlatformConsts.REST_ANON_PREFIX)
@AllArgsConstructor
public class PlatformController {

    private PlatformManager platformManager;

    @GetMapping("/modules")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getModuleUUIDs() {
        return platformManager.getModules().stream()
                .map(m -> m.getConfig().getUuid())
                .toList();
    }
}
