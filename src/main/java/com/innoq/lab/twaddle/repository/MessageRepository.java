package com.innoq.lab.twaddle.repository;

import com.innoq.lab.twaddle.model.Message;
import org.springframework.data.repository.CrudRepository;


public interface MessageRepository extends CrudRepository<Message, Long> {
}