-- :name edit-household! :! :n
-- :doc changes a household name given new name and living_situation_id for household
update households
set house_name = :new_house_name
from living_situations
where living_situations.household_id = households.id
and living_situations.id = :living_situation_id

-- :name edit-chore! :! :n
-- :doc changes a chore name given new name and chore_id
update chores
set chore_name = :new_chore_name
where id = :chore_id
