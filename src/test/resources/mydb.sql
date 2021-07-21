
CREATE TABLE `DEMO_SCHEMA` (
  `id` bigint auto_increment,
  `col_int` int(10) UNSIGNED NOT NULL,
  `col_double` DOUBLE UNSIGNED DEFAULT NULL,
  `col_boolean` BOOLEAN DEFAULT NULL ,
  `col_tinyint` tinyint(1) NOT NULL ,
  `col_enum` enum('default','enum1','enum2') DEFAULT NULL ,
  `col_decimal` decimal(20,3) DEFAULT 0.000 ,
  `col_varchar` varchar(50) NOT NULL ,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
