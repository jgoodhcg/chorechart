-- :name find-person :? :1
-- :doc retrieves a person given user_name
select * from people
where email = :email

-- :name find-household :? :1
-- :doc retrieves a household given house_name
select * from households
where house_name = :house_name
