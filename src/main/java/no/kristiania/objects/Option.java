package no.kristiania.objects;

public class Option {
    private String text;
    private long question;
    private long id;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
