-- :name add-person! :! :n
-- :doc creates a new person record
insert into people
(user_name, email, pass)
values (:user_name, :email, :password)


-- :name find-person :? :1
-- :doc retrieves a person given user_name 
select * from people
where user_name = :user_name

-- :name add-household! :! :n
-- :doc creates a new household
insert into households
(house_name)
values (:house_name)

-- :name find-household :? :1
-- :doc retrieves a household given house_name
select * from households
where house_name = :house_name

-- :name add-living-situation! :! :n
-- :doc creates a living situation record given a household id and person id
insert into living_situations
(person_id, household_id)
values (:person_id, :household_id)
