alter table chart drop column household_id;
--;;
alter table chart drop column person_id;
--;;
alter table chart add column living_situation_id integer references living_situations (id);
