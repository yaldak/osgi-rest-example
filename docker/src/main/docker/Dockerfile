FROM openjdk:8-jre-slim
MAINTAINER Yalda Kako <yalda@kako.cc>

ENV KARAF_USER karaf
ENV KARAF_UID 8181
ENV KARAF_VERSION 4.2.7
ENV JAVA_MAX_MEM 256m
ENV KARAF_EXEC exec

RUN apt-get update && apt-get install -y --no-install-recommends jq curl && rm -rf /var/lib/apt/lists/*

RUN groupadd -r $KARAF_USER --gid=$KARAF_UID && useradd -rm -g $KARAF_USER --uid=$KARAF_UID $KARAF_USER

RUN mkdir -p /opt/karaf

COPY maven/payload.tar.gz /tmp

RUN cd /opt/karaf && tar xfz /tmp/payload.tar.gz --strip-components=1

RUN mkdir -p /opt/karaf/data /opt/karaf/data/log \
    && chown -R $KARAF_USER.$KARAF_USER /opt/karaf \
    && chmod 700 /opt/karaf/data

EXPOSE 8181

USER $KARAF_USER

CMD ["/opt/karaf/bin/karaf", "run"]
