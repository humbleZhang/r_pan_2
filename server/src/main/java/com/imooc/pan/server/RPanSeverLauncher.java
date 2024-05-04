package com.imooc.pan.server;

import com.imooc.pan.core.constants.RPanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication(scanBasePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@Transactional//事务
@MapperScan(basePackages =   RPanConstants.BASE_COMPONENT_SCAN_PATH+".server.modules.**.mapper")
public class RPanSeverLauncher {

    public static void main(String[] args){
        SpringApplication.run(RPanSeverLauncher.class);
    }



}
