package com.example.lotterymgmtapi.repository;

import com.example.lotterymgmtapi.model.LotteryTicket;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * The interface Lottery repository.
 */
public interface LotteryRepository extends MongoRepository<LotteryTicket, String> {

}
