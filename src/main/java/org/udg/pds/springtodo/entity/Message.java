package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Message implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Message() {
    }

    public Message(String text, Date date){
        this.date = date;
        this.text = text;
    }

    private String text;

    private Date date;

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_participant")
    private Participant participant;

    @Column(name = "fk_participant", insertable = false, updatable = false)
    private Long participantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_chat")
    private Chat chat;

    @Column(name = "fk_chat", insertable = false, updatable = false)
    private Long chatId;

    @JsonView(Views.Private.class)
    @JsonProperty("messageId")
    public Long getId() {
        return id;
    }

    @JsonView(Views.Private.class)
    public Date getDate() {
        return date;
    }

    @JsonView(Views.Private.class)
    public String getText() {
        return text;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @JsonView(Views.Complete.class)
    public Participant getParticipant() {
        return participant;
    }

    @JsonView(Views.Public.class)
    public Long getParticipantId() {
        return participantId;
    }

    @JsonView(Views.Private.class)
    @JsonProperty("userId")
    public Long getUserId() {
        return participant.getUser().getId();
    }

    @JsonView(Views.Private.class)
    @JsonProperty("participantName")
    public String getParticipantName() {
        return participant.getUser().getFirstName() + " " + participant.getUser().getLastName();
    }

    @JsonView(Views.Private.class)
    @JsonProperty("participantImageUrl")
    public String getParticipantPhoto() {
        return participant.getUser().getImageUrl();
    }

    @JsonView(Views.Complete.class)
    public Chat getChat() {
        return chat;
    }

    @JsonView(Views.Complete.class)
    public long getChatId() {
        return chatId;
    }

}
