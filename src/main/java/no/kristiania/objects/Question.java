package no.kristiania.objects;

public class Question {
    private long questionnaire;
    private String title;
    private String lowLabel;
    private String highLabel;
    private long id;

    public long getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(long questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getLowLabel() {
        return lowLabel;
    }

    public void setLowLabel(String lowLabel) {
        this.lowLabel = lowLabel;
    }

    public String getHighLabel() {
        return highLabel;
    }

    public void setHighLabel(String highLabel) {
        this.highLabel = highLabel;
    }

}