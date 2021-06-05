package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Participant;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.ChatRepository;
import org.udg.pds.springtodo.repository.ParticipantRepository;

import java.util.Iterator;
import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    UserService userService;

    public ParticipantRepository crud(){ return participantRepository; }

    public Participant findUserParticipantChat(Long userId,Long chatId){

        try{
            Participant participant = null;
            User u = userService.getUser(userId);
            List<Participant> participating = (List<Participant>) u.getParticipants();
            Iterator<Participant> iParticipant = participating.iterator();
            boolean trobat = false;

            while(iParticipant.hasNext() && !trobat){
                Participant p = iParticipant.next();
                if(p.getChatId()==chatId){
                    participant = p;
                    trobat = true;
                }
            }

            return participant;
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
}
