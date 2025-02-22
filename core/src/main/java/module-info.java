module org.vmalibu.module.core {

    requires static lombok;
    requires org.checkerframework.checker.qual;
    requires org.slf4j;

    requires org.vmalibu.modules;
    requires org.vmalibu.module.security;

    requires com.google.common;
    requires spring.beans;
    requires spring.tx;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires spring.webmvc;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.annotation;
    requires spring.security.core;

    opens org.vmalibu.module.core to spring.core;
    opens org.vmalibu.module.core.database.domainobject to org.hibernate.orm.core, spring.core, spring.beans;
    opens org.vmalibu.module.core.database.dao to spring.data.commons;
    opens org.vmalibu.module.core.service.tag;

    exports org.vmalibu.module.core;
    exports org.vmalibu.module.core.controller.tag;
    exports org.vmalibu.module.core.database.dao;
    exports org.vmalibu.module.core.database.domainobject;
    exports org.vmalibu.module.core.database.changelog to spring.beans;
    exports org.vmalibu.module.core.service.tag to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.core.service.tag.list to spring.beans, spring.core, spring.aop;
}