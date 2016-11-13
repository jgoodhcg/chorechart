-- :name add-person! :! :n
-- :doc creates a new person record
insert into people
(user_name, email, pass)
values (:user_name, :email, :password)


-- :name find-person :? :1
-- :doc retrieves a person given user_name record
select *
from people
where user_name = :user_name
