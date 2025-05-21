package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData {

    @XmlAttribute(name = "pgroup")
    private String pgroup;

    @XmlAttribute(name = "asin")
    private String asin;

    @XmlAttribute(name = "salesrank")
    private String salesrank;

    @XmlAttribute(name = "picture")
    private String picture;

    @XmlAttribute(name = "detailpage")
    private String detailpage;

    @XmlAttribute(name = "ean")
    private String ean;

    @XmlElement(name = "price")
    private PriceData price;

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "bookspec")
    private BookSpecData bookspec;

    @XmlElement(name = "dvdspec")
    private DVDSpecData dvdspec;

    @XmlElement(name = "musicspec")
    private MusicSpecData musicspec;

    @XmlElementWrapper(name = "labels")
    @XmlElement(name = "label")
    private List<LabelData> labels;

    @XmlElementWrapper(name = "tracks")
    @XmlElement(name = "title")
    private List<String> tracks;

    @XmlElementWrapper(name = "artists")
    @XmlElement(name = "artist")
    private List<ArtistData> artists;

    @XmlElementWrapper(name = "authors")
    @XmlElement(name = "author")
    private List<AuthorData> authors;

    @XmlElementWrapper(name = "directors")
    @XmlElement(name = "director")
    private List<DirectorData> directors;

    @XmlElementWrapper(name = "actors")
    @XmlElement(name = "actor")
    private List<ActorData> actors;
    
    @XmlElementWrapper(name = "creators")
    @XmlElement(name = "creator")
    private List<CreatorData> creators;

    @XmlElementWrapper(name = "publishers")
    @XmlElement(name = "publisher")
    private List<PublisherData> publisher;

    @XmlElement(name = "similars")
    private AehnlichData similars;

    @Override
    public String toString() {
        return "ItemData{" +
                "asin='" + asin + '\'' +
                ", title='" + title + '\'' +
                ", pgroup='" + pgroup + '\'' +
                '}';
    }

}
