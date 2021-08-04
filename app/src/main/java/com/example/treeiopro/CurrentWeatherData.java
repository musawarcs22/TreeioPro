
package com.example.treeiopro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentWeatherData {

    private com.example.treeiopro.Coord coord;
    private List<com.example.treeiopro.Weather> weather = null;
    private String base;
    private com.example.treeiopro.Main main;
    private Integer visibility;
    private com.example.treeiopro.Wind wind;
    private com.example.treeiopro.Clouds clouds;
    private Integer dt;
    private com.example.treeiopro.Sys sys;
    private Integer timezone;
    private Integer id;
    private String name;
    private Integer cod;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public com.example.treeiopro.Coord getCoord() {
        return coord;
    }

    public void setCoord(com.example.treeiopro.Coord coord) {
        this.coord = coord;
    }

    public List<com.example.treeiopro.Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<com.example.treeiopro.Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public com.example.treeiopro.Main getMain() {
        return main;
    }

    public void setMain(com.example.treeiopro.Main main) {
        this.main = main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public com.example.treeiopro.Wind getWind() {
        return wind;
    }

    public void setWind(com.example.treeiopro.Wind wind) {
        this.wind = wind;
    }

    public com.example.treeiopro.Clouds getClouds() {
        return clouds;
    }

    public void setClouds(com.example.treeiopro.Clouds clouds) {
        this.clouds = clouds;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public com.example.treeiopro.Sys getSys() {
        return sys;
    }

    public void setSys(com.example.treeiopro.Sys sys) {
        this.sys = sys;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
