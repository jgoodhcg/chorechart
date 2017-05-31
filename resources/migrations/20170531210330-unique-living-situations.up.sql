alter table living_situations
add constraint unique_living_situation
unique (person_id, household_id);
