INSERT INTO `quiz` (`id`,`name`,`description`,`start_date`,`end_date`,`published`)
VALUES
(18,
'問卷名稱',
'問卷敘述',
'2024-12-06',
'2024-12-24',
'1');

-- insert into if not exists
insert into quiz (`id`,`name`,`description`,`start_date`,`end_date`,`published`)
select (0, '測試問卷', '測試敘述:我要寫多一點東西才會有效果', '2024-12-25', '2025-01-30', '1')
where not exists (select 1 from id where id = 0)


