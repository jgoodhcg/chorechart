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

-- :name add-living-situation! :<! :1
-- :doc creates a living situation record given a household id and person id
insert into living_situations
(person_id, household_id)
values (:person_id, :household_id)
returning living_situations.id as living_situation_id

-- :name add-chore! :! :n
-- :doc creates a chore definition given a name, description, and household_id
insert into chores
(chore_name, description, household_id)
values (:chore_name, :description, :household_id)

-- :name add-chart-entry! :! :n
-- :doc creates a chore chart entry given a person_id, chore_id, and timestamp in yyyy-mm-dd
insert into chart
(living_situation_id, chore_id, moment)
values (:living_situation_id, :chore_id, :moment::date)
