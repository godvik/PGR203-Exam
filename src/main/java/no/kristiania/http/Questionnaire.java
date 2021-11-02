package no.kristiania.http;

public class Questionnaire {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "name='" + name + '\'' +
                '}';
    }
}
