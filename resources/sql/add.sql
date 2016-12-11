-- :name add-person! :! :n
-- :doc creates a new person record
insert into people
(user_name, email, pass)
values (:user_name, :email, :password)

-- :name add-household! :<! :1
-- :doc creates a new household
insert into households
(house_name)
values (:house_name)
returning households.id as id

-- :name add-roomate! :<! :1
-- :doc creates a new living_situation id given a person's email and the inviting account's living_situation_id returns person's user_name and new living_situation_id
with person
as (select id as person_id
   from people
   where people.email = :roomate_email),
household
as (select households.id as household_id
   from households
   inner join living_situations
   on living_situations.household_id = households.id
   where living_situations.id = :living_situation_id
   group by households.id)
insert into living_situations
(person_id, household_id)
values ((select person_id from person), (select household_id from household))
returning living_situations.id as living_situation_id

-- :name add-living-situation! :<! :1
-- :doc creates a living situation record given a household id and person id
insert into living_situations
(person_id, household_id)
values (:person_id, :household_id)
returning living_situations.id as living_situation_id

-- :name add-chore! :! :n
-- :doc creates a chore given a name, description, and household_id
insert into chores
(chore_name, description, household_id)
values (:chore_name, :description, :household_id)
returning chores.id, chores.chore_name, chores.description

-- :name add-chart-entry! :! :n
-- :doc creates a chore chart entry given a person_id, chore_id, and timestamp in yyyy-mm-dd
insert into chart
(living_situation_id, chore_id, moment)
values (:living_situation_id, :chore_id, :moment::date)
