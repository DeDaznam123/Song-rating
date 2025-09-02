package com.mycompany.irr00_group_project.models.reviewables.targets;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;

/**
 * Represents an artist in the application.
 */
public class Artist extends Reviewable {

    /**
     * Constructor for the Artist class.
     *
     * @param id    The unique identifier for the artist.
     * @param name  The name of the artist.
     * @param image The path to the image file for the artist.
     */
    public Artist(String id, String name, String image) {
        super(id, name, image);
    }

    @Override
    public ReviewableType getType() {
        return ReviewableType.ARTIST;
    }
}
