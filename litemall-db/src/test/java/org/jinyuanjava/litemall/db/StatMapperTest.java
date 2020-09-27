package org.jinyuanjava.litemall.db;

import org.junit.runner.RunWith;
import org.jinyuanjava.litemall.db.dao.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StatMapperTest {

    @Autowired
    private StatMapper statMapper;



}
