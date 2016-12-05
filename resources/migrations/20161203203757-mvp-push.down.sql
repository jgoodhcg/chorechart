alter table people drop constraint people_email_key;
--;;
alter table people add constraint people_user_name_key unique (user_name);
--;;
alter table households add constraint households_house_name unique (house_name);
