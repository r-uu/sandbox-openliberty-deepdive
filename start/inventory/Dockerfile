FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi
#FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="r-uu" \
  org.opencontainers.image.vendor="r-uu.io" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/draft-guide-liberty-deepdive" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="r-uu.io" \
  name="inventory" \
  version="$VERSION-$REVISION" \
  summary="" \
  description="this image contains the inventory microservice running with the Open Liberty runtime."

COPY --chown=1001:0 /src/main/liberty/config /config
COPY --chown=1001:0 target/*.war /config/apps
# copy postgresql libraries
COPY --chown=1001:0 target/liberty/wlp/usr/shared/resources/postgresql-42.4.0.jar /opt/ol/wlp/usr/shared/resources/

RUN features.sh
RUN configure.sh