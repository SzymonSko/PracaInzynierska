package com.example.inzynierka;

public class ScoreHistory {
    public String data, score, windSpeed, gun, imageUrl;
    public ScoreHistory(){};
    public ScoreHistory(String TextData, String TextScore, String TextwindSpeed, String TextGun, String imageUrl){

        this.data = TextData;
        this.score = TextScore;
        this.windSpeed= TextwindSpeed;
        this.gun = TextGun;
        this.imageUrl = imageUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getGun() {
        return gun;
    }

    public void setGun(String gun) {
        this.gun = gun;
    }

    public String getImageUrl(){return imageUrl;}

    public void  setImageUrl(String imageUrl){this.imageUrl = imageUrl;}

}
