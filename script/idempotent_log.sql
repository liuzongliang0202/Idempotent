CREATE TABLE `idempotent_log` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `transaction_signature` varchar(255) NOT NULL DEFAULT '' COMMENT '业务签名',
                                  `idempotent_param_list` varchar(255) NOT NULL DEFAULT '' COMMENT '幂等参数相关列表',
                                  `idempotent_value_hash` varchar(255) NOT NULL DEFAULT '' COMMENT '幂等参数值的hash值',
                                  `idempotent_value` varchar(255) NOT NULL DEFAULT '' COMMENT '幂等参数的值',
                                  `full_param_value` blob NOT NULL COMMENT '方法全量参数值',
                                  `return_value` blob NOT NULL COMMENT '方法返回值',
                                  `gmt_create` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
                                  `status` tinyint(5) NOT NULL DEFAULT '0' COMMENT '状态',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_class_method` (`transaction_signature`) USING BTREE,
                                  KEY `idx_hash` (`idempotent_value_hash`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;