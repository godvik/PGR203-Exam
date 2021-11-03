package no.kristiania.http;

public class Option {
    private String text;
    private String question;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
