package com.example.chatapp3;

import java.util.ArrayList;

public class Room {
    private Object odaKurucusu;
    private String odaIsmi;
    private String odaSifresi;
    private String kurulmaTarihi;
    private ArrayList<String> odadakiler;
    Room(Object odaKurucusu, String odaIsmi, String odaSifresi, String kurulmaTarihi, ArrayList<String> odadakiler){
        this.odaKurucusu = odaKurucusu;
        this.odaIsmi = odaIsmi;
        this.odaSifresi = odaSifresi;
        this.kurulmaTarihi = kurulmaTarihi;
        this.odadakiler = odadakiler;
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

    public String getKurulmaTarihi() {
        return kurulmaTarihi;
    }

    public void setKurulmaTarihi(String kurulmaTarihi) {
        this.kurulmaTarihi = kurulmaTarihi;
    }

    public ArrayList<String> getOdadakiler() {
        return odadakiler;
    }

    public void setOdadakiler(ArrayList<String> odadakiler) {
        this.odadakiler = odadakiler;
    }


}
