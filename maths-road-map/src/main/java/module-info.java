module org.vmalibu.module.mathsroadmap {

    requires static lombok;
    requires org.checkerframework.checker.qual;
    requires org.slf4j;

    requires org.vmalibu.modules;
    requires org.vmalibu.module.security;

    requires com.google.common;
    requires org.springdoc.openapi.webmvc.core;
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

    opens org.vmalibu.module.mathsroadmap to spring.core;
    opens org.vmalibu.module.mathsroadmap.database.domainobject to org.hibernate.orm.core, spring.core, spring.beans;
    opens org.vmalibu.module.mathsroadmap.service.topic to spring.core;
    opens org.vmalibu.module.mathsroadmap.database.dao to spring.data.commons;

    exports org.vmalibu.module.mathsroadmap;
    exports org.vmalibu.module.mathsroadmap.database.dao;
    exports org.vmalibu.module.mathsroadmap.database.domainobject;
    exports org.vmalibu.module.mathsroadmap.service.topic to spring.beans, spring.core, spring.aop;

}