package com.faroanalytics.lorange;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Alumni {
    private int userID, positionLat, positionLng;
    private String firstName, lastName, phone, email, job, birthDate, residence, password, picture;

    public Alumni (int userID, String firstName, String lastName, String phone, String email, String job,
                   String birthDate, String residence, String password, int positionLat, int positionLng, String picture) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.job = job;
        this.birthDate = birthDate;
        this.residence = residence;
        this.password = password;
        this.positionLat = positionLat;
        this.positionLng = positionLng;
        this.picture = picture;
    }

    public LatLng getPosition() {
        return new LatLng((double)(positionLat*1.0/1000000), (double)(positionLng*1.0/1000000));
    }

    public String getName() {
        return (firstName + " " + lastName);
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(int positionLat) {
        this.positionLat = positionLat;
    }

    public int getPositionLng() {
        return positionLng;
    }

    public void setPositionLng(int positionLng) {
        this.positionLng = positionLng;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Calendar getBirthday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar =  new GregorianCalendar();
            calendar.setTime(format.parse(this.birthDate));
            Calendar birthday = Calendar.getInstance();
            birthday.set(Calendar.DAY_OF_MONTH, calendar.DAY_OF_MONTH);
            birthday.set(Calendar.MONTH, calendar.MONTH);
            return birthday;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
