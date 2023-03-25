CREATE TABLE meme (
    meme_id serial PRIMARY KEY,
    message_id bigint NOT NULL,
    chat_id bigint NOT NULL,
    publish_date date NOT NULL,
    user_id bigint NOT NULL,
    UNIQUE(message_id, chat_id)
);

CREATE TYPE score_types AS ENUM ('LIKE', 'DISLIKE', 'ACCORDION');

CREATE TABLE meme_score (
    meme_id bigint NOT NULL,
    user_id bigint NOT NULL,
    score score_types NOT NULL,
    PRIMARY KEY(meme_id, user_id),
    CONSTRAINT fk_meme FOREIGN KEY(meme_id) REFERENCES meme(meme_id)
);