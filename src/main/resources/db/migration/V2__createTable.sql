CREATE TABLE translations (
  id              SERIAL PRIMARY KEY,
  language_from   TEXT,
  language_to     TEXT,
  original_text   TEXT,
  translated_text TEXT,
  created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);


CREATE TABLE USERS (
  id   SERIAL PRIMARY KEY,
  name TEXT
);

INSERT INTO USERS (name) VALUES ('Hello'), ('My friend');