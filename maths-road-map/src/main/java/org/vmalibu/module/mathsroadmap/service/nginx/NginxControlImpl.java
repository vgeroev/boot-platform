package org.vmalibu.module.mathsroadmap.service.nginx;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.mathsroadmap.service.config.MathsRoadMapConfigService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class NginxControlImpl implements NginxControl {

    private final MathsRoadMapConfigService mathsRoadMapConfigService;

    public void reload() throws PlatformException {
        String reloadCmd = mathsRoadMapConfigService.getNginxConfig().reloadCmd();
        int exitValue = exec(reloadCmd);
        if (exitValue != 0) {
            throw MathsRoadMapExceptionFactory.buildNginxReloadErrorException(exitValue);
        }
    }

    private static int exec(String reloadCmd) throws PlatformException {
        Process process;
        try {
            process = Runtime.getRuntime().exec(reloadCmd);
        } catch (IOException e) {
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }

        try {
            process.waitFor(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        return process.exitValue();
    }

}
