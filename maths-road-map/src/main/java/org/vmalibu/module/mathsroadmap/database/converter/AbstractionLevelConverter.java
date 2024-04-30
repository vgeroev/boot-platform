package org.vmalibu.module.mathsroadmap.database.converter;

import org.vmalibu.module.mathsroadmap.service.article.AbstractionLevel;
import org.vmalibu.modules.database.converter.BaseEnumConverter;

public class AbstractionLevelConverter extends BaseEnumConverter<AbstractionLevel> {

    public AbstractionLevelConverter() {
        super(AbstractionLevel::from);
    }
}
