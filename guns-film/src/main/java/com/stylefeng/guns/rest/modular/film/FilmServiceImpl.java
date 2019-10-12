package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.film.FilmService;
import org.springframework.stereotype.Component;

@Service(interfaceClass = FilmService.class)
@Component
public class FilmServiceImpl implements FilmService{

}
