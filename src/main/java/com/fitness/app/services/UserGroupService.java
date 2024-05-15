package com.fitness.app.services;


import com.fitness.app.persistence.entities.Group;
import com.fitness.app.persistence.entities.UserGroup;
import com.fitness.app.persistence.repositories.GroupRepository;
import com.fitness.app.persistence.repositories.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupService {

    @Autowired
    private final UserGroupRepository userGroupRepository;
    @Autowired
    private final GroupRepository groupRepository;


    public void deleteUserGroupById(Long id){
        userGroupRepository.deleteByUserId(id);
    }

    public List<Group> getUserGroupsByUserId(Long userId) {
        // Obtener los UserGroup asociados con el usuario
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        log.info("GRUPOS {}",userGroups);
        // Lista para almacenar la información completa de cada grupo
        List<Group> completeGroups = new ArrayList<>();

        // Obtener la información completa de cada grupo a partir de sus IDs
        for (UserGroup userGroup : userGroups) {
            Long groupId = userGroup.getGroupId();
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group != null) {
                completeGroups.add(group);
            }
        }

        return completeGroups;
    }

    public UserGroup saveUserGroup(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }



    public Long countUsersInGroup(Long groupId) {
        // Implementa la lógica necesaria para contar los usuarios en el grupo
        return userGroupRepository.countByGroupId(groupId);
    }

    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        // Implementa la lógica necesaria para verificar si el usuario pertenece al grupo
        return userGroupRepository.existsByGroupIdAndUserId(groupId, userId);
    }





}
