package cn.tedu.ivos.base.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//表示扫描 cn.tedu.ivos.*.mapper下的 Mapper 接口,并自动将其注册为 Spring Bean
@MapperScan("cn.tedu.ivos.*.mapper")
public class MybatisConfig {
}