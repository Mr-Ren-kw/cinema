package com.stylefeng.guns;

import com.stylefeng.guns.rest.GunsPromoApplication;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GunsPromoApplication.class)
public class GunsRestApplicationTests {
	@Autowired
	PromoService promoService;

	@Test
	public void contextLoads() {
		PromoOrderVo promoOrderVo = new PromoOrderVo();
		promoOrderVo.setPromoId(1);
		promoOrderVo.setAmount(2);
		promoService.createPromoOrder(promoOrderVo, 1);
	}

}
