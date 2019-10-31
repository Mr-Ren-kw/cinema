package com.stylefeng.guns.rest.promo;

import com.stylefeng.guns.rest.promo.vo.PromoVO;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

public interface PromotionService {
    PromoVO[] queryAllPromotions();

    PromoVO[] queryPromotionsByCinemaId(Integer cinemaId);

    boolean createOrder(int userId, Integer promoId, Integer amount, String stockLogId) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;

    boolean updatePromoStock(Integer cinemaId);

    Boolean transactionalCreateOrder(int userId, Integer promoId, Integer amount, String stockLogId);

    String initPromoStockLog(Integer promoId, Integer amount);

    String generateToken(int userId, Integer promoId);

}
