package net.johanbasson.fp.api.config;

public record Configuration(Database database, Server server, RabbitMq rabbitMq) { }
