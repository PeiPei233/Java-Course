CREATE TABLE IF NOT EXISTS messages
(
    "from"    TEXT    not null,
    "to"      TEXT    not null,
    content   TEXT    not null,
    isRoom    INTEGER not null,
    timestamp INTEGER not null
);