package com.jogjaraya.id.model;

public class ProfileModel {
    private String profil_nama, profil_slug;

    public ProfileModel(String profil_nama, String profil_slug) {
        this.profil_slug = profil_slug;
        this.profil_nama = profil_nama;
    }

    public String getProfil_nama() {
        return profil_nama;
    }

    public void setProfil_nama(String profil_nama) {
        this.profil_nama = profil_nama;
    }

    public String getProfil_slug() {
        return profil_slug;
    }

    public void setProfil_slug(String profil_slug) {
        this.profil_slug = profil_slug;
    }
}
