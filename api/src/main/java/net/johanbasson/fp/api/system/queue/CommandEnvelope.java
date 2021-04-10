package net.johanbasson.fp.api.system.queue;

public record CommandEnvelope(byte[] body, String clazz) {

}
