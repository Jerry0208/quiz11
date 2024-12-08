CREATE TABLE if not exists `quiz` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `published` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists `ques` (
  `quiz_id` int NOT NULL,
  `ques_id` int NOT NULL,
  `ques_name` varchar(45) NOT NULL,
  `type` varchar(20) NOT NULL,
  `required` tinyint DEFAULT '0',
  `options` varchar(800) DEFAULT NULL,
  PRIMARY KEY (`quiz_id`,`ques_id`)
);

CREATE TABLE if not exists `feedback` (
  `quiz_id` int NOT NULL,
  `ques_id` int NOT NULL,
  `answer` varchar(200) DEFAULT NULL,
  `user_name` varchar(45) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `age` int DEFAULT '0',
  `fillin_date` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`quiz_id`,`email`,`ques_id`)
);


