module org.vmalibu.module.mathsroadmap {

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

    opens org.vmalibu.module.mathsroadmap to spring.core;
    opens org.vmalibu.module.mathsroadmap.database.domainobject to org.hibernate.orm.core, spring.core, spring.beans;
    opens org.vmalibu.module.mathsroadmap.database.dao to spring.data.commons;
    opens org.vmalibu.module.mathsroadmap.controller.article;
    opens org.vmalibu.module.mathsroadmap.service.config;
    opens org.vmalibu.module.mathsroadmap.service.article;
    opens org.vmalibu.module.mathsroadmap.service.article.pagemanager;
    opens org.vmalibu.module.mathsroadmap.service.pdflatexconverter;
    opens org.vmalibu.module.mathsroadmap.service.latexconverter;
    opens org.vmalibu.module.mathsroadmap.service.nginx;

    exports org.vmalibu.module.mathsroadmap;
    exports org.vmalibu.module.mathsroadmap.controller.article;
    exports org.vmalibu.module.mathsroadmap.database.dao;
    exports org.vmalibu.module.mathsroadmap.database.domainobject;
    exports org.vmalibu.module.mathsroadmap.database.converter to spring.beans;
    exports org.vmalibu.module.mathsroadmap.service.config to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.mathsroadmap.service.article to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.mathsroadmap.service.article.pagemanager to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.mathsroadmap.service.pdflatexconverter to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.mathsroadmap.service.latexconverter to spring.beans, spring.core, spring.aop;
    exports org.vmalibu.module.mathsroadmap.service.nginx to spring.beans, spring.core, spring.aop;

}