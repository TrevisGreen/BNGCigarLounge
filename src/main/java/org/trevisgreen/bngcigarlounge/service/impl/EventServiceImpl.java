/*
 * The MIT License
 *
 * Copyright 2015 Trevis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.trevisgreen.bngcigarlounge.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trevisgreen.bngcigarlounge.dao.EventDao;
import org.trevisgreen.bngcigarlounge.dao.UserDao;
import org.trevisgreen.bngcigarlounge.model.Event;
import org.trevisgreen.bngcigarlounge.model.Role;
import org.trevisgreen.bngcigarlounge.model.User;
import org.trevisgreen.bngcigarlounge.service.BaseService;
import org.trevisgreen.bngcigarlounge.service.EventService;

/**
 *
 * @author Trevis
 */
@Service
@Transactional
public class EventServiceImpl extends BaseService implements EventService {

    @Autowired
    private EventDao eventDao;
    @Autowired UserDao userDao;
    
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(Map<String, Object> params) {
        return eventDao.list(params);
    }
    
    @Override
    public Event create(Event event) {
        Date date = new Date();
        event.setDateCreated(date);
        event.setLastUpdated(date);
        event.setId(UUID.randomUUID().toString());
        return eventDao.create(event);
    }
    
    @Transactional
    @Override
    public Event get(String eventId) {
        return eventDao.get(eventId);
    }
    

    @Override
    public Event getByCode(String code) {
        return eventDao.getByCode(code);
    }
    
    @Override
    public String delete(String eventId, String name) {
        User user = userDao.get(name);
        boolean isAdmin = false;
        for(Role role : user.getRoles()) {
            if (role.getAuthority().contains("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }
        Event event = eventDao.get(eventId);
        if (isAdmin || event.getUser().equals(user.getId())) {
            eventDao.delete(event);
            return event.getName();
        } else {
            throw new RuntimeException("You can't delete an event that doesn't belong to you.");
        }
    }
    
}
