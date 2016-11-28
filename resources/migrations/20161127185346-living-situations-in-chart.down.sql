alter table chart drop column living_situation_id;
--;;
alter table chart add column person_id references people (id);
--;;
alter table chart add column household_id references households (id);
