package com.example.chatapp3;

public class Room {
    private Object odaKurucusu;
    private String odaIsmi;
    private String odaSifresi;

    Room(Object odaKurucusu, String odaIsmi, String odaSifresi){
        this.odaKurucusu = odaKurucusu;
        this.odaIsmi = odaIsmi;
        this.odaSifresi = odaSifresi;
    }
    public Object getOdaKurucusu() {
        return odaKurucusu;
    }

    public void setOdaKurucusu(User odaKurucusu) {
        this.odaKurucusu = odaKurucusu;
    }

    public String getOdaIsmi() {
        return odaIsmi;
    }

    public void setOdaIsmi(String odaIsmi) {
        this.odaIsmi = odaIsmi;
    }

    public String getOdaSifresi() {
        return odaSifresi;
    }

    public void setOdaSifresi(String odaSifresi) {
        this.odaSifresi = odaSifresi;
    }

}
