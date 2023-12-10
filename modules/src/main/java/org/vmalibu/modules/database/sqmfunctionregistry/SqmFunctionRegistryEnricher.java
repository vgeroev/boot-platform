package org.vmalibu.modules.database.sqmfunctionregistry;

import jakarta.persistence.EntityManager;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.vmalibu.modules.utils.database.DatabaseFunctionNames;

@Configuration
public class SqmFunctionRegistryEnricher implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        NodeBuilder criteriaBuilder = (NodeBuilder) entityManager.getCriteriaBuilder();
        SqmFunctionRegistry sqmFunctionRegistry = criteriaBuilder.getQueryEngine().getSqmFunctionRegistry();
        BasicTypeRegistry basicTypeRegistry = criteriaBuilder.getTypeConfiguration().getBasicTypeRegistry();
        initFunctions(sqmFunctionRegistry, basicTypeRegistry);
    }

    private void initFunctions(SqmFunctionRegistry functionRegistry, BasicTypeRegistry basicTypeRegistry) {
        functionRegistry.registerPattern(
                DatabaseFunctionNames.BIT_AND, "(?1 & ?2)", basicTypeRegistry.resolve(StandardBasicTypes.INTEGER));
        functionRegistry.registerPattern(
                DatabaseFunctionNames.BIT_OR, "(?1 | ?2)", basicTypeRegistry.resolve(StandardBasicTypes.INTEGER));
        functionRegistry.registerPattern(
                DatabaseFunctionNames.BIT_XOR, "(?1 ^ ?2)", basicTypeRegistry.resolve(StandardBasicTypes.INTEGER));
    }
}
