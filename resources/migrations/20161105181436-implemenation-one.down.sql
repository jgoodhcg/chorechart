drop table chore_chart;
--;;
drop table chores;
--;;
drop table living_situations;
--;;
drop table households
--;;
drop table people;
--;;

CREATE TABLE chart
(id INT PRIMARY KEY,
chore VARCHAR(120),
person VARCHAR(30),
completed VARCHAR(30)
);
--;;

CREATE SEQUENCE chart_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;
--;;


ALTER TABLE chart ALTER COLUMN id SET DEFAULT nextval('chart_id_seq'::regclass);
--;;

ALTER TABLE chart ALTER COLUMN id SET NOT NULL;
--;;

DO '
BEGIN
PERFORM setval(pg_get_serial_sequence(''chart'', ''id''), coalesce(max(id),0) + 1, false) FROM chart;
END; ';
--;;

CREATE TABLE users
(id VARCHAR(20) PRIMARY KEY,
first_name VARCHAR(30),
last_name VARCHAR(30),
email VARCHAR(30),
admin BOOLEAN,
last_login TIME,
is_active BOOLEAN,
pass VARCHAR(300));
