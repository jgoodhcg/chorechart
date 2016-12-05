alter table households drop constraint households_house_name_key;
--;;
alter table people drop constraint people_user_name_key;
--;;
alter table people add constraint people_email_key unique (email);
