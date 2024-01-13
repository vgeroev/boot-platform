module org.vmalibu.module.exercises {

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

    opens org.vmalibu.module.exercises to spring.core;
    opens org.vmalibu.module.exercises.access to org.vmalibu.module.security;
    opens org.vmalibu.module.exercises.database.domainobject to org.hibernate.orm.core, spring.core, spring.beans;
    opens org.vmalibu.module.exercises.service.exercise to spring.core;
    opens org.vmalibu.module.exercises.service.exercise.list to spring.core;
    opens org.vmalibu.module.exercises.service.exercisesource to spring.core;
    opens org.vmalibu.module.exercises.service.exercisesource.list to spring.core;
    opens org.vmalibu.module.exercises.service.exercisesourceaccess to spring.core;
    opens org.vmalibu.module.exercises.service.exercisesourcepublishrequest to spring.core;
    opens org.vmalibu.module.exercises.controller.authorized to com.fasterxml.jackson.databind;
    opens org.vmalibu.module.exercises.database.dao to spring.data.commons;

    exports org.vmalibu.module.exercises;
    exports org.vmalibu.module.exercises.database.dao;
    exports org.vmalibu.module.exercises.database.domainobject;
    exports org.vmalibu.module.exercises.service.exercise to spring.beans, spring.core, spring.aop, com.fasterxml.jackson.databind;
    exports org.vmalibu.module.exercises.service.exercisesource to spring.beans, spring.core, spring.aop, com.fasterxml.jackson.databind;
    exports org.vmalibu.module.exercises.service.exercisesourceaccess to spring.beans, spring.core, spring.aop, com.fasterxml.jackson.databind;
    exports org.vmalibu.module.exercises.service.exercisesourcepublishrequest to spring.beans, spring.core, spring.aop, com.fasterxml.jackson.databind;
    exports org.vmalibu.module.exercises.database.converter to spring.beans;
    exports org.vmalibu.module.exercises.controller.authorized to spring.beans, spring.core, spring.web, com.fasterxml.jackson.databind;
    exports org.vmalibu.module.exercises.service.exercise.list to com.fasterxml.jackson.databind, spring.aop, spring.beans, spring.core;
    exports org.vmalibu.module.exercises.service.exercisesource.list to com.fasterxml.jackson.databind, spring.aop, spring.beans, spring.core;

}