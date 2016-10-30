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

