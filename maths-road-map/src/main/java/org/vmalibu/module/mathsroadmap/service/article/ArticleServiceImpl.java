package org.vmalibu.module.mathsroadmap.service.article;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticleLatex;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;

    @Override
    @Transactional(readOnly = true)
    public @Nullable ArticleDTO findArticle(long id) {
        Optional<DBArticle> oTopic = articleDAO.findById(id);
        return oTopic.map(ArticleDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull ArticleDTO create(@NonNull String title,
                                      @NonNull String latex,
                                      @Nullable String configuration,
                                      @NonNull AbstractionLevel abstractionLevel,
                                      @NonNull UserSource userSource) throws PlatformException {
        checkArticle(title, latex);

        DBArticleLatex articleLatex = new DBArticleLatex();
        articleLatex.setLatex(latex);
        articleLatex.setConfiguration(configuration);

        DBArticle article = new DBArticle();
        article.setArticleLatex(articleLatex);
        article.setCreatorUsername(userSource.getUsername());
        article.setAbstractionLevel(abstractionLevel);
        article.setTitle(title);

        articleDAO.save(article);
        return ArticleDTO.from(article);
    }

    private static void checkArticle(String title,
                                     String latex) throws PlatformException {
        checkTitle(title);
        checkLatex(latex);
    }

    private static void checkTitle(String title) throws PlatformException {
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBArticleLatex.class, DBArticle.Fields.title);
        }
    }

    private static void checkLatex(String body) throws PlatformException {
        if (!StringUtils.hasText(body)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBArticleLatex.class, DBArticleLatex.Fields.latex);
        }
    }

}
