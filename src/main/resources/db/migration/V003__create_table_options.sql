CREATE TABLE OPTIONS
(
    Option_id   serial primary key,
    Question_id int references Questions (Question_id) ON DELETE CASCADE,
    Text        varchar not null
);