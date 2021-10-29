package no.kristiania.http;

public class Question {
    private String questionniare;
    private String title;
    private String text;

    public String getQuestionniare() {
        return questionniare;
    }

    public void setQuestionniare(String questionniare) {
        this.questionniare = questionniare;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
