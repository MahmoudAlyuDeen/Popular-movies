
package com.afterapps.movies.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

//Generated("org.jsonschema2pojo")
public class Cast {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<CastMember> castMembers = new ArrayList<>();

    /**
     * 
     * @return
     *     The id
     */
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

    /**
     * 
     * @return
     *     The cast
     */
    public List<CastMember> getCastMembers() {
        return castMembers;
    }

    /**
     * 
     * @param castMembers
     *     The cast
     */
    public void setCastMembers(List<CastMember> castMembers) {
        this.castMembers = castMembers;
    }
}
