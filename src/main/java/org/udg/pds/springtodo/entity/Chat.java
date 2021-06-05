package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.minio.messages.Part;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Entity
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    public Chat() {
        this.messages = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    private Collection<Participant> participants;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    private Collection<Message> messages;

    @JsonView(Views.Public.class)
    @JsonProperty("chatId")
    public Long getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonView(Views.Complete.class)
    public Collection<Message> getMessages() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        messages.size();
        return messages;
    }

    @JsonView(Views.Public.class)
    public Collection<Participant> getParticipants() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        participants.size();
        return participants;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Long participantId){
        List<Participant> newParticipants = new ArrayList<>();

        Iterator<Participant> it = participants.iterator();
        while(it.hasNext()){
            Participant p = it.next();
            if(p.getId()!=participantId)newParticipants.add(p);
        }
        participants = newParticipants;
    }
}
