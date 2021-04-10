CREATE TABLE events (
    id              SERIAL PRIMARY KEY,
    event_date      TIMESTAMP,
    event_type      VARCHAR(255) NOT NULL,
    user_id         UUID REFERENCES users(id),
    payload         JSON
)