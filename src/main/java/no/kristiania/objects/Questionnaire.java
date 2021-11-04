package no.kristiania.objects;

public class Questionnaire {

    private String name;
    private long id;

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

    public void setID(long id) {
     this.id = id;
    }

    public long getId() {
        return id;
    }
}
