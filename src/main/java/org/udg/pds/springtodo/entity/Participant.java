package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Participant implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Participant() {
        this.messages = new ArrayList<>();
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_user")
    private User user;

    @Column(name = "fk_user", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_chat")
    private Chat chat;

    @Column(name = "fk_chat", insertable = false, updatable = false)
    private Long chatId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant")
    private Collection<Message> messages;

    public void setUser(User user) {
        this.user = user;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @JsonView(Views.Public.class)
    @JsonProperty("participantId")
    public Long getId() {
        return id;
    }

    @JsonView(Views.Public.class)
    public User getUser() {
        return user;
    }

    @JsonView(Views.Complete.class)
    public long getUserId() {
        return userId;
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
