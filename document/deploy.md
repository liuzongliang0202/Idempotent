1.将项目打成JAR包，引入到你要使用的服务中
2.在项目中mysql配置数据源和redis数据源
3.在对应数据库中创建idempotent_log表
4.在需要做幂等处理的业务方法上加上注解