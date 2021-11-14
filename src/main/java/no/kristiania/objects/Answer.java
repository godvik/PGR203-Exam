package no.kristiania.objects;

public class Answer {
    private long id;
    private long questionnaire;
    private long question;
    private long option;
    private String value;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setQuestionnaire(long questionnaire) {
        this.questionnaire = questionnaire;
    }

    public long getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public long getQuestion() {
        return question;
    }

    public void setOption(long option) {
        this.option = option;
    }

    public long getOption() {
        return option;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
