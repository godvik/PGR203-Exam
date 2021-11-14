create table ANSWERS
(
    answer_id serial primary key,
    questionnaire_id int references questionnaires(questionnaire_id) ON DELETE CASCADE ,
    question_id int references questions(question_id) ON DELETE CASCADE ,
    option_id int references options(option_id) ON DELETE CASCADE ,
    answer_value varchar not null
);