package net.johanbasson.fp.api.system.queue;

public final class EventEnvelope {

    private byte[] body;
    private String clazz;

    public EventEnvelope(byte[] body, String clazz) {
        this.body = body;
        this.clazz = clazz;
    }

    public EventEnvelope() {
    }

    public byte[] getBody() {
        return body;
    }

    public String getClazz() {
        return clazz;
    }
}
