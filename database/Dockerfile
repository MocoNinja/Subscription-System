FROM postgres:12-alpine

LABEL maintainer="MocoNinja"

ENV POSTGRES_USER "developer"
ENV POSTGRES_PASSWORD "developer"
ENV POSTGRES_DB "SUBSCRIPTIONS"

ADD "./init.sql" "/docker-entrypoint-initdb.d/"
