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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trevisgreen.bngcigarlounge.dao.EventDao;
import org.trevisgreen.bngcigarlounge.dao.PartyDao;
import org.trevisgreen.bngcigarlounge.model.Event;
import org.trevisgreen.bngcigarlounge.model.Party;
import org.trevisgreen.bngcigarlounge.service.BaseService;
import org.trevisgreen.bngcigarlounge.service.PartyService;
import org.trevisgreen.bngcigarlounge.utils.NotEnoughSeatsException;

/**
 *
 * @author Trevis
 */
@Service
@Transactional
public class PartyServiceImpl extends BaseService implements PartyService {
    
    @Autowired
    private PartyDao partyDao;
    @Autowired
    private EventDao eventDao;
    
    @Override
    public Party create(Party party) throws NotEnoughSeatsException {
        Event event = eventDao.get(party.getEvent().getId());
        if (event.getSeats() > 0 ) {
            Integer allotedSeats = partyDao.getAllotedSeats(party);
            log.debug("AllotedSeats: {} | {} | {}", new Object[] {allotedSeats, party.getSeats(), event.getSeats()});
            if ((allotedSeats + party.getSeats()) > event.getSeats()) {
                throw new NotEnoughSeatsException("There's only " + (event.getSeats() - allotedSeats) + " place(s) available.");
            }
        }
        party = partyDao.create(party);
        return party;
    }
}
