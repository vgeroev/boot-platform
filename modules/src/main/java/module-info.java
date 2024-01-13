module org.vmalibu.modules {

    requires static lombok;

    requires org.checkerframework.checker.qual;
    requires jdk.unsupported;
//    requires org.jboss.logging;
    requires org.slf4j;
    requires jakarta.xml.bind;
    requires jakarta.cdi;
    requires jakarta.transaction;
    requires jakarta.persistence;
    requires jakarta.annotation;
    requires jakarta.interceptor;
    requires org.apache.tomcat.embed.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.starter.data.jpa;
    requires spring.boot.starter;
    requires spring.boot.starter.aop;
    requires spring.boot.starter.jdbc;
    requires spring.boot.starter.logging;
    requires spring.boot.starter.web;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires spring.webmvc;
    requires spring.aspects;
    requires spring.aop;
    requires spring.tx;
    requires com.zaxxer.hikari;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;
    requires net.bytebuddy;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
//    requires com.fasterxml.classmate;
    requires com.google.common;
    requires org.json;

    opens org.vmalibu.modules.crypto to spring.aop, spring.core;
    opens org.vmalibu.modules.module to spring.aop, spring.core;
    opens org.vmalibu.modules.web.configuration to spring.core;
    opens org.vmalibu.modules.threadpool to spring.core;
    opens org.vmalibu.modules.entrypoint to spring.aop, spring.core;
    opens org.vmalibu.modules.module.settings to spring.beans, spring.context, spring.core;
    opens org.vmalibu.modules.settings to spring.beans, spring.context, spring.core;
    opens org.vmalibu.modules.database.domainobject;
    opens org.vmalibu.modules.database.domainobject.listener;
    opens org.vmalibu.modules.database.sqmfunctionregistry;
    opens org.vmalibu.modules.database.repository to spring.data.commons;
    opens org.vmalibu.modules.web.advice to com.fasterxml.jackson.databind;

    exports org.vmalibu.modules.module;
    exports org.vmalibu.modules.database.domainobject;
    exports org.vmalibu.modules.database.converter;
    exports org.vmalibu.modules.database.domainobject.listener;
    exports org.vmalibu.modules.database.paging;
    exports org.vmalibu.modules.database.repository;
    exports org.vmalibu.modules.module.exception;
    exports org.vmalibu.modules.web.configuration;
    exports org.vmalibu.modules.web.exception.advice;
    exports org.vmalibu.modules.web.advice;
    exports org.vmalibu.modules.threadpool;
    exports org.vmalibu.modules.settings;
    exports org.vmalibu.modules.module.settings;
    exports org.vmalibu.modules.module.settings.rabbit;
    exports org.vmalibu.modules.crypto;
    exports org.vmalibu.modules.utils;
    exports org.vmalibu.modules.utils.function;
    exports org.vmalibu.modules.utils.database;
    exports org.vmalibu.modules.entrypoint;

}