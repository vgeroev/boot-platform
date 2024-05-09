package org.vmalibu.module.mathsroadmap.exception;

import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;

public class MathsRoadMapExceptionFactory {

    public static final String TOPICS_HAVE_CYCLE_CODE = "articles_have_cycle";
    public static final String INVALID_LATEX_SYNTAX_CODE = "invalid_latex_syntax";
    public static final String NGINX_RELOAD_ERROR_CODE = "nginx_reload_error";

    private MathsRoadMapExceptionFactory() { }

    public static PlatformException buildArticlesHaveCycleException() {
        return new PlatformException(TOPICS_HAVE_CYCLE_CODE);
    }

    public static PlatformException buildInvalidLatexSyntaxException(String cmdOutput) {
        return new PlatformException(INVALID_LATEX_SYNTAX_CODE, Map.of("cmdOutput", cmdOutput));
    }

    public static PlatformException buildNginxReloadErrorException(int exitValue) {
        return new PlatformException(NGINX_RELOAD_ERROR_CODE, Map.of("exitValue", exitValue));
    }
}
