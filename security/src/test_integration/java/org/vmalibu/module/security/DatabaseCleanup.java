package org.vmalibu.module.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DatabaseCleanup implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> truncateQueries;

    @Transactional
    public void execute() {
        entityManager.flush();
        for (String query : truncateQueries) {
            entityManager.createNativeQuery(query).executeUpdate();
        }
    }

    @Override
    public void afterPropertiesSet() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        List<String> queries = new ArrayList<>();
        for (EntityType<?> entity : entities) {
            Table annotation = entity.getJavaType().getAnnotation(Table.class);
            if (annotation != null) {
                queries.add(getTruncateQuery(annotation.name()));
            }
        }

        this.truncateQueries = queries;
    }

    private static String getTruncateQuery(String tableName) {
        return "TRUNCATE TABLE " + tableName + " CASCADE";
    }
}
