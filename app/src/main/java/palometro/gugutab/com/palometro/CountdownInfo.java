package palometro.gugutab.com.palometro;

public class CountdownInfo {
    String name;
    int time;
    int divs;

    public CountdownInfo(String name, int time, int divs) {

        this.name = name;
        this.time = time;
        this.divs = divs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDivs() {
        return divs;
    }

    public void setDivs(int divs) {
        this.divs = divs;
    }


}
