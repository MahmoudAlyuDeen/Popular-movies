
package com.afterapps.movies.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

//Generated("org.jsonschema2pojo")
public class MoviesByPerson {

    @SerializedName("cast")
    @Expose
    private List<KnownFor> cast = new ArrayList<>();

    @SerializedName("id")
    @Expose
    private Integer id;

    public List<KnownFor> getCastOf() {
        return cast;
    }

    public void setCastOf(List<KnownFor> cast) {
        this.cast = cast;
    }

    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
