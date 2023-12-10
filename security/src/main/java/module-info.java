module org.vmalibu.module.security {

    requires static lombok;

    requires org.vmalibu.modules;
    requires com.google.common;
    requires org.apache.tomcat.embed.core;
    requires org.slf4j;
    requires spring.security.oauth2.core;
    requires spring.security.oauth2.resource.server;
    requires spring.security.oauth2.jose;
    requires com.nimbusds.jose.jwt;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.security.config;
    requires spring.security.crypto;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires spring.webmvc;
    requires jakarta.annotation;
    requires jakarta.persistence;
    requires org.checkerframework.checker.qual;

    exports org.vmalibu.module.security.service.privilege;
    exports org.vmalibu.module.security;
    exports org.vmalibu.module.security.access;
    exports org.vmalibu.module.security.database.converter;
    exports org.vmalibu.module.security.authentication.jwt;
    exports org.vmalibu.module.security.authorization.manager;
    exports org.vmalibu.module.security.authorization.controller;
    exports org.vmalibu.module.security.authorization.controller.privilege;
    exports org.vmalibu.module.security.authorization.source;
    exports org.vmalibu.module.security.configuration to spring.beans, spring.context;
    exports org.vmalibu.module.security.configuration.authorized to spring.beans, spring.core, spring.context;
    exports org.vmalibu.module.security.configuration.anonymous to spring.beans, spring.core, spring.context;

    opens org.vmalibu.module.security.configuration to spring.core;
    opens org.vmalibu.module.security.configuration.authorized to spring.core;
    opens org.vmalibu.module.security.configuration.anonymous to spring.core;
    opens org.vmalibu.module.security.authorization.controller to spring.core;
    opens org.vmalibu.module.security.authorization.manager to spring.core;
}