FROM debian:10 AS build_stage

RUN apt-get update -y && apt-get install -y curl gnupg \
    && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs \
    && curl -fsSL https://dl.yarnpkg.com/debian/pubkey.gpg | gpg --dearmor -o /usr/share/keyrings/yarn-keyring.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/yarn-keyring.gpg] https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update && apt-get install -y yarn \
    && node --version && yarn --version

WORKDIR /opt/src
ENV JAVA_HOME=/opt/src/docker/jdk

COPY ./ $pwd
RUN ./gradlew clean && ./gradlew bootJar

# Application
FROM debian:10 AS default

WORKDIR /var/lib/boot-platform

COPY --from=build_stage /opt/src/docker/jdk /var/lib/jdk
COPY --from=build_stage /opt/src/build/libs/boot-platform.jar /var/lib/boot-platform/jar/boot-platform.jar
COPY ./docker/install-tl-unx.tar.gz /tmp

ENV PATH=/usr/local/texlive/2024/bin/x86_64-linux:$PATH

RUN apt-get -y update  \
    && apt-get install -y curl \
    && apt-get install -y perl  \
    && cd /tmp \
    && zcat < install-tl-unx.tar.gz | tar xf - \
    && cd install-tl-20240702 \
    && perl ./install-tl --scheme=scheme-medium --no-doc-install --no-interaction \
    # Need to remove this
#    && rm install-tl-unx.tar.gz \
#    && rm install-tl-20240702 \
    && tlmgr install tikz-cd \
    && tlmgr install enumitem \
    && apt-get -y install ghostscript \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

EXPOSE 8078 8078
CMD [ \
    "/var/lib/jdk/bin/java","-XX:+UseG1GC", "-Xmx512m","-server","-jar", "/var/lib/boot-platform/jar/boot-platform.jar", \
    "--springdoc.api-docs.enabled=false" \
    ]
