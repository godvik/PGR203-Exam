create table QUESTIONS
(
    question_id serial primary key,
    questionnaire_id int references questionnaires(questionnaire_id) ON DELETE CASCADE ,
    title varchar not null,
    low_label varchar not null,
    high_label varchar not null
);