package org.vmalibu.module.mathsroadmap.exception;

import org.vmalibu.modules.module.exception.PlatformException;

import java.util.List;
import java.util.Map;

public class MathsRoadMapExceptionFactory {

    public static final String ARTICLE_GRAPH_IS_NOT_A_TREE_CODE = "article_graph_is_not_a_tree";
    public static final String INVALID_LATEX_SYNTAX_CODE = "invalid_latex_syntax";
    public static final String USER_DOES_NOT_HAVE_ACCESS_CODE = "user_does_not_have_access";

    private MathsRoadMapExceptionFactory() { }

    public static PlatformException buildArticleGraphIsNotATreeException(List<Long> cycle) {
        return new PlatformException(ARTICLE_GRAPH_IS_NOT_A_TREE_CODE);
    }

    public static PlatformException buildInvalidLatexSyntaxException(String cmdOutput) {
        return new PlatformException(INVALID_LATEX_SYNTAX_CODE, Map.of("cmdOutput", cmdOutput));
    }

    public static PlatformException buildUserDoesNotHaveAccessException(String username) {
        return new PlatformException(USER_DOES_NOT_HAVE_ACCESS_CODE, Map.of("username", username));
    }
}
