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

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trevisgreen.bngcigarlounge.dao.MessageDao;
import org.trevisgreen.bngcigarlounge.model.Message;
import org.trevisgreen.bngcigarlounge.service.BaseService;
import org.trevisgreen.bngcigarlounge.service.MessageService;

/**
 *
 * @author Trevis
 */
@Service
@Transactional
public class MessageServiceImpl extends BaseService implements MessageService {

    @Autowired
    private MessageDao messageDao;
    
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(Map<String, Object> params) {
        return messageDao.list(params);
    }
    
    @Transactional(readOnly = true)
    @Override
    public Message get(Long messageId) {
        return messageDao.get(messageId);
    }
    
    @Override
    public Message update(Message message) {
        return messageDao.update(message);
    }
    
    @Override
    public Message get(String name) {
        return messageDao.get(name);
    }
}
